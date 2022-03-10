;(function (config) {
  config.devtool = 'eval-source-map'
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
})(config)
console.warn(config)
