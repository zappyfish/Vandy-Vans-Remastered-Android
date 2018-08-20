package liamkengineering.vandyvans.data;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import liamkengineering.vandyvans.data.types.ArrivalData;
import liamkengineering.vandyvans.data.types.VanStop;

public class CalculationManager {

    /*
    private static final String WALKING_DISTANCE_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial";
    private static final String API_KEY = "";

    private final RequestQueue mRequestQueue;
    private final RequestFuture<JSONObject> mFuture;
    private final Context mContext;

    private static CalculationManager sInstance;

    private CalculationManager(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
        mFuture = RequestFuture.newFuture();
        mContext = context;
    }

    public static synchronized CalculationManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CalculationManager(context);
        }
        return sInstance;
    }

    public ArrivalData calculateBestDepartureStop(
            Map<String, Map<String, List<ArrivalData>>> colorStopIDArrivalDataMap,
            Map<String, List<VanStop>> colorVanStopMap,
            VanStop destination) {

        // If we don't know where you are, this functionality won't work
        if (!DataManager.getInstance(mContext).hasUserLocationData()) {
            return null;
        }

        List<String> candidateRoutes = new LinkedList<>();
        for (String color : colorVanStopMap.keySet()) {
            if (hasStop(destination, colorVanStopMap.get(color))) {
                candidateRoutes.add(color);
            }
        }
        ArrivalData bestDeparture;
        for (String candidate: candidateRoutes) {
            Map<String, List<ArrivalData>> stopIDArrivalDataMap = colorStopIDArrivalDataMap.get(candidate);
            List<VanStop> vanStops = colorVanStopMap.get(candidate);
            for (VanStop stop : vanStops) {
                ArrivalData arrivalData = stopIDArrivalDataMap.get(stop.getID());
                if (arrivalData != null) {
                    if (canWalkInTime(arrivalData, stop)) {
                        if (bestDeparture == null) {
                            bestDeparture = arrivalData;
                        } else {
                            bestDeparture = betterArrivalTime(bestDeparture, arrivalData);
                        }
                    }
                }
            }
        }
        return bestDeparture;
    }

    public ArrivalData betterArrivalTime(ArrivalData arrivalOne, ArrivalData arrivalTwo) {
        return arrivalOne.getMinutesToArrival() < arrivalTwo.getMinutesToArrival() ?
                arrivalOne : arrivalTwo;
    }

    private boolean hasStop(VanStop stop, List<VanStop> vanStopList) {
        for (VanStop stop : vanStopList) {
            if (stop.getStopName().equals(stop.getStopName())) {
                return true;
            }
        }
        return false;
    }

    private double calcWalkTime(Context context, VanStop stop) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getRequestURL(stop), reqBody, mFuture, mFuture);

        try {
            JSONObject response = null;
            while (response == null) {
                try {
                    response = mFuture.get(30, TimeUnit.SECONDS); // Block thread, waiting for response, timeout after 30 seconds
                } catch (InterruptedException e) {
                    // Received interrupt signal, but still don't have response
                    // Restore thread's interrupted status to use higher up on the call stack
                    Thread.currentThread().interrupt();
                    // Continue waiting for response (unless you specifically intend to use the interrupt to cancel your request)
                }
            }
            // Do something with response, i.e.

        } catch (Exception e) {
            return -1;
        }
    }

    private boolean canWalkInTime(Context context, ArrivalData data, VanStop stop) {
        return calcWalkTime(context, stop) < data.getMinutesToArrival();
    }

    private String getRequestURL(VanStop stop) {
        String requestURL = WALKING_DISTANCE_URL + "&origins=";
        String start = DataManager.getInstance(mContext).getUserLatitude() + "," +
                DataManager.getInstance(mContext).getUserLongitude();
        String end = stop.getLatitude() + "," + stop.getLongitude();
        requestURL += start + "&destinations=" + end + "&key=" + API_KEY;
        return requestURL;
    }
    */
}