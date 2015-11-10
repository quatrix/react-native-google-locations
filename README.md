# react-native-google-location

Location acquisition through Google Play Services.

### Installation

#### If you have not done - Install Google Play APK

Check [here](https://developers.google.com/android/guides/setup) 

#### Install the npm package
```bash
npm i --save react-native-google-location
```

### Add it to your android project

* In `android/settings.gradle`

```gradle
...
include ':react-native-google-location', ':app'
project(':react-native-google-location').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-google-location/android/app')
```

* In `android/app/build.gradle`

```gradle
...
dependencies {
    ...
    compile project(':react-native-google-location')
}
```

* register module (in MainActivity.java)

```java
import com.timhagn.rngloc.RNGLocation;  // <--- import

public class MainActivity extends Activity implements DefaultHardwareBackBtnHandler {
  ......

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mReactRootView = new ReactRootView(this);

    mReactInstanceManager = ReactInstanceManager.builder()
      .setApplication(getApplication())
      .setBundleAssetName("index.android.bundle")
      .setJSMainModuleName("index.android")
      .addPackage(new MainReactPackage())
      .addPackage(new RNGLocation()) // <-- Register package here
      .setUseDeveloperSupport(BuildConfig.DEBUG)
      .setInitialLifecycleState(LifecycleState.RESUMED)
      .build();

    mReactRootView.startReactApplication(mReactInstanceManager, "example", null);

    setContentView(mReactRootView);
  }

  ......

}
```

#### Add your Google API Key and permissions to your Project

Add this to your AndroidManifest file;

[More info on API Keys can be found here](https://developers.google.com/maps/documentation/android-api/signup?hl=en)

``` xml
// file: android/app/src/main/AndroidManifest.xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

<uses-permission android:name="android.permission.ACCESS_GPS" />
<uses-permission android:name="android.permission.ACCESS_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```
Make sure this goes at the bottom of the `<application>` tag.
``` xml
	<uses-library android:name="com.google.android.maps" />
	<meta-data
	    android:name="com.google.android.geo.API_KEY"
	    android:value="YOUR_API_KEY"/>
```

## Example
```javascript
'use strict';

var React = require('react-native');

// For registering the Callback-Listener
var { DeviceEventEmitter } = require('react-native');

var RNGLocation = require('react-native-google-location');

var {
  AppRegistry,
  StyleSheet,
  Text,
  View,
} = React;

var RNGLocationExample = React.createClass({
  // Create and Reset initial State Longitude (lng) and Latitude (lat)	
  getInitialState: function() { return { lng: 0.0, lat: 0.0}; },

  componentDidMount: function() {
  	  // Register Listener Callback !Important!
      DeviceEventEmitter.addListener('updateLocation', function(e: Event) {
          this.setState({lng: e.Longitude, lat: e.Latitude });
      }.bind(this));
      // Initialize RNGLocation
      RNGLocation.getLocation();
  },

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.location}>
          Lng: {this.state.lng} Lat: {this.state.lat}
        </Text>
      </View>
    );
  }
});

var styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  location: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  }
});

AppRegistry.registerComponent('RNGLocationExample', () => RNGLocationExample);
```
