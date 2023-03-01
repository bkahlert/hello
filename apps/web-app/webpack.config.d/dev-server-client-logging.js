// Don't open (yet another) browser tab on each build
/* global config */
// noinspection JSUnnecessarySemicolon
;(function (config) {
  'use strict';
  if (config.mode === 'development') {
    config.devServer = Object.assign({}, config.devServer || {}, {});
    config.devServer.client = Object.assign({}, config.devServer.client || {}, {
      logging: 'error',
      overlay: {
        errors: true,
        warnings: true,
      },
      progress: false,
    });
  }
})(config);
