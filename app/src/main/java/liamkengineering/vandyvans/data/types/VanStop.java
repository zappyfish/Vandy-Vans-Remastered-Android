package liamkengineering.vandyvans.data.types;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import liamkengineering.vandyvans.R;

/**
 * Created by Liam on 4/10/2018.
 */

public class VanStop implements Serializable {

    private static final String NAME_KEY = "name";
    private static final String LATITUDE_KEY = "lat";
    private static final String LONGITUDE_KEY = "lon";
    private static final String ID_KEY = "id";

    private final String mStopName;
    private final String mID;

    private final double mLatitude;
    private final double mLongitude;

    private final MarkerOptions mMarkerOptions;
    private Marker mMarker;

    private final Map<String, ArrivalData> mBusIDArrivalMap;

    public static VanStop getVanStopFromJSON(JSONObject stopJSON) {
        try {
            String ID = stopJSON.getString(ID_KEY);
            String name = stopJSON.getString(NAME_KEY);
            double latitude = stopJSON.getDouble(LATITUDE_KEY);
            double longitude = stopJSON.getDouble(LONGITUDE_KEY);
            return new VanStop(name, latitude, longitude, ID);
        } catch (Exception e) {
            return null;
        }
    }

    public static VanStop copyVanStop(VanStop stop) {
        return new VanStop(stop.getStopName(), stop.getLatitude(), stop.getLongitude(), stop.getID());
    }

    private VanStop(String stopName, double latitude, double longitude, String ID) {
        mStopName = stopName;
        mLatitude = latitude;
        mLongitude = longitude;
        mID = ID;
        mBusIDArrivalMap = new HashMap<>();
        mMarkerOptions = createStopMarker();
    }

    private MarkerOptions createStopMarker() {
        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.stop));
        options.title(this.getStopName());
        options.position(new LatLng(this.getLatitude(), this.getLongitude()));
        return options;
    }

    public void updateArrivalData(ArrivalData data) {
        mBusIDArrivalMap.put(data.getBusID(), data);
    }

    public Collection<ArrivalData> getArrivalData() {
        return mBusIDArrivalMap.values();
    }

    public MarkerOptions getMarkerOptions() {
        return mMarkerOptions;
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

    public void setMarker(Marker marker) {
        mMarker = marker;
    }

    public Marker getMarker() {
        return mMarker;
    }

    public String getID() { return mID; }
}