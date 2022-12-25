/* global config */
// noinspection JSUnnecessarySemicolon
;(function (config) {
  'use strict';

  config.resolve.modules.push('src/main/resources/application.json');
  // see https://webpack.js.org/guides/asset-modules/
  config.module.rules.push({
    test: /\.mp3$/i,
    type: 'asset/inline'
  });

  if (config.mode === 'development') {
    config.resolve.modules.push('src/main/resources/application.dev.json');
  }
})(config);
