package liamkengineering.vandyvans;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import liamkengineering.vandyvans.data.DataManager;
import liamkengineering.vandyvans.data.JSONUpdateListener;
import liamkengineering.vandyvans.data.types.InitialData;
import liamkengineering.vandyvans.data.types.InitialDataListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataManager.getInstance(MainActivity.this).getInitialData(new InitialDataListener() {
            @Override
            public void onInitialDataAvailable(List<InitialData> initialDataList) {
                
            }
        });
    }
}
