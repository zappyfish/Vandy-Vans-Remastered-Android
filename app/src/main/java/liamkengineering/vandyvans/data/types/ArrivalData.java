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
    private final String mBusID;

    public static String getArrivalRequestURL(String stopID) {
        return "https://vandyvan.doublemap.com/map/v2/eta?stop=" + stopID;
    }

    // TODO: Remove context after debugging
    public static List<ArrivalData> parseArrivalData(JSONArray jsonArray, Context context, String stopID) {
        List<ArrivalData> arrivalDataList = new LinkedList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            try {
                JSONObject arrival = jsonArray.getJSONObject(i);
                String routeID = Integer.toString(arrival.getInt("route"));
                String busID = Integer.toString(arrival.getInt("bus_id"));
                int minutes = ((int)arrival.getDouble("avg"));
                arrivalDataList.add(new ArrivalData(minutes, stopID, routeID, busID));
            } catch (JSONException e) {
                // Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        return arrivalDataList;
    }

    ArrivalData(int minutes, String stopID, String route, String busID) {
        mMinutes = minutes;
        mStopID = stopID;
        mRouteID = route;
        mBusID = busID;
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

    public String getBusID() { return mBusID; }
}
