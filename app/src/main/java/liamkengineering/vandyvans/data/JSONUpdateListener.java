package liamkengineering.vandyvans.data;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Liam on 4/9/2018.
 */

public interface JSONUpdateListener {
    void onJSONObjectUpdate(JSONObject jsonResponse);

    void onJSONArrayUpdate(JSONArray jsonResponse);

    void onError();
}
