// noinspection JSUnresolvedReference

;(function (config) {
  'use strict'

  config.resolve = config.resolve || {}
  config.resolve.fallback = config.resolve.fallback || {}
  config.resolve.fallback['path'] = false // node package required (but unused) by openai-client
  config.resolve.fallback['os'] = false // node package required (but unused) by openai-client
})(config)
