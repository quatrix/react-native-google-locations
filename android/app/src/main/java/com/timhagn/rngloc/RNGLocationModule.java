package com.timhagn.rngloc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

/**
 * Created by hagn on 11/5/15.
 *
 * Simple React Native Module for accessing Android Location Services by way of Google Play Services
 *
 */
public class RNGLocationModule extends ReactContextBaseJavaModule implements LocationProvider.LocationCallback {
    public static final String REACT_CLASS = "RNGLocation";
    public static final String TAG = RNGLocationModule.class.getSimpleName();
    public static final int REQUEST_LOCATION_PERMISSIONS = 0x666;
    public static final int USER_DISAGREED = 0;
    public static final int USER_AGREED = -1;
    private Location mLastLocation;
    private LocationProvider mLocationProvider;
    ReactApplicationContext mReactContext;

    public RNGLocationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    /*
     * Location Callback as defined by LocationProvider
     */
    @Override
    public void handleNewLocation(Location location) {
        if (location != null) {
            mLastLocation = location;
            Log.i(TAG, "New Location..." + location.toString());
            getLocation();
        }
    }

    @ReactMethod
    public void start() {
        // Get Location Provider from Google Play Services
        mLocationProvider = new LocationProvider(mReactContext.getApplicationContext(), getCurrentActivity(), this);

        // Check if all went well and the Google Play Service are available...
        if (!mLocationProvider.checkPlayServices()) {
            Log.i("vova", "Location Provider not available...");
        } else {
            // Connect to Play Services
            mLocationProvider.connect();
            Log.i("vova", "Location Provider successfully created.");
        }
    }

    @ReactMethod
    public void getLocation() {
        if (mLastLocation != null) {
            try {
                double Longitude;
                double Latitude;

                // Receive Longitude / Latitude from (updated) Last Location
                Longitude = mLastLocation.getLongitude();
                Latitude = mLastLocation.getLatitude();

                Log.i(TAG, "Got new location. Lng: " + Longitude+" Lat: " + Latitude);

                // Create Map with Parameters to send to JS
                WritableMap params = Arguments.createMap();
                params.putDouble("Longitude", Longitude);
                params.putDouble("Latitude", Latitude);

                // Send Event to JS to update Location
                sendEvent(mReactContext, "updateLocation", params);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "Location services disconnected.");
            }
        }
    }

    /*
     * Internal function for communicating with JS
     */
    private void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        if (reactContext.hasActiveCatalystInstance()) {
            reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, params);
        } else {
            Log.i(TAG, "Waiting for CatalystInstance...");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        // If Location Provider is connected, disconnect.
        if (mLocationProvider != null && mLocationProvider.connected) {
            mLocationProvider.disconnect();
        }
    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_LOCATION_PERMISSIONS)
            return false;

        if (resultCode == USER_DISAGREED) {
            sendEvent(mReactContext, "noLocationPermissions", Arguments.createMap());
        }

        if (resultCode == USER_AGREED)
            mLocationProvider.requestLocation();

        return true;
    }
}
