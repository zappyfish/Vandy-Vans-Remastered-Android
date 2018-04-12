package liamkengineering.vandyvans;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import liamkengineering.vandyvans.data.DataManager;
import liamkengineering.vandyvans.data.Van;
import liamkengineering.vandyvans.data.types.VanLocation;
import liamkengineering.vandyvans.data.types.VanLocationUpdateListener;
import liamkengineering.vandyvans.data.types.VanStop;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    // This is kinda gross, but I think the backend might provide colors, so they can be
    // parsed later
    private Map<String, PolylineOptions> mColorRouteMap;
    private Map<String, List<VanStop>> mColorVanStopMap;

    private static final String BLACK = "BLACK";
    private static final String RED = "RED";
    private static final String GOLD = "GOLD";

    private static final double VANDERBILT_LATITUDE = 36.1425898;
    private static final double VANDERBILT_LONGITUDE = -86.8032756;

    private static final float DEFAULT_ZOOM = 14.8f;
    private static final float STOP_ZOOM = 17f;
    private static final int STOP_RADIUS = 115;
    private static final double STOP_CLICK_THRESH = 0.001035;
    private static final float VAN_RADIUS = 80;

    private GoogleMap mMap;
    private String mVisibleRoute = "BLACK";

    private List<VanLocation> mLatestBlackLocations;
    private List<VanLocation> mLatestGoldLocations;
    private List<VanLocation> mLatestRedLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mColorRouteMap = (HashMap<String, PolylineOptions>) getIntent().getSerializableExtra("route_map");
        mColorVanStopMap = (HashMap<String, List<VanStop>>) getIntent().getSerializableExtra("vanstop_map");

        mLatestBlackLocations = new LinkedList<>();
        mLatestGoldLocations = new LinkedList<>();
        mLatestRedLocations = new LinkedList<>();

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
                    drawVanMap(mMap, color);
                    mVisibleRoute = color;
                }
            }
        });
    }

    private void initInteractions() {
        ImageView goldButton = (ImageView) findViewById(R.id.gold_button);
        initRouteButton(goldButton, GOLD);
        ImageView redButton = (ImageView) findViewById(R.id.red_button);
        initRouteButton(redButton, RED);
        ImageView blackButton = (ImageView) findViewById(R.id.black_button);
        initRouteButton(blackButton, BLACK);

        final ImageView backButton = (ImageView) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawVanMap(mMap, mVisibleRoute);
                LatLng vandy = new LatLng(VANDERBILT_LATITUDE, VANDERBILT_LONGITUDE);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(vandy, DEFAULT_ZOOM));
                backButton.setVisibility(View.INVISIBLE);
            }
        });

        DataManager.getInstance(MainActivity.this).registerVanLocationListener(GOLD, new VanLocationUpdateListener() {
            @Override
            public void onVanLocationsUpdate(List<VanLocation> vanLocations) {
                mLatestGoldLocations = vanLocations;
                if (mVisibleRoute.equals(GOLD)) {
                    mMap.clear();
                    drawVanMap(mMap, GOLD);
                    drawVansOnMap(mMap, vanLocations);
                }
            }
        });

        DataManager.getInstance(MainActivity.this).registerVanLocationListener(BLACK, new VanLocationUpdateListener() {
            @Override
            public void onVanLocationsUpdate(List<VanLocation> vanLocations) {
                mLatestBlackLocations = vanLocations;
                if (mVisibleRoute.equals(BLACK)) {
                    mMap.clear();
                    drawVanMap(mMap, BLACK);
                    drawVansOnMap(mMap, vanLocations);
                }
            }
        });

        DataManager.getInstance(MainActivity.this).registerVanLocationListener(RED, new VanLocationUpdateListener() {
            @Override
            public void onVanLocationsUpdate(List<VanLocation> vanLocations) {
                mLatestRedLocations = vanLocations;
                if (mVisibleRoute.equals(RED)) {
                    mMap.clear();
                    drawVanMap(mMap, RED);
                    drawVansOnMap(mMap, vanLocations);
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(vandy, DEFAULT_ZOOM));

        initInteractions();

        drawVanMap(mMap, BLACK);
    }

    private void drawStop(GoogleMap map, VanStop stop) {
        BitmapDescriptor image = BitmapDescriptorFactory.fromResource(R.drawable.stop);
        GroundOverlayOptions options = new GroundOverlayOptions();
        options.image(image);
        options.position(new LatLng(stop.getLatitude(), stop.getLongitude()), STOP_RADIUS);
        options.zIndex(0.5f);
        map.addGroundOverlay(options);
    }

    private void drawVanMap(final GoogleMap map, final String color) {
        map.addPolyline(mColorRouteMap.get(color));
        for (VanStop stop : mColorVanStopMap.get(color)) {
            drawStop(map, stop);
        }
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                for (VanStop stop : mColorVanStopMap.get(color)) {
                    if (wasObjectClicked(latLng, stop.getLatitude(), stop.getLongitude(), STOP_CLICK_THRESH)) {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(stop.getLatitude(), stop.getLongitude()), STOP_ZOOM));
                        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
                        GroundOverlayOptions options = new GroundOverlayOptions();
                        options.image(createPureTextIcon(stop.getStopName()));
                        options.position(new LatLng(stop.getLatitude(), stop.getLongitude()), STOP_RADIUS);
                        options.zIndex(1.0f);
                        map.addGroundOverlay(options);
                        break;
                    }
                }
            }
        });
    }

    private boolean wasObjectClicked(LatLng clickLocation, double objLat, double objLong, double thresh) {
        double dist = Math.pow(clickLocation.latitude - objLat, 2) + Math.pow(clickLocation.longitude - objLong, 2);
        dist = Math.sqrt(dist);
        return dist <= thresh;
    }

    private BitmapDescriptor createPureTextIcon(String text) {
        Paint textPaint = new Paint();

        float textWidth = textPaint.measureText(text);
        float textHeight = textPaint.getTextSize();
        int width = (int) (textWidth);
        int height = (int) (textHeight * 2);

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);

        canvas.translate(0, height);

        canvas.drawText(text, 0, 0, textPaint);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(image);
        return icon;
    }

    private void drawVansOnMap(GoogleMap map, List<VanLocation> locations) {
        BitmapDescriptor vanImage = BitmapDescriptorFactory.fromResource(R.drawable.van_icon);
        GroundOverlayOptions options = new GroundOverlayOptions();
        options.zIndex(1.0f);
        options.image(vanImage);
        for (VanLocation location : locations) {
            options.position(new LatLng(location.getLatitude(), location.getLongitude()), VAN_RADIUS);
            map.addGroundOverlay(options);
        }
    }
}
