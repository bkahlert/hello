;(function (config) {
  'use strict'

  config.module.rules.push({
    test: /\.(less)$/,
    use: [{
      loader: 'style-loader',
      options: {
        injectType: 'lazyStyleTag',
      }
    }, {
      loader: 'css-loader',
      options: {}
    }, {
      loader: 'less-loader',
      options: {}
    }],
  })
})(config)
