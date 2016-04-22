# react-native-google-location

Location acquisition through Google Play Services.

### Installation

#### If you have not done - Install Google Play APK

Check [here](https://developers.google.com/android/guides/setup) 

#### Install the npm package
```bash
npm i --save react-native-google-location
```

Then you must install the native dependencies. You can use [`rnpm`](https://github.com/rnpm/rnpm) to
add native dependencies automatically (you still have to add permissions to your Manifest file, see _"Add Permissions and used Google API to your Project"_):

```bash
rnpm link
```

or do it manually as described below:

### Manual Installation: Add it to your android project

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


**Newer versions of React Native**
```java
...
import com.timhagn.rngloc.RNGLocation;  // <--- import
...
public class MainActivity extends ReactActivity {
 ....
 @Override
 protected List<ReactPackage> getPackages() {
   return Arrays.<ReactPackage>asList(
     new MainReactPackage(),
     new RNGLocation() // <---- and This!
   );
 }
}
```

**Older versions of React Native**

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

#### Add Permissions and used Google API to your Project

Add this to your AndroidManifest file;

``` xml
// file: android/app/src/main/AndroidManifest.xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```
Make sure this goes at the bottom of the `<application>` tag.
``` xml
	<uses-library android:name="com.google.android.maps" />
	<meta-data
        android:name="com.google.android.gms.version"
        android:value="@integer/google_play_services_version" />
```

## Example
```javascript
'use strict';

var React = require('react-native');

// Import react-native-google-location
import RNGLocation from 'react-native-google-location';

var {
  Component,
  AppRegistry,
  // DeviceEventEmitter for registering of the Callback-Listener
  DeviceEventEmitter,
  StyleSheet,
  Text,
  View,
} = React;

export default class RNGLocationExample extends Component {
  constructor(props) {
    super(props);
    // Create and Reset initial State Longitude (lng) and Latitude (lat)
    this.state = {
      lng: 0.0, 
      lat: 0.0,
    };

    if (!this.evEmitter) {
      // Register Listener Callback - has to be removed later
      this.evEmitter = DeviceEventEmitter.addListener('updateLocation', this.onLocationChange.bind(this));
      // Initialize RNGLocation
      RNGLocation.getLocation();
    }
  }

  onLocationChange (e: Event) {
    this.setState({
      lng: e.Longitude, 
      lat: e.Latitude 
    });
  }

  componentWillUnmount() {
    // Stop listening for Events
    this.evEmitter.remove();
  }

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.location}>
          Lng: {this.state.lng} Lat: {this.state.lat}
        </Text>
      </View>
    );
  }
}

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
