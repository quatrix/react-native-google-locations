package com.timhagn.rngloc;

import android.location.Location;
import android.os.Bundle;

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

import java.util.Map;

/**
 * Created by hagn on 11/5/15.
 */
public class RNGLocationModule extends ReactContextBaseJavaModule implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String REACT_CLASS = "RNGLoc";
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private ReactApplicationContext mReactContext;

    public RNGLocationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        try {
            mGoogleApiClient = new GoogleApiClient.Builder(reactContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            sendError("getLocation Error", "location_error");
        }
        mReactContext = reactContext;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        } catch (Exception e) {
            e.printStackTrace();
            sendError("getLocation Error", "location_error");
        }
    }

    @ReactMethod
    public void getLocation(Callback successCallback, Callback errorCallback) {
        try {
            double Longitude;
            double Latitude;

            Longitude = mLastLocation.getLongitude();
            Latitude = mLastLocation.getLatitude();

            successCallback.invoke(Longitude, Latitude);
        } catch(Exception e) {
            e.printStackTrace();
            sendError("getLocation Error", "location_error");
        }
    }

    private void sendError (String message, String type) {
        WritableMap error = Arguments.createMap();
        error.putString("message", message);
        error.putString("type", type);

        mReactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("locationError", error);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

}
