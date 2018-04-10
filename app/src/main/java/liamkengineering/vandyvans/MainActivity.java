package liamkengineering.vandyvans;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import liamkengineering.vandyvans.data.DataManager;
import liamkengineering.vandyvans.data.JSONUpdateListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataManager.getInstance(MainActivity.this).getInitialData(new JSONUpdateListener() {
            @Override
            public void onJSONObjectUpdate(JSONObject jsonResponse) {
                Toast.makeText(MainActivity.this, jsonResponse.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onJSONArrayUpdate(JSONArray jsonResponse) {

            }
        });
    }
}
