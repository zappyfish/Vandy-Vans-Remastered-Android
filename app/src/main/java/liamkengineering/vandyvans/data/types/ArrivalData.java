package liamkengineering.vandyvans.data.types;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import liamkengineering.vandyvans.MainActivity;

/**
 * Created by Liam on 4/13/2018.
 */

public class ArrivalData {

    private final int mMinutes;
    private final String mStopID;
    private final String mRouteID;

    public static String getArrivalRequestURL(String stopID) {
        return "https://www.vandyvans.com/Stop/" + stopID + "/Arrivals";
    }

    // TODO: Remove context after debugging
    public static List<ArrivalData> parseArrivalData(JSONArray jsonArray, Context context) {
        List<ArrivalData> arrivalDataList = new LinkedList<>();
        try {
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONArray curArray = jsonArray.getJSONObject(i).getJSONArray("Arrivals");
                for (int j = 0; j < curArray.length(); ++j) {
                    JSONObject arrival = curArray.getJSONObject(j);
                    String routeID = Integer.toString(arrival.getInt("RouteID"));
                    int minutes = ((int)arrival.getDouble("SecondsToArrival"))/60;
                    String stopID = Integer.toString(arrival.getInt("StopID"));
                    arrivalDataList.add(new ArrivalData(minutes, stopID, routeID));
                }
            }
        } catch (JSONException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }

        return arrivalDataList;
    }

    ArrivalData(int minutes, String stopID, String route) {
        mMinutes = minutes;
        mStopID = stopID;
        mRouteID = route;
    }

    public int getMinutesToArrival() {
        return mMinutes;
    }

    public String getStopID() {
        return mStopID;
    }

    public String getRouteID() {
        return mRouteID;
    }
}
