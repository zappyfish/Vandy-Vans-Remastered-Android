package liamkengineering.vandyvans;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import liamkengineering.vandyvans.data.DataManager;
import liamkengineering.vandyvans.data.types.ArrivalData;
import liamkengineering.vandyvans.data.types.Route;
import liamkengineering.vandyvans.data.types.VanLocation;
import liamkengineering.vandyvans.data.types.VanLocationUpdateListener;
import liamkengineering.vandyvans.data.types.VanStop;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    // List of the last vans of the map so we can clear them easily
    private List<Marker> mLastVansList;

    private static final String BLACK = "BLACK";
    private static final String RED = "RED";
    private static final String GOLD = "GOLD";

    private static final double VANDERBILT_LATITUDE = 36.1425898;
    private static final double VANDERBILT_LONGITUDE = -86.8032756;

    private static final float DEFAULT_ZOOM = 14.8f;
    private static final float STOP_ZOOM = 17f;

    private GoogleMap mMap;
    private Route mVisibleRoute;

    private boolean mIsZoomed = false;
    private Marker mClickedMarker;

    private ImageView mBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVisibleRoute = DataManager.getInstance(this).getRouteByName(BLACK);

        mLastVansList = new LinkedList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void initRouteButton(ImageView button, final String color) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Route clicked = DataManager.getInstance(MainActivity.this).getRouteByName(color);
                if (mVisibleRoute != clicked) {
                    mVisibleRoute = clicked;
                    if (mIsZoomed) {
                        unZoom();
                    }
                    drawVanMap();
                    drawVansOnMap();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mIsZoomed) {
            unZoom();
        } else {
            super.onBackPressed();
        }
    }

    private void unZoom() {
        mIsZoomed = false;
        drawVanMap();
        LatLng vandy = new LatLng(VANDERBILT_LATITUDE, VANDERBILT_LONGITUDE);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(vandy, DEFAULT_ZOOM));
        mBackButton.setVisibility(View.INVISIBLE);
        mClickedMarker.hideInfoWindow();
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
                drawVanMap();
                LatLng vandy = new LatLng(VANDERBILT_LATITUDE, VANDERBILT_LONGITUDE);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(vandy, DEFAULT_ZOOM));
                mBackButton.setVisibility(View.INVISIBLE);
                mClickedMarker.hideInfoWindow();
                mIsZoomed = false;
            }
        });

        DataManager.getInstance(MainActivity.this).setVanLocationListener(new VanLocationUpdateListener() {
            @Override
            public void onVanLocationsUpdate() {
                clearVans();
                drawVanMap();
            }
        });
        Collection<VanStop> stops = DataManager.getInstance(this).getStops();
        String[] stopsArr = new String[stops.size()];
        int index = 0;
        for (VanStop stop : stops) {
            stopsArr[index++] = stop.getStopName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, stopsArr);
        final AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autocomplete_bar);
        textView.setAdapter(adapter);

        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
                String stopName = (String)adapterView.getItemAtPosition(i);
                VanStop stop = mVisibleRoute.getStopForName(stopName);
                if (stop != null) {
                    Marker stopMarker = stop.getMarker();
                    ((GoogleMap.OnMarkerClickListener) stopMarker.getTag()).onMarkerClick(stopMarker);
                    textView.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "The " + mVisibleRoute.getName()
                            + " van does not stop at " + stopName, Toast.LENGTH_SHORT).show();
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

        drawVanMap();
    }

    private void drawStop(final Route route, final VanStop stop) {
        MarkerOptions options = stop.getMarkerOptions();
        Marker marker = mMap.addMarker(options);
        marker.setTag(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Zoom in on the stop, set its arrival time, and show the back button
                LatLng pos = new LatLng(stop.getLatitude(), stop.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, STOP_ZOOM));
                Collection<ArrivalData> arrivalData = stop.getArrivalData();
                if (arrivalData != null && !arrivalData.isEmpty()) {
                    // Make sure there is data available
                    String arrivals = "";
                    for (ArrivalData data : arrivalData) {
                        arrivals += "Van arriving in: " + data.getMinutesToArrival() + " minutes\n";
                    }
                    marker.setSnippet(arrivals);
                } else {
                    marker.setSnippet("No arrival data available for " + mVisibleRoute.getName() + " route.");
                }
                mBackButton.setVisibility(View.VISIBLE);
                mIsZoomed = true;
                mClickedMarker = marker;
                marker.showInfoWindow();
                return false;
            }
        });
        // Store away the marker so we can find it with a search
        stop.setMarker(marker);
    }

    private void drawVanMap() {
        mMap.clear();
        mMap.addPolyline(mVisibleRoute.getPolylineOptions());
        for (VanStop stop : mVisibleRoute.getStops()) {
            drawStop(mVisibleRoute, stop);
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                GoogleMap.OnMarkerClickListener listener = (GoogleMap.OnMarkerClickListener)marker.getTag();
                return listener != null && listener.onMarkerClick(marker);
            }
        });
        drawVansOnMap();
    }

    private void clearVans() {
        for (Marker van : mLastVansList) {
            van.remove();
        }
        mLastVansList.clear();
    }

    private void drawVansOnMap() {
        for (VanLocation location : mVisibleRoute.getVanLocations()) {
            drawVan(location);
        }
    }

    private void drawVan(VanLocation location) {
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.van_icon);
        Bitmap vanIcon = bitmapdraw.getBitmap();
        MarkerOptions van = new MarkerOptions();
        van.icon(BitmapDescriptorFactory.fromBitmap(bitmapSizeByScall(vanIcon, 0.65f)));
        van.position(new LatLng(location.getLatitude(), location.getLongitude()));
        van.anchor(0.5f, 0.5f);
        van.rotation(location.getHeading());
        mLastVansList.add(mMap.addMarker(van));
    }

    private static Bitmap bitmapSizeByScall(Bitmap bitmapIn, float scall_zero_to_one_f) {

        Bitmap bitmapOut = Bitmap.createScaledBitmap(bitmapIn,
                Math.round(bitmapIn.getWidth() * scall_zero_to_one_f),
                Math.round(bitmapIn.getHeight() * scall_zero_to_one_f), false);

        return bitmapOut;
    }
}