package liamkengineering.vandyvans.data;


import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;


public class MyLocationListener implements LocationListener {

    private static MyLocationListener sInstance;

    public static synchronized MyLocationListener getInstance() {
        if (sInstance == null) {
            sInstance = new MyLocationListener();
        }
        return sInstance;
    }

    private MyLocationListener() {

    }

    private Location mCurLocation;

    @Override
    public void onLocationChanged(Location loc) {
        mCurLocation = loc;
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    public double getLatitude() {
        return mCurLocation.getLatitude();
    }

    public double getLongitude() {
        return mCurLocation.getLongitude();
    }

    public boolean hasLocationData() {
        return mCurLocation != null;
    }
}