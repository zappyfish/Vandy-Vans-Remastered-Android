package liamkengineering.vandyvans;

import android.media.Image;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import liamkengineering.vandyvans.data.DataManager;
import liamkengineering.vandyvans.data.types.VanLocation;
import liamkengineering.vandyvans.data.types.VanLocationUpdateListener;
import liamkengineering.vandyvans.data.types.VanStop;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private Map<String, Float> mDirectionRotationMap;

    // This is kinda gross, but I think the backend might provide colors, so they can be
    // parsed lateror
    private Map<String, PolylineOptions> mColorRouteMap;

    // List of van stops by color
    private Map<String, List<VanStop>> mColorVanStopMap;

    // Each color's map of van stop ID to van stop object
    private Map<String, Map<String, MarkerOptions>> mColorStopIDVanStopMap;

    // Map of van stop name to its ID
    private Map<String, String> mStopNameIDMap;

    // List of the last vans of the map so we can clear them easily
    private List<Marker> mLastVansList;

    // Map of routeID to color for purposes of arrivals
    private Map<String, String> mRouteIDRouteColorMap;

    private static final String BLACK = "BLACK";
    private static final String RED = "RED";
    private static final String GOLD = "GOLD";

    private static final double VANDERBILT_LATITUDE = 36.1425898;
    private static final double VANDERBILT_LONGITUDE = -86.8032756;

    private static final float DEFAULT_ZOOM = 14.8f;
    private static final float STOP_ZOOM = 17f;

    private GoogleMap mMap;
    private String mVisibleRoute = "BLACK";

    private boolean mIsZoomed = false;
    private Marker mClickedMarker;

    private ImageView mBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mColorRouteMap = (HashMap<String, PolylineOptions>) getIntent().getSerializableExtra("route_map");
        mColorVanStopMap = (HashMap<String, List<VanStop>>) getIntent().getSerializableExtra("vanstop_map");

        mColorStopIDVanStopMap = new HashMap<>();

        initRotationMap();

        mLastVansList = new LinkedList<>();

        createStopMarkersForColor(RED);
        createStopMarkersForColor(BLACK);
        createStopMarkersForColor(GOLD);

        mStopNameIDMap = new HashMap<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void initRouteButton(ImageView button, final String color) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mVisibleRoute.equals(color)) {
                    mMap.clear();
                    drawVanMap(color);
                    mVisibleRoute = color;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mIsZoomed) {
            mIsZoomed = false;
            drawVanMap(mVisibleRoute);
            LatLng vandy = new LatLng(VANDERBILT_LATITUDE, VANDERBILT_LONGITUDE);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(vandy, DEFAULT_ZOOM));
            mBackButton.setVisibility(View.INVISIBLE);
            mClickedMarker.hideInfoWindow();
        } else {
            super.onBackPressed();
        }
    }

    private void initInteractions() {
        ImageView goldButton = (ImageView) findViewById(R.id.gold_button);
        initRouteButton(goldButton, GOLD);
        ImageView redButton = (ImageView) findViewById(R.id.red_button);
        initRouteButton(redButton, RED);
        ImageView blackButton = (ImageView) findViewById(R.id.black_button);
        initRouteButton(blackButton, BLACK);

        mBackButton = (ImageView) findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawVanMap(mVisibleRoute);
                LatLng vandy = new LatLng(VANDERBILT_LATITUDE, VANDERBILT_LONGITUDE);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(vandy, DEFAULT_ZOOM));
                mBackButton.setVisibility(View.INVISIBLE);
                mClickedMarker.hideInfoWindow();
                mIsZoomed = false;
            }
        });

        DataManager.getInstance(MainActivity.this).registerVanLocationListener(GOLD, new VanLocationUpdateListener() {
            @Override
            public void onVanLocationsUpdate(List<VanLocation> vanLocations) {
                if (mVisibleRoute.equals(GOLD)) {
                    clearVans();
                    drawVansOnMap(vanLocations);
                }
            }
        });

        DataManager.getInstance(MainActivity.this).registerVanLocationListener(BLACK, new VanLocationUpdateListener() {
            @Override
            public void onVanLocationsUpdate(List<VanLocation> vanLocations) {
                if (mVisibleRoute.equals(BLACK)) {
                    clearVans();
                    drawVansOnMap(vanLocations);
                }
            }
        });

        DataManager.getInstance(MainActivity.this).registerVanLocationListener(RED, new VanLocationUpdateListener() {
            @Override
            public void onVanLocationsUpdate(List<VanLocation> vanLocations) {
                if (mVisibleRoute.equals(RED)) {
                    clearVans();
                    drawVansOnMap(vanLocations);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng vandy = new LatLng(VANDERBILT_LATITUDE, VANDERBILT_LONGITUDE);

        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(vandy, DEFAULT_ZOOM));

        initInteractions();

        drawVanMap(BLACK);
    }

    private void drawStop(String color, final VanStop stop) {
        MarkerOptions options = mColorStopIDVanStopMap.get(color).get(stop.getID());
        Marker marker = mMap.addMarker(options);
        marker.setTag(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng pos = new LatLng(stop.getLatitude(), stop.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, STOP_ZOOM));
                mBackButton.setVisibility(View.VISIBLE);
                mIsZoomed = true;
                mClickedMarker = marker;
                return false;
            }
        });
    }

    private void drawVanMap(final String color) {
        mMap.addPolyline(mColorRouteMap.get(color));
        for (VanStop stop : mColorVanStopMap.get(color)) {
            drawStop(color, stop);
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                GoogleMap.OnMarkerClickListener listener = (GoogleMap.OnMarkerClickListener)marker.getTag();
                return listener != null && listener.onMarkerClick(marker);
            }
        });
    }

    private MarkerOptions createStopMarker(VanStop stop) {
        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.stop));
        options.title(stop.getStopName());
        options.position(new LatLng(stop.getLatitude(), stop.getLongitude()));
        return options;
    }

    private void createStopMarkersForColor(String color) {
        List<VanStop> stops = mColorVanStopMap.get(color);
        Map<String, MarkerOptions> vanStopIDVanStopMarkerMap = new HashMap<>();
        for (VanStop stop : stops) {
            vanStopIDVanStopMarkerMap.put(stop.getID(), createStopMarker(stop));
        }
        mColorStopIDVanStopMap.put(color, vanStopIDVanStopMarkerMap);
    }

    private void clearVans() {
        for (Marker van : mLastVansList) {
            van.remove();
        }
        mLastVansList.clear();
    }

    private void drawVansOnMap(List<VanLocation> locations) {
        for (VanLocation location : locations) {
            drawVan(location);
        }
    }

    private void drawVan(VanLocation location) {
        MarkerOptions van = new MarkerOptions();
        van.icon(BitmapDescriptorFactory.fromResource(R.drawable.van_icon));
        van.position(new LatLng(location.getLatitude(), location.getLongitude()));
        van.anchor(0.5f, 0.5f);
        van.rotation(mDirectionRotationMap.get(location.getHeading()));
        mLastVansList.add(mMap.addMarker(van));
    }

    private void initRotationMap() {
        mDirectionRotationMap = new HashMap<>();
        mDirectionRotationMap.put("N", 0f);
        mDirectionRotationMap.put("NE", 45.0f);
        mDirectionRotationMap.put("E", 90.0f);
        mDirectionRotationMap.put("SE", 135.0f);
        mDirectionRotationMap.put("S", 180.0f);
        mDirectionRotationMap.put("SW", 225.0f);
        mDirectionRotationMap.put("W", 270.0f);
        mDirectionRotationMap.put("NW", 315.0f);
    }
}