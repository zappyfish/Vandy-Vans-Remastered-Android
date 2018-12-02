package liamkengineering.vandyvans.data.types;

/**
 * Created by Liam on 4/10/2018.
 */

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import liamkengineering.vandyvans.MainActivity;

/** Might want to rethink this class since it kinda relies on constant garbage collection */
public class VanLocation {

    private static final String HEADING_KEY = "heading";
    private static final String LATITUDE_KEY = "lat";
    private static final String LONGITUDE_KEY = "lon";
    private static final String ID_KEY = "id";
    private static final String ROUTE_KEY = "route";

    private final double mLatitude;
    private final double mLongitude;
    private final float mHeading;
    private final String mID;
    private final String mRoute;

    // TODO: Remove context parameter after debugging
    public static List<VanLocation> getCurrentVanLocations(JSONArray jsonArray) {
        List<VanLocation> locations = new LinkedList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            try {
                float heading = (float)jsonArray.getJSONObject(i).getDouble(HEADING_KEY);
                String ID = Integer.toString(jsonArray.getJSONObject(i).getInt(ID_KEY));
                double latitude = jsonArray.getJSONObject(i).getDouble(LATITUDE_KEY);
                double longitude = jsonArray.getJSONObject(i).getDouble(LONGITUDE_KEY);
                String route = jsonArray.getJSONObject(i).getString(ROUTE_KEY);
                locations.add(new VanLocation(heading, latitude, longitude, ID, route));
            } catch (JSONException e) {
                // TODO: Handle exception. How?
            }
        }
        return locations;
    }

    private VanLocation(float heading, double latitude, double longitude, String ID, String route) {
        mHeading = heading;
        mLatitude = latitude;
        mLongitude = longitude;
        mID = ID;
        mRoute = route;
    }

    public float getHeading() {
        return mHeading;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public String getID() {
        return mID;
    }

    public String getRoute() { return mRoute; }
}
