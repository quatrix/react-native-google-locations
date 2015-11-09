package com.timhagn.rngloc;

import android.location.Location;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

/**
 * Created by hagn on 11/5/15.
 */
public class RNGLocationModule extends ReactContextBaseJavaModule implements LocationProvider.LocationCallback {

    public static final String REACT_CLASS = "RNGLocation";
    public static final String TAG = RNGLocationModule.class.getSimpleName();

    ReactApplicationContext mReactContext;
    private Location mLastLocation;
    private LocationProvider mLocationProvider;

    public RNGLocationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        mLocationProvider = new LocationProvider(mReactContext.getApplicationContext(), this);

        if (!mLocationProvider.checkPlayServices()) {
            mLocationProvider.disconnect(); //*/

            Log.i(TAG, "Location Provider not available, trying GPS.");

            GPSTracker gps = new GPSTracker(mReactContext.getApplicationContext(), this);
            if (gps.canGetLocation()) {
                Log.i(TAG, "Using GPS....");
                handleNewLocation(gps.location);
            } else {
                Log.i(TAG, "No Location Service available.");
            }
        } else {
            mLocationProvider.connect();
            Log.i(TAG, "Location Provider successfully created.");
        }//*/

        // TODO: Schaun, wieso update Location bei GPS nen Fehler wirft...
    }


    @Override
    public String getName() {
        return REACT_CLASS;
    }

    protected void onResume() {
        mLocationProvider.connect();
    }

    protected void onPause() {
        mLocationProvider.disconnect();
    }

    @Override
    public void handleNewLocation(Location location) {
        mLastLocation = location;
        Log.i(TAG, "New GPSLocation..." + location.toString());
        getLocation();
    }

    @ReactMethod
    public void getLocation() {
        if (mLastLocation != null) {
            try {
                double Longitude;
                double Latitude;

                Longitude = mLastLocation.getLongitude();
                Latitude = mLastLocation.getLatitude();

                Log.i(TAG, "Got new location. Lng: " +Longitude+" Lat: "+Latitude);

                WritableMap params = Arguments.createMap();
                params.putDouble("Longitude", Longitude);
                params.putDouble("Latitude", Latitude);

                sendEvent(mReactContext, "updateLocation", params);

                //successCallback.invoke(Longitude, Latitude);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "Location services disconnected.");
            }
        }
    }

    private void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }
}
