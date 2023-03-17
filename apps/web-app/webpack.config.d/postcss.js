;(function (config) {
  const insertAfter = 'css-loader'
  const postcssLoader = {
    loader: 'postcss-loader',
  }

  config.module.rules = config.module.rules.map((value) => {
    if (value.use) {
      let pos = value.use.findIndex(element => element === insertAfter || element.loader === insertAfter)
      if (pos >= 0) {
        value.use.splice(pos + 1, 0, postcssLoader)
      }
    }
    return value
  })

  const fs = require('fs')
  const path = require('path')
  const resources = ['.browserslistrc', 'postcss.config.js', 'tailwind.config.js']
  resources.forEach((resource) => {
    const dest = path.resolve(__dirname, resource)
    if (!fs.existsSync(dest)) {
      const src = path.resolve(__dirname, 'kotlin', resource)
      fs.copyFile(src, dest, (err) => {
        if (err) {
          throw err
        }
      })
    }
  })
})(config)
