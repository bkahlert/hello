/* global config */
// noinspection JSUnnecessarySemicolon
;(function (config) {
  'use strict';

  if (config.mode === 'development') {
    config.devServer = config.devServer || {};
    Object.assign(config.devServer, config.devServer, {
      allowedHosts: ['all'],
      client: {
        logging: 'info',
        overlay: {
          errors: true,
          warnings: false,
        },
        progress: false,
      },
      // see https://github.com/chimurai/http-proxy-middleware#proxycontext-config
      proxy: {
        '/api.clickup.com': {
          target: 'https://api.clickup.com',
          secure: false,
          pathRewrite: {
            '^/api.clickup.com': '/',
          },
        },
      }
    });
  }
})(config);
