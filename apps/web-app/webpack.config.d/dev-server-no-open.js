// Don't open (yet another) browser tab on each build
/* global config */
// noinspection JSUnnecessarySemicolon
;(function (config) {
  'use strict';
  if (config.mode === 'development') {
    config.devServer = config.devServer || {};
    Object.assign(config.devServer, config.devServer, {
      open: false
    });
  }
})(config);