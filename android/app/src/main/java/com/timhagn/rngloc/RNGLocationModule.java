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

    public RNGLocationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        mLocationProvider = new LocationProvider(mReactContext, this);
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

    public void handleNewLocation(Location location) {
        mLastLocation = location;
    }

    @ReactMethod
    public void getLocation(Callback successCallback, Callback errorCallback) {
        if (mLastLocation != null) {
            try {
                double Longitude;
                double Latitude;

                Longitude = mLastLocation.getLongitude();
                Latitude = mLastLocation.getLatitude();

                successCallback.invoke(Longitude, Latitude);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "Location services connected.");
            }
        }
    }
}
