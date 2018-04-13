package liamkengineering.vandyvans.data.types;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Liam on 4/13/2018.
 */

public class ArrivalData {

    private final int mMinutes;
    private final String mStopID;
    private final String mRouteID;

    static List<ArrivalData> parseArrivalData(JSONArray jsonArray) {
        List<ArrivalData> arrivalDataList = new LinkedList<>();
        try {
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONArray curArray = jsonArray.getJSONArray(i);
                for (int j = 0; j < curArray.length(); ++j) {
                    JSONObject arrival = curArray.getJSONObject(j);
                    String routeID = Integer.toString(arrival.getInt("RouteID"));
                    int minutes = ((int)arrival.getDouble("SecondsToArrival"))/60;
                    String stopID = Integer.toString(arrival.getInt("StopID"));
                    arrivalDataList.add(new ArrivalData(minutes, stopID, routeID));
                }
            }
        } catch (JSONException e) {

        }
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
