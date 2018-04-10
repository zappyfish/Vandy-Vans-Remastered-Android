package liamkengineering.vandyvans;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import liamkengineering.vandyvans.data.types.VanStop;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    // This is kinda gross, but I think the backend might provide colors, so they can be
    // parsed later
    private Map<String, PolylineOptions> mColorRouteMap;
    private Map<String, List<VanStop>> mColorVanStopMap;

    private static final double VANDERBILT_LATITUDE = 36.1425898;
    private static final double VANDERBILT_LONGITUDE = -86.8032756;

    private static final float DEFAULT_ZOOM = 15.0f;
    private static final int STOP_RADIUS = 50;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mColorRouteMap = (HashMap<String, PolylineOptions>) getIntent().getSerializableExtra("route_map");
        mColorVanStopMap = (HashMap<String, List<VanStop>>) getIntent().getSerializableExtra("vanstop_map");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng vandy = new LatLng(VANDERBILT_LATITUDE, VANDERBILT_LONGITUDE);

        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(vandy, DEFAULT_ZOOM));

        mMap.addPolyline(mColorRouteMap.get("RED"));
        for (VanStop stop : mColorVanStopMap.get("RED")) {
            drawStop(mMap, stop, "RED");
        }
    }

    private void drawStop(GoogleMap map, VanStop stop, String color) {
        Circle circle = map.addCircle(new CircleOptions()
            .center(new LatLng(stop.getLatitude(), stop.getLongitude()))
            .radius(STOP_RADIUS)
            .strokeColor(Color.BLACK)
            .fillColor(Color.RED));
    }
}
