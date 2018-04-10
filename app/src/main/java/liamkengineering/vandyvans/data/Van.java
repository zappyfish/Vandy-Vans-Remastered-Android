package liamkengineering.vandyvans.data;

import liamkengineering.vandyvans.map.MapData;

/**
 * Created by Liam on 4/9/2018.
 */

public class Van {

    // REST API request base URL and paths
    private static final String BASE_URL = "https://www.vandyvans.com";
    private static final String ROUTE = "Route";
    private static final String VEHICLES = "Vehicles";
    private static final String DIRECTION = "Direction";
    private static final String STOPS = "Stops";
    private static final String WAYPOINTS = "Waypoints";

    private final String mWaypointRequest;
    private final String mStopRequest;
    private final String mVehicleRequest;

    private final String mColor;

    private final MapData mCurrentMapData;
    private JSONUpdateListener mUpdateListener;
    private boolean mIsPolling = true;

    Van(String color, String routeID, String patternID) {
        mCurrentMapData = new MapData();

        mColor = color;

        mWaypointRequest = assembleRequestURL(ROUTE, routeID, WAYPOINTS);
        mStopRequest = assembleRequestURL(ROUTE, patternID, DIRECTION, routeID, STOPS);
        mVehicleRequest = assembleRequestURL(ROUTE, routeID, VEHICLES);
    }

    String getWaypointURL() {
        return mWaypointRequest;
    }

    String getStopURL() {
        return mStopRequest;
    }

    String getVehicleURL() {
        return mVehicleRequest;
    }

    public String getColor() {
        return mColor;
    }

    private String assembleRequestURL(String... pathModifiers) {
        String requestURL = BASE_URL;
        for (String modifier : pathModifiers) {
            requestURL += "/" + modifier;
        }
        return requestURL;
    }

    public MapData getMapData() {
        return mCurrentMapData;
    }

    private void setMapData() {

    }

    void setUpdateListener(JSONUpdateListener listener) {
        mUpdateListener = listener;
    }

    JSONUpdateListener getUpdateListener() {
        return mUpdateListener;
    }

    void setIsPolling(boolean isPolling) {
        mIsPolling = isPolling;
    }

    boolean isPolling() {
        return mIsPolling;
    }
}
