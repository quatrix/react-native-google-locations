package com.timhagn.rngloc;

import android.content.Intent;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by hagn on 11/6/15.
 */
public class RNGLocation implements ReactPackage {

    private RNGLocationModule mModuleInstance;

    @Override
    public List<NativeModule> createNativeModules( ReactApplicationContext reactContext) {
        mModuleInstance = new RNGLocationModule(reactContext);
        return Arrays.<NativeModule>asList(mModuleInstance);
    }

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }
    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactApplicationContext) {
        return Collections.emptyList();
    }

    public boolean handleActivityResult(final int requestCode, final int resultCode, final Intent data) {
        return mModuleInstance.handleActivityResult(requestCode, resultCode, data);
    }
}
