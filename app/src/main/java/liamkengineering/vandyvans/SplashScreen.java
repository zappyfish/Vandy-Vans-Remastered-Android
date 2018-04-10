package liamkengineering.vandyvans;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/** Download data for startup (e.g. maps) here, transition to MainActivity on result */
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }

    private void transitionToMainActivity() {
        Intent intent = new Intent (SplashScreen.this, MainActivity.class);
        // Place any relevant data in an intent here
        startActivity(intent);
    }
}
