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

    private static final String HEADING_KEY = "Heading";
    private static final String LATITUDE_KEY = "Latitude";
    private static final String LONGITUDE_KEY = "Longitude";
    private static final String COORDINATE_KEY = "Coordinate";

    private final double mLatitude;
    private final double mLongitude;
    private final String mHeading;

    // TODO: Remove context parameter after debugging
    public static List<VanLocation> getCurrentVanLocations(JSONArray jsonArray, Context context) {
        List<VanLocation> locations = new LinkedList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            try {
                JSONObject coordinates = jsonArray.getJSONObject(i).getJSONObject(COORDINATE_KEY);
                String heading = jsonArray.getJSONObject(i).getString(HEADING_KEY);
                double latitude = coordinates.getDouble(LATITUDE_KEY);
                double longitude = coordinates.getDouble(LONGITUDE_KEY);
                locations.add(new VanLocation(heading, latitude, longitude));
            } catch (JSONException e) {
                // TODO: Handle exception. How?
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        return locations;
    }

    private VanLocation(String heading, double latitude, double longitude) {
        mHeading = heading;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public String getHeading() {
        return mHeading;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }
}
