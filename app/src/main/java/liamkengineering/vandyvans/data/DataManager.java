package liamkengineering.vandyvans.data;

/**
 * Created by Liam on 4/9/2018.
 */

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import liamkengineering.vandyvans.data.types.ArrivalTimeListener;
import liamkengineering.vandyvans.data.types.InitialData;
import liamkengineering.vandyvans.data.types.InitialDataListener;
import liamkengineering.vandyvans.data.types.Route;
import liamkengineering.vandyvans.data.types.VanLocation;
import liamkengineering.vandyvans.data.types.VanLocationUpdateListener;
import liamkengineering.vandyvans.data.types.VanStop;

/** Singleton class which runs worker threads to obtain latest data
 *  and invokes callbacks upon receipt on the main thread.
 */
public class DataManager {

    public static final String WAYPOINTS_KEY = "waypoints";
    public static final String STOPS_KEY = "stops";

    private static final int POLLING_PERIOD_SECONDS = 2;
    private static final int NUM_VANS = 3;

    // Request for initial route data for ALL vans
    private static final String ROUTE_DATA_INIT_REQUEST_URL = "https://www.vandyvans.com/Region/0/Routes";

    private final RequestQueue mRequestQueue;
    private final Handler mPollingHandler;
    private final Runnable mPollerRunnable;

    private final Van[] mVans;
    // Map is probably unnecessary, but if one day in the future there are 10 vans or something it might be useful
    private final Map<String, Van> mVanColorMap;

    private ArrivalTimeListener mArrivalTimeListener;

    // Can probably get rid of this after testing.
    private Context mContext;

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

        mVans = new Van[NUM_VANS];
        mVanColorMap = new HashMap<>();

        mRequestQueue = Volley.newRequestQueue(context);
        mPollingHandler = new Handler();
        mPollerRunnable = new Runnable() {
            @Override
            public void run() {
                for (Van van : mVans) {
                    if (van.isPolling()) {
                        makeVanDataRequest(van);
                        // makeArrivalRequests
                    }
                }
                mPollingHandler.postDelayed(mPollerRunnable, POLLING_PERIOD_SECONDS * 1000);
            }
        };
    }

    /** Make Volley to get initial information, create vans, and then return a massive JSON object with
     *  everything collated
     **/
    public void getInitialData(final InitialDataListener onCompletionListener) {
        // Make Volley requests and then callback onCompletionListener
        final JSONObject initJSONData = new JSONObject();
        makeJSONArrayRequest(Request.Method.GET, ROUTE_DATA_INIT_REQUEST_URL, new JSONUpdateListener() {
            @Override
            public void onJSONObjectUpdate(JSONObject jsonResponse) {

            }

            @Override
            public void onJSONArrayUpdate(JSONArray jsonResponse) {
                // Get the initial van data
                for (int i = 0; i < NUM_VANS; ++i) {
                    try {
                        JSONObject vanJSON = jsonResponse.getJSONObject(i);
                        String color = vanJSON.getString("ShortName");
                        String routeID = Integer.toString(vanJSON.getInt("ID"));
                        String patternID = Integer.toString(((JSONObject)vanJSON.getJSONArray("Patterns").get(0)).getInt("ID"));
                        Van van = new Van(color, routeID, patternID);
                        mVans[i] = van;
                        mVanColorMap.put(color, van);
                    } catch (JSONException e) {
                        // TODO: Handle exception
                        Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
                mPollingHandler.post(mPollerRunnable);
                makeAllInitialRequests(initJSONData, onCompletionListener);
            }
        });
    }

    public void registerVanLocationListener(String color, VanLocationUpdateListener listener) {
        Van van = mVanColorMap.get(color);
        van.setUpdateListener(listener);
        van.setIsPolling(true);
    }

    private void makeVanDataRequest(final Van van) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, van.getVehicleURL(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                van.getUpdateListener().onVanLocationsUpdate(VanLocation.getCurrentVanLocations(response, mContext));
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error. How?
            }
        });
        mRequestQueue.add(request);
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
            }
        });
        mRequestQueue.add(request);
    }

    /** Might want to set van polling at different points e.g. app is backgrounded, app is foregrounded */
    public void setShouldPollForVanData(Van van, boolean shouldPoll) {
        van.setIsPolling(shouldPoll);
    }

    private void makeAllInitialRequests(final JSONObject finalJson, final InitialDataListener onCompletionListener) {
        try {
            finalJson.put(WAYPOINTS_KEY, new JSONObject());
            // Recursively obtain waypoints for all vans
            makeWaypointRequestsUntilFinished(finalJson, 0, new JSONUpdateListener() {
                @Override
                public void onJSONObjectUpdate(JSONObject jsonResponse) {
                    try {
                        finalJson.put(STOPS_KEY, new JSONObject());
                        // The waypoints were recursively obtained, now recursively obtain the stops
                        makeStopRequestsUntilFinished(finalJson, 0, new JSONUpdateListener() {
                            @Override
                            public void onJSONObjectUpdate(JSONObject jsonResponse) {
                                // Finally, parse the json here and call the onCompletionListener
                                parseInitialDataForCallback(jsonResponse, onCompletionListener);
                            }

                            @Override
                            public void onJSONArrayUpdate(JSONArray jsonResponse) {

                            }
                        });
                    } catch (JSONException e) {
                        // TODO: Handle exception
                    }
                }

                @Override
                public void onJSONArrayUpdate(JSONArray jsonResponse) {

                }
            });
        } catch (JSONException e) {
            // TODO: Handle exception
        }
    }

    private void parseInitialDataForCallback(JSONObject jsonResponse, InitialDataListener onCompletionListener) {

        List<InitialData> initialDataList = new LinkedList<>();
        try {
            JSONObject stopsJSON = jsonResponse.getJSONObject(STOPS_KEY);
            JSONObject waypointsJSON = jsonResponse.getJSONObject(WAYPOINTS_KEY);
            for (Van van : mVans) {
                List<VanStop> vanStops = VanStop.getVanStopsFromJSON(stopsJSON.getJSONArray(van.getColor()));
                Route route = Route.getRouteFromWaypointJSON(waypointsJSON.getJSONArray(van.getColor()));
                initialDataList.add(new InitialData(van.getColor(), vanStops, route));
            }
        } catch (JSONException e) {
            // TODO: Handle exception. How?
        }
        onCompletionListener.onInitialDataAvailable(initialDataList);
    }

    private void makeStopRequestsUntilFinished(final JSONObject finalJson, final int index, final JSONUpdateListener onCompletionListener) {
        // Recursively make calls here until all stops have been obtained
        makeJSONArrayRequest(Request.Method.GET, mVans[index].getStopURL(), new JSONUpdateListener() {
            @Override
            public void onJSONObjectUpdate(JSONObject jsonResponse) {

            }

            @Override
            public void onJSONArrayUpdate(JSONArray jsonResponse) {
                try {
                    finalJson.getJSONObject(STOPS_KEY).put(mVans[index].getColor(), jsonResponse);
                } catch (JSONException e) {
                    // TODO: Handle exception
                }
                if (index + 1 < NUM_VANS) {
                    makeStopRequestsUntilFinished(finalJson, index + 1, onCompletionListener);
                } else {
                    onCompletionListener.onJSONObjectUpdate(finalJson);
                }
            }
        });
    }

    private void makeWaypointRequestsUntilFinished(final JSONObject finalJson, final int index, final JSONUpdateListener onCompletionListener) {
        makeJSONArrayRequest(Request.Method.GET, mVans[index].getWaypointURL(), new JSONUpdateListener() {
            @Override
            public void onJSONObjectUpdate(JSONObject jsonResponse) {

            }

            @Override
            public void onJSONArrayUpdate(JSONArray jsonResponse) {
                try {
                    JSONArray waypointArray = (JSONArray)jsonResponse.get(0);
                    finalJson.getJSONObject(WAYPOINTS_KEY).put(mVans[index].getColor(), waypointArray);
                } catch (JSONException e) {
                    // TODO: Handle exception
                }
                if (index + 1 < NUM_VANS) {
                    makeWaypointRequestsUntilFinished(finalJson, index + 1, onCompletionListener);
                } else {
                    // This is kind of gross and should be abstracted more but oh well
                    onCompletionListener.onJSONObjectUpdate(finalJson);
                }
            }
        });
    }

    public void setArrivalListener(ArrivalTimeListener listener) {
        mArrivalTimeListener = listener;
    }

    private void makeArrivalRequest() {

    }
}
