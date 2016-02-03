/**
 * @providesModule RNGLocation
 */

'use strict';

/**
 * This Module is a Quick and Dirty Hack to return Location from Google Play Services Location for Android
 */

var { NativeModules } = require('react-native');
module.exports = NativeModules.RNGLocation;