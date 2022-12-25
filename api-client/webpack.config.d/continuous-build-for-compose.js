// Fix continuous Gradle builds in combination with Compose for Web (`./gradlew jsBrowserRun --continuous`)
/* global config */
// noinspection JSUnnecessarySemicolon
;(function (config) {
  'use strict';
  if (config.mode === 'development') {
    // see https://webpack.js.org/configuration/watch/
    config.watch = false;
    config.watchOptions = config.watchOptions || {};
    Object.assign(config.watchOptions, config.watchOptions, {
      ignored: '**/node_modules',
      aggregateTimeout: 200,
      poll: 1000,
    });
  }
})(config);
