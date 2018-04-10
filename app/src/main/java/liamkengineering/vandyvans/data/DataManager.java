package liamkengineering.vandyvans.data;

/**
 * Created by Liam on 4/9/2018.
 */

/** Singleton class which runs a background service to update latest data
 *  and invoke callbacks upon receipt.
 */
public class DataManager {

    private static final String

    private

    private static DataManager sInstance;

    public static final synchronized DataManager getInstance() {
        if (sInstance == null) {
            sInstance = new DataManager();
        }
    }

    private DataManager() {
    }

    /** Get initial data and then make callback */
    private void init() {

    }

    public void getInitialData(JSONUpdateListener routeListener, JSONUpdateListener waypointListener) {
        // Make Volley request and then callback routeListener and waypointListener
    }

    private void registerVanDataListener(JSONUpdateListener listener, Van van) {
        
    }
}
