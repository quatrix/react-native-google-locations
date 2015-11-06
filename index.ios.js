'use strict';

let React = require('react-native');

let {
  Component,
} = React;

let ERROR = 'RNGLocations is not available in iOS - use Geolocation Polyfill.';

class RNGLocations extends Component {
  constructor (props) {
    super(props);
    console.log(ERROR);
  }

  getLocation () { console.error(ERROR) }
}

module.exports = RNGLocations;
