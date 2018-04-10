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
import java.util.Map;

/** Singleton class which runs worker threads to obtain latest data
 *  and invokes callbacks upon receipt on the main thread.
 */
public class DataManager {

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

    // Can probably get rid of this after testing.
    private final Context mContext;

    private static DataManager sInstance;

    public static final synchronized DataManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataManager(context);
        }
        return sInstance;
    }

    private DataManager(Context context) {
        mContext = context;
        mVans = new Van[3];
        mVanColorMap = new HashMap<>();
        mRequestQueue = Volley.newRequestQueue(context);
        mPollingHandler = new Handler();
        mPollerRunnable = new Runnable() {
            @Override
            public void run() {
                for (Van van : mVans) {
                    if (van.isPolling()) {
                        makeVanDataRequest(van);
                    }
                }
                mPollingHandler.postDelayed(mPollerRunnable, POLLING_PERIOD_SECONDS);
            }
        };
    }

    /** Make Volley to get initial information, create vans, and then return a massive JSON object with
     *  everything collated
     **/
    public void getInitialData(JSONUpdateListener onCompletionListener) {
        // Make Volley request and then callback routeListener
        final JSONObject initJSONData = new JSONObject();
        makeJSONArrayRequest(Request.Method.GET, ROUTE_DATA_INIT_REQUEST_URL, new JSONUpdateListener() {
            @Override
            public void onJSONObjectUpdate(JSONObject jsonResponse) {

            }

            @Override
            public void onJSONArrayUpdate(JSONArray jsonResponse) {
                Toast.makeText(mContext, jsonResponse.toString(), Toast.LENGTH_LONG).show();
                for (int i = 0; i < NUM_VANS; ++i) {
                    try {
                        JSONObject vanJSON = (JSONObject)jsonResponse.get(i);
                        String color = vanJSON.getString("ShortName");
                        String routeID = Integer.toString(vanJSON.getInt("ID"));
                        String patternID = Integer.toString(((JSONObject)vanJSON.getJSONArray("Patterns").get(0)).getInt("ID"));
                        Van van = new Van(color, routeID, patternID);
                        mVans[i] = van;
                        mVanColorMap.put(color, van);
                    } catch (JSONException e) {
                        // TODO: Handle exception
                    }
                }
            }
        });
    }

    private void registerVanDataListener(Van van, JSONUpdateListener listener) {
        van.setUpdateListener(listener);
    }

    void makeVanDataRequest(Van van) {
        makeJSONObjectRequest(Request.Method.GET, van.getVehicleURL(), van.getUpdateListener());
    }

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
}
