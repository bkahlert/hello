with (config) {
  if (devServer) {
    devtool = 'eval-source-map'
    // devtool = 'eval-cheap-source-map'

    devServer.allowedHosts = ['all']
    devServer.client = {
      logging: 'info',
      overlay: {
        errors: true,
        warnings: false,
      },
      progress: false,
    }
    devServer.open = false
    devServer.proxy = {
      '/api': {
        'target': 'https://api.clickup.com',
        'secure': false,
      },
    }
  }
}

console.warn(config)
