package liamkengineering.vandyvans.data.types;

import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import liamkengineering.vandyvans.R;

/**
 * Created by Liam on 4/10/2018.
 */

public class Route {

    private static final int ROUTE_WIDTH = 8;

    private final List<Point> mPoints;
    private final Map<String, VanStop> mStopNameStopMap;
    private final Map<String, VanStop> mStopIDStopMap;
    private final String mName;
    private final String mID;
    private final Map<String, VanLocation> mVanLocationMap;
    private final PolylineOptions mPolyLineOptions;

    public static Route getRouteFromJSON(JSONObject routeJSON, Map<String, VanStop> vanIDMap) {
        List<VanStop> routeStops = new LinkedList<>();
        try {
            JSONArray stops = routeJSON.getJSONArray("stops");
            for (int i = 0; i < stops.length(); i++) {
                VanStop originalStop = vanIDMap.get(Integer.toString(stops.getInt(i)));
                routeStops.add(VanStop.copyVanStop(originalStop));
            }
        } catch (Exception e) {

        }
        JSONArray waypoints = null;
        String name = null;
        String id = null;
        try {
            waypoints = routeJSON.getJSONArray("path");
            name = routeJSON.getString("name");
            id = routeJSON.getString("id");
        } catch (Exception e) {

        }
        return new Route(waypoints, routeStops, name, id);
    }

    Route(JSONArray waypoints, List<VanStop> stops, String name, String id) {
        mPoints = new LinkedList<>();

        mName = name.toUpperCase();
        mID = id;
        mVanLocationMap = new HashMap<>();
        mStopNameStopMap = new HashMap<>();
        mStopIDStopMap = new HashMap<>();

        for (VanStop stop : stops) {
            mStopNameStopMap.put(stop.getStopName(), stop);
            mStopIDStopMap.put(stop.getID(), stop);
        }

        if (waypoints != null) {
            for (int i = 0; i < waypoints.length(); i += 2) {
                try {
                    mPoints.add(new Point(waypoints.getDouble(i), waypoints.getDouble(i + 1)));
                } catch (JSONException e) {
                    // TODO: Handle exception
                }
            }
        }
        mPolyLineOptions = getRouteForMap();
    }

    private static String getColorForName(String name) {
        switch (name) {
            case "BLACK":
                return "#000000";
            case "RED":
                return "#FF0000";
            case "GOLD":
                return "#E9C34C";
            default:
                return "#0000FF";
        }
    }

    public List<Point> getPoints() {
        return mPoints;
    }

    public Collection<VanStop> getStops() {
        return mStopNameStopMap.values();
    }

    public String getName() {
        return mName;
    }

    public String getID() {
        return mID;
    }

    public String getColor() {
        return mName;
    }

    public void updateArrivalData(ArrivalData data) {
        mStopIDStopMap.get(data.getStopID()).updateArrivalData(data);
    }

    public void updateVanLocation(VanLocation location) {
        mVanLocationMap.put(location.getID(), location);
    }

    public Collection<VanLocation> getVanLocations() {
        return mVanLocationMap.values();
    }

    public PolylineOptions getPolylineOptions() {
        return mPolyLineOptions;
    }

    public boolean stopsAt(VanStop stop) {
        return mStopNameStopMap.containsKey(stop.getStopName());
    }

    public VanStop getStopForName(String name) {
        return mStopNameStopMap.get(name);
    }

    public VanStop getStopForID(String id) {
        return mStopIDStopMap.get(id);
    }

    private PolylineOptions getRouteForMap() {
        PolylineOptions options = new PolylineOptions();
        options.color(Color.parseColor(getColorForName(this.mName)));
        options.width(ROUTE_WIDTH);
        options.visible(true);
        for (Point point : this.getPoints()) {
            options.add(new LatLng(point.getLatitude(), point.getLongitude()));
        }
        return options;
    }

    public MarkerOptions getMarkerOptions(String stopName) {
        return mStopNameStopMap.get(stopName).getMarkerOptions();
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
