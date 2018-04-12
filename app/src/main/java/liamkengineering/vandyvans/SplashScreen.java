package liamkengineering.vandyvans;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.List;

import liamkengineering.vandyvans.data.DataManager;
import liamkengineering.vandyvans.data.types.InitialData;
import liamkengineering.vandyvans.data.types.InitialDataListener;
import liamkengineering.vandyvans.data.types.Route;
import liamkengineering.vandyvans.data.types.VanStop;

/** Download data for startup (e.g. maps) here, transition to MainActivity on result */
public class SplashScreen extends AppCompatActivity {

    private static final int ROUTE_WIDTH = 8;

    private HashMap<String, String> mColorMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        initColorMap();

        final Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        DataManager.getInstance(SplashScreen.this).getInitialData(new InitialDataListener() {
            @Override
            public void onInitialDataAvailable(List<InitialData> initialDataList) {
                // HashMap specifically b/c it implements Serializable
                intent.putExtra("route_map", getColorRouteMap(initialDataList));
                // Don't need to process the stop data, so we'll just send over the rest of the
                // initialDataList
                HashMap<String, List<VanStop>> vanStopListMap = new HashMap<>();
                for (InitialData initialData : initialDataList) {
                    vanStopListMap.put(initialData.getColor(), initialData.getVanStops());
                }
                intent.putExtra("vanstop_map", vanStopListMap);
                startActivity(intent);
            }
        });
    }

    private void transitionToMainActivity() {
        Intent intent = new Intent (SplashScreen.this, MainActivity.class);
        // Place any relevant data in an intent here
        startActivity(intent);
    }

    private PolylineOptions getRouteForMap(String color, Route route) {
        PolylineOptions options = new PolylineOptions();
        options.color(Color.parseColor(mColorMap.get(color)));
        options.width(ROUTE_WIDTH);
        options.visible(true);
        for (Route.Point point : route.getPoints()) {
            options.add(new LatLng(point.getLatitude(), point.getLongitude()));
        }
        return options;
    }

    private HashMap<String, PolylineOptions> getColorRouteMap(List<InitialData> initialDataList) {
        HashMap<String, PolylineOptions> colorRouteMap = new HashMap<>();
        for (InitialData initialData : initialDataList) {
            String color = initialData.getColor();
            colorRouteMap.put(color, getRouteForMap(color, initialData.getVanRoute()));
        }
        return colorRouteMap;
    }

    private void initColorMap() {
        mColorMap = new HashMap<>();
        mColorMap.put("BLACK", "#000000");
        mColorMap.put("RED", "#FF0000");
        mColorMap.put("GOLD", "#FFDF00");
    }
}
