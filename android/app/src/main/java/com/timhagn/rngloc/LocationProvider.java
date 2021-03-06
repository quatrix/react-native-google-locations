package com.timhagn.rngloc;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by benjakuben on 12/17/14.
 */
public class LocationProvider implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public boolean mAlreadyAskedForLocationPermission = false;

    /**
     * Location Callback interface to be defined in Module
     */
    public abstract interface LocationCallback {
        public abstract void handleNewLocation(Location location);
    }

    // Unique Name for Log TAG
    public static final String TAG = LocationProvider.class.getSimpleName();
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // Location Callback for later use
    private LocationCallback mLocationCallback;
    // Context for later use
    private Context mContext;
    // Main Google API CLient (Google Play Services API)
    private GoogleApiClient mGoogleApiClient;
    // Location Request for later use
    // Are we Connected?
    public Boolean connected;
    private LocationRequest mLocationRequest;
    private Activity mActivity;


    public LocationProvider(Context context, Activity activity, LocationCallback updateCallback) {
        mContext = context;
        mActivity = activity;
        mLocationCallback = updateCallback;

        // First we need to check availability of play services
        if (checkPlayServices()) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    /**
     * Method to verify google play services on the device
     * */
    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(mContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Log.i(TAG, GooglePlayServicesUtil.getErrorString(resultCode));
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    /**
     * Connects to Google Play Services - Location
     */
    public void connect() {
        mGoogleApiClient.connect();
    }

    /**
     * Disconnects to Google Play Services - Location
     */
    public void disconnect() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");

        connected = true;

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location != null) {
            mLocationCallback.handleNewLocation(location);
        }

        startUpdatingLocation();
    }

    private void startUpdatingLocation() {
        Intent lu = new Intent(mContext, LocationUpdaterService.class);
        LocationUpdaterService.setCallback(this);
        mContext.startService(lu);

        AlarmManager alarm = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        alarm.set(
            alarm.RTC_WAKEUP,
            System.currentTimeMillis(),
            PendingIntent.getService(mContext, 0, new Intent(mContext, LocationUpdaterService.class), 0)
        );
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended...");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */

        if (connectionResult.hasResolution() && mContext instanceof Activity) {
            try {
                Activity activity = (Activity)mContext;
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(activity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            /*
             * Thrown if Google Play services canceled the original
             * PendingIntent
             */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    public void requestLocation() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setExpirationDuration(30 * 1000)
                .setInterval(60 * 1000)
                .setFastestInterval(10 * 1000)
                .setNumUpdates(1);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        builder.setAlwaysShow(true);
        final LocationProvider outer = this;
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, outer);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        if (mAlreadyAskedForLocationPermission)
                            return;

                        try {
                            status.startResolutionForResult(mActivity, RNGLocationModule.REQUEST_LOCATION_PERMISSIONS);
                            mAlreadyAskedForLocationPermission = true;
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                }
            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {
        mLocationCallback.handleNewLocation(location);
    }
}



