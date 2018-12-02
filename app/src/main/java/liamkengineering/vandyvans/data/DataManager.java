package liamkengineering.vandyvans.data;

/**
 * Created by Liam on 4/9/2018.
 */

import android.content.Context;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import liamkengineering.vandyvans.data.types.ArrivalData;
import liamkengineering.vandyvans.data.types.ArrivalTimeListener;
import liamkengineering.vandyvans.data.types.InitialDataListener;
import liamkengineering.vandyvans.data.types.Route;
import liamkengineering.vandyvans.data.types.VanLocation;
import liamkengineering.vandyvans.data.types.VanLocationUpdateListener;
import liamkengineering.vandyvans.data.types.VanStop;

/** Singleton class which runs worker threads to obtain latest data
 *  and invokes callbacks upon receipt on the main thread.
 */
public class DataManager {

    public static final String BASE_URL = "https://vandyvan.doublemap.com/map/v2/";

    private static final int POLLING_PERIOD_SECONDS = 10;

    // Request for initial route data for ALL vans
    private static final String ROUTE_DATA_INIT_REQUEST_URL = BASE_URL + "routes";
    private static final String STOP_DATA_INIT_REQUEST_URL = BASE_URL + "stops";
    private static final String VAN_LOCATION_DATA_REQUEST_URL = BASE_URL + "buses";

    private final RequestQueue mRequestQueue;
    private final Handler mPollingHandler;
    private final Runnable mPollerRunnable;

    private final Map<String, VanStop> mStopIDStopMap;
    private final Map<String, Route> mRouteIDRouteMap;
    private final Map<String, Route> mRouteNameRouteMap;

    private VanLocationUpdateListener mVanLocationUpdateListener = new VanLocationUpdateListener() {
        @Override
        public void onVanLocationsUpdate() {

        }
    };

    // Can probably get rid of this after testing.
    private Context mContext;

    private final LocationManager mLocationManager;
    private final MyLocationListener mLocationListener;

    private int mUserXCoordinate;
    private int mUserYCoordinate;

    private static DataManager sInstance;

    public static final synchronized DataManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataManager(context);
        }
        sInstance.mContext = context;
        return sInstance;
    }

    private DataManager(Context context) {
        mContext = context;

        mLocationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = MyLocationListener.getInstance();

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 5000, 10, mLocationListener);
        } catch (SecurityException e) {
            // TODO: handle exception
        }

        mStopIDStopMap = new HashMap<>();
        mRouteIDRouteMap = new HashMap<>();
        mRouteNameRouteMap = new HashMap<>();

        mRequestQueue = Volley.newRequestQueue(context);
        mPollingHandler = new Handler();
        mPollerRunnable = new Runnable() {
            @Override
            public void run() {
                makeVanDataRequest();
                for (VanStop stop : mStopIDStopMap.values()) {
                    makeArrivalRequests();
                }
                mPollingHandler.postDelayed(mPollerRunnable, POLLING_PERIOD_SECONDS * 1000);
            }
        };
    }

    public Collection<VanStop> getStops() {
        return mStopIDStopMap.values();
    }

    public VanStop getStop(String id) {
        return mStopIDStopMap.get(id);
    }

    public Collection<Route> getRoutes() {
        return mRouteIDRouteMap.values();
    }

    public Route getRoute(String id) {
        return mRouteIDRouteMap.get(id);
    }

    public Route getRouteByName(String name) {
        return mRouteNameRouteMap.get(name.toUpperCase());
    }

    public boolean hasUserLocationData() {
        return mLocationListener.hasLocationData();
    }

    public double getUserLongitude() {
        return mLocationListener.getLongitude();
    }

    public double getUserLatitude() {
        return mLocationListener.getLatitude();
    }

    /** Make Volley to get initial information, create vans, and then place in a massive JSON object with
     *  everything collated
     **/
    public void getInitialData(final InitialDataListener onCompletionListener) {
        // Make Volley requests and then callback onCompletionListener
        makeJSONArrayRequest(Request.Method.GET, STOP_DATA_INIT_REQUEST_URL, new JSONUpdateListener() {
            @Override
            public void onError() {
                onCompletionListener.onInitialDataAvailable(false);
            }
            @Override
            public void onJSONObjectUpdate(JSONObject jsonResponse) {

            }

            @Override
            public void onJSONArrayUpdate(JSONArray jsonResponse) {
                // Get stops, then get routes
                for (int i = 0; i < jsonResponse.length(); i++) {
                    try {
                        VanStop stop = VanStop.getVanStopFromJSON(jsonResponse.getJSONObject((i)));
                        mStopIDStopMap.put(stop.getID(), stop);
                    } catch (Exception e) {

                    }
                }
                makeJSONArrayRequest(Request.Method.GET, ROUTE_DATA_INIT_REQUEST_URL, new JSONUpdateListener() {
                    @Override
                    public void onError() {
                        onCompletionListener.onInitialDataAvailable(false);
                    }
                    @Override
                    public void onJSONObjectUpdate(JSONObject jsonResponse) {

                    }

                    @Override
                    public void onJSONArrayUpdate(JSONArray jsonResponse) {
                        for (int i = 0; i < jsonResponse.length(); i++) {
                            try {
                                Route route = Route.getRouteFromJSON(jsonResponse.getJSONObject(i),
                                        mStopIDStopMap);
                                mRouteIDRouteMap.put(route.getID(), route);
                                mRouteNameRouteMap.put(route.getName(), route);
                            } catch (Exception e) {
                                Log.v("route json", e.getMessage());
                            }
                        }
                        mPollingHandler.post(mPollerRunnable);
                        onCompletionListener.onInitialDataAvailable(mRouteNameRouteMap.containsKey("BLACK"));
                    }
                });
            }
        });
    }

    // Can probably get rid of this method, but ya never know what'll happen to the backend after
    // the potential update.
    private void makeJSONObjectRequest(int requestMethod, String requestURL, final JSONUpdateListener listener) {
        JsonObjectRequest request = new JsonObjectRequest(requestMethod, requestURL, null,
            new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    listener.onJSONObjectUpdate(response);
                }
            }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error. How?
                listener.onError();
            }
        });
        mRequestQueue.add(request);
    }

    private void makeJSONArrayRequest(int requestMethod, String requestURL, final JSONUpdateListener listener) {
        JsonArrayRequest request = new JsonArrayRequest(requestMethod, requestURL, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        listener.onJSONArrayUpdate(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error. How?
                listener.onError();
            }
        });
        mRequestQueue.add(request);
    }

    public void setVanLocationListener(VanLocationUpdateListener listener) {
        mVanLocationUpdateListener = listener;
    }

    private void makeArrivalRequest(final String stopID) {
        makeJSONObjectRequest(Request.Method.GET, ArrivalData.getArrivalRequestURL(stopID), new JSONUpdateListener() {
            @Override
            public void onError() {

            }
            @Override
            public void onJSONObjectUpdate(JSONObject jsonResponse) {
                try {
                    JSONObject obj = jsonResponse.getJSONObject("etas");
                    JSONArray stopDataJSON = obj.getJSONObject(stopID).getJSONArray("etas");
                    List<ArrivalData> arrivalData = ArrivalData.parseArrivalData(stopDataJSON,
                            mContext, stopID);
                    for (ArrivalData data : arrivalData) {
                        mRouteIDRouteMap.get(data.getRouteID()).updateArrivalData(data);
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onJSONArrayUpdate(JSONArray jsonResponse) {

            }

        });
    }

    private void makeArrivalRequests() {
        for (String stopID : mStopIDStopMap.keySet()) {
            makeArrivalRequest(stopID);
        }
    }

    private void makeVanDataRequest() {
        makeJSONArrayRequest(Request.Method.GET, VAN_LOCATION_DATA_REQUEST_URL, new JSONUpdateListener() {
            @Override
            public void onError() {

            }
            @Override
            public void onJSONObjectUpdate(JSONObject jsonResponse) {

            }

            @Override
            public void onJSONArrayUpdate(JSONArray jsonResponse) {
                List<VanLocation> locations = VanLocation.getCurrentVanLocations(jsonResponse);
                for (VanLocation location : locations) {
                    mRouteIDRouteMap.get(location.getRoute()).updateVanLocation(location);
                }
                mVanLocationUpdateListener.onVanLocationsUpdate();
            }
        });
    }
}
