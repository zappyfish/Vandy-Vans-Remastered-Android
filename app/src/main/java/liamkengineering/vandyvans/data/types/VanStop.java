package liamkengineering.vandyvans.data.types;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Liam on 4/10/2018.
 */

public class VanStop implements Serializable {

    private static final String NAME_KEY = "Name";
    private static final String LATITUDE_KEY = "Latitude";
    private static final String LONGITUDE_KEY = "Longitude";

    private final String mStopName;

    private final double mLatitude;
    private final double mLongitude;

    public static List<VanStop> getVanStopsFromJSON(JSONArray jsonArray) {
        List<VanStop> vanStops = new LinkedList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            try {
                JSONObject stopJSON = jsonArray.getJSONObject(i);
                String name = stopJSON.getString(NAME_KEY);
                double latitude = stopJSON.getDouble(LATITUDE_KEY);
                double longitude = stopJSON.getDouble(LONGITUDE_KEY);
                vanStops.add(new VanStop(name, latitude, longitude));
            } catch (JSONException e) {
                // TODO: Handle exception. How?
            }
        }
        return vanStops;
    }

    private VanStop(String stopName, double latitude, double longitude) {
        mStopName = stopName;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public String getStopName() {
        return mStopName;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }
}