# react-native-google-location

Location acquisition through Google Play Services.

### Installation

```bash
npm i --save react-native-google-location
```

### Add it to your android project

* In `android/setting.gradle`

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
