/* global config */
/* jshint -W033 */
(function (config) {
  'use strict'
  config.resolve.modules.push('src/main/resources/application.json')
  // see https://webpack.js.org/guides/asset-modules/
  config.module.rules.push({
    test: /\.mp3$/i,
    type: 'asset/inline'
  })
  if (config.mode === 'development') {
    config.resolve.modules.push('src/main/resources/application.dev.json')

    // see https://webpack.js.org/configuration/watch/
    config.watch = false
    config.watchOptions = config.watchOptions || {}
    Object.assign(config.watchOptions, config.watchOptions, {
      ignored: '**/node_modules',
      aggregateTimeout: 200,
      poll: 1000,
    })

    config.devServer = config.devServer || {}
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
      open: false,
      // see https://github.com/chimurai/http-proxy-middleware#proxycontext-config
      proxy: {
        '/api': {
          target: 'https://api.clickup.com',
          secure: false,
        },
        '/attachments': {
          target: 'https://attachments.clickup.com',
          changeOrigin: true,
          ws: true,
          pathRewrite: {
            '^/attachments': '/',
          },
          secure: false,
        },
      }
    })
  }
})(config)
