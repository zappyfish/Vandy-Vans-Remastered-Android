package liamkengineering.vandyvans.data.types;

/**
 * Created by Liam on 4/10/2018.
 */

public class VanStop {

    private final String mStopName;

    private final double mLatitude;
    private final double mLongitude;

    VanStop(String stopName, double latitude, double longitude) {
        mStopName = stopName;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    String getStopName() {
        return mStopName;
    }

    double getLatitude() {
        return mLatitude;
    }

    double getLongitude() {
        return mLongitude;
    }
}