package com.timhagn.rngloc;

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
    @Override
    public List<NativeModule> createNativeModules( ReactApplicationContext reactContext) {
        return Arrays.<NativeModule>asList(
                new RNGLocationModule(reactContext)
        );
    }
    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }
    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactApplicationContext) {
        return Collections.emptyList();
    }
}
