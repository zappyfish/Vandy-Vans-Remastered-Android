package liamkengineering.vandyvans;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.MapsInitializer;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        MapsInitializer.initialize(getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        final Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        final Intent retryIntent = new Intent(SplashScreen.this, RetryActivity.class);
        DataManager.getInstance(SplashScreen.this).getInitialData(new InitialDataListener() {
            @Override
            public void onInitialDataAvailable(boolean success) {
                if (success) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivityIfNeeded(intent, 0);
                } else {
                    retryIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivityIfNeeded(retryIntent, 0);
                }
            }
        });
    }
}
