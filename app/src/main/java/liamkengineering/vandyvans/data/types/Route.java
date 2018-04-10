package liamkengineering.vandyvans.data.types;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Liam on 4/10/2018.
 */

public class Route {

    private final String LATITUDE_KEY = "Latitude";
    private final String LONGITUDE_KEY = "Longitude";

    private final List<Point> mPoints;

    public static Route getRouteFromWaypointJSON(JSONArray waypoints) {
        return new Route(waypoints);
    }

    Route(JSONArray waypoints) {
        mPoints = new LinkedList<>();
        for (int i = 0; i < waypoints.length(); ++i) {
            try {
                mPoints.add(new Point(waypoints.getJSONObject(i).getDouble(LATITUDE_KEY),
                        waypoints.getJSONObject(i).getDouble(LONGITUDE_KEY)));
            } catch (JSONException e) {
                // TODO: Handle exception
            }
        }
    }

    public List<Point> getPoints() {
        return mPoints;
    }

    public static class Point {

        private final double mLatitude;
        private final double mLongitude;

        private Point(double latitude, double longitude) {
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
}
