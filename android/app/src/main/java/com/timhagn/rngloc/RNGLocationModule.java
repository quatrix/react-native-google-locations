package com.timhagn.rngloc;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.renderscript.ScriptGroup;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

/**
 * Created by hagn on 11/5/15.
 */
public class RNGLocationModule extends ReactContextBaseJavaModule implements LocationProvider.LocationCallback {

    public static final String REACT_CLASS = "RNGLocation";
    public static final String TAG = RNGLocationModule.class.getSimpleName();

    ReactApplicationContext mReactContext;
    private Location mLastLocation;
    private LocationProvider mLocationProvider;
    private Callback lastSuccessCallback;
    private Callback lastErrorCallback;

    public RNGLocationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        mLocationProvider = new LocationProvider(mReactContext.getApplicationContext(), this);

        if (!mLocationProvider.checkPlayServices()) {
            mLocationProvider.disconnect();

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

        // TODO: Schaun, wieso update Location nich ausgeführt wird...
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
        if (lastSuccessCallback != null) {
            Log.i(TAG, "New GPSLocation..." + location.toString());
            getLocation(lastSuccessCallback, lastErrorCallback);
        }
    }

    @ReactMethod
    public void getLocation(Callback successCallback, Callback errorCallback) {
        lastErrorCallback = errorCallback;
        lastSuccessCallback = successCallback;
        if (mLastLocation != null) {
            try {
                double Longitude;
                double Latitude;

                Longitude = mLastLocation.getLongitude();
                Latitude = mLastLocation.getLatitude();

                Log.i(TAG, "Got new location.");

                successCallback.invoke(Longitude, Latitude);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "Location services disconnected.");
            }
        }
    }
}
