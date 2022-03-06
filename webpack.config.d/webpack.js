if (config.devServer) {
  config.devtool = 'eval-cheap-source-map'

  // TODO remove
  config.devServer.headers = {
    'Access-Control-Allow-Origin': 'http://localhost:8080',
  }

  config.devServer.allowedHosts = ['all']

  config.devServer.proxy = {
    '/api': {
      'target': 'https://api.clickup.com',
      'secure': false,
    },
  }

  console.warn(config.devServer)
}
