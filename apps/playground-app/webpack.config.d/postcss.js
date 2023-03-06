/* global config */
// noinspection JSUnnecessarySemicolon
;(function (config) {
  'use strict';
  const insertAfter = 'css-loader';
  const postcssLoader = {
    loader: 'postcss-loader',
    options: {}
  };

  config.module.rules = config.module.rules.map((value) => {
    if (value.use) {
      let pos = value.use.findIndex(element => element === insertAfter || element.loader === insertAfter);
      if (pos >= 0) {
        value.use.splice(pos + 1, 0, postcssLoader);
      }
    }
    return value;
  });

  const fs = require('fs');
  const path = require('path');
  const resources = ['.browserslistrc', 'postcss.config.js', 'tailwind.config.js'];
  resources.forEach((resource) => {
    const kotlinResource = path.resolve(__dirname, 'kotlin', resource);
    fs.copyFile(kotlinResource, path.resolve(__dirname, resource), (err) => {
      if (err) {throw err;}
    });
  });
})(config);
