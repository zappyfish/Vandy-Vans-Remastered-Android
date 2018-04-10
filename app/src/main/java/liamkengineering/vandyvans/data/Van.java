package liamkengineering.vandyvans.data;

/**
 * Created by Liam on 4/9/2018.
 */

public enum Van {
    BLACK("black", "1290", "1857"),
    GOLD("gold", "1289", "3021"),
    RED("red", "1291", "1858");

    // REST API request base URL and paths
    private static final String BASE_URL = "https://www.vandyvans.com";
    private static final String REGION = "Region";
    private static final String ROUTES = "Routes";
    private static final String ROUTE = "Route";
    private static final String VEHICLES = "Vehicles";
    private static final String DIRECTION = "Direction";
    private static final String STOPS = "Stops";
    private static final String WAYPOINTS = "Waypoints";

    private final String mWaypointRequest;
    private final String mStopRequest;
    private final String mVehicleRequest;
    private final String mRouteDataRequest;

    Van(String color, String routeID, String patternID) {

        mWaypointRequest = assembleRequestURL(ROUTE + routeID + WAYPOINTS);
        mStopRequest = assembleRequestURL(ROUTE + patternID + DIRECTION + routeID + STOPS);
        mVehicleRequest = assembleRequestURL(ROUTE + routeID + VEHICLES);
        mRouteDataRequest = assembleRequestURL(REGION, "0", ROUTES);
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

    String getRouteDataURL() {
        return mRouteDataRequest;
    }

    private String assembleRequestURL(String... pathModifiers) {
        String requestURL = BASE_URL;
        for (String modifier : pathModifiers) {
            requestURL += "/" + modifier;
        }
        return requestURL;
    }
}
