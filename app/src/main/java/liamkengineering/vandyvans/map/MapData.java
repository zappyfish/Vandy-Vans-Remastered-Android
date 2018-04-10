package liamkengineering.vandyvans.map;

/**
 * Created by Liam on 4/9/2018.
 */

public class MapData {

    static class VanStop {

        private final String mStopName;

        private final double mLatitude;
        private final double mLongitude;

        VanStop(String stopName, double latitude, double longitude) {
            mStopName = stopName;
            mLatitude = latitude;
            mLongitude = longitude;
        }
    }
}
