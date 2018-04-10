package liamkengineering.vandyvans.data.types;

/**
 * Created by Liam on 4/10/2018.
 */

import org.json.JSONArray;

import java.util.List;

/** Might want to rethink this class since it kinda relies on constant garbage collection */
public class VanLocation {

    private final double mLatitude;
    private final double mLongitude;

    public static List<VanLocation> getCurrentVanLocations(JSONArray jsonArray) {
        return null;
    }

    private VanLocation(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }
}
