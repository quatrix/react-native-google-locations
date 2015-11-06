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
public class RNGLocationModule extends ReactContextBaseJavaModule  {

    public static final String REACT_CLASS = "RNGLoc";
    private ReactApplicationContext mReactContext;
    private Location mLastLocation;
    private Boolean mIsBound;

    public RNGLocationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
    }
    private GPLocationService mBoundService;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((GPLocationService.LocalBinder)service).getService();
            mLastLocation = mBoundService.getLocation();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
        }
    };

    public void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        mReactContext.bindService(new Intent(mBoundService,
                GPLocationService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;

    }

    public void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            mReactContext.unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    public void connectGPLoc() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //Check that an app exists to receive the intent
        if (intent.resolveActivity(mReactContext.getPackageManager()) != null) {
            mReactContext.startActivity(intent);
        }
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
                sendError("getLocation Error", "location_error");
            }
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

}
