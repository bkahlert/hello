/* global config */
/* jshint -W033 */
(function (config) {
  'use strict'
  config.resolve.modules.push('src/main/resources/application.json')
  if (config.mode === 'development') {
    config.resolve.modules.push('src/main/resources/application.dev.json')
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
      proxy: {
        '/api': {
          'target': 'https://api.clickup.com',
          'secure': false,
        },
      }
    })
  }
})(config)
