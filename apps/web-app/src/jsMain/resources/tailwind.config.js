// default at https://github.com/tailwindlabs/tailwindcss/blob/master/stubs/config.full.js

const _ = require('underscore')
const colors = require('tailwindcss/colors')
const chroma = require('chroma-js')
const colorScale = (...colors) => chroma.scale(colors).mode('oklch').colors(11)
const colorShades = (color) =>
  [50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 950].reduce((acc, shade) => _.extend(acc, { [shade]: color.set('oklch.l', shade / 1000).hex() }), {
    DEFAULT: color.hex(),
  })

const glass = {
  light: chroma(colors.slate['50']).alpha(0.25),
  dark: chroma(colors.slate['800']).alpha(0.45),
}

const swatches = {
  blue: chroma.oklch(0.6985, 0.133, 232.37),
  magenta: chroma.oklch(0.5424, 0.204, 356.06),
  yellow: chroma.oklch(0.8997, 0.192, 108.08),
}

const colorScales = _.extend(
  // { glass-light: [...], glass-dark: [...] }
  Object.entries(glass).reduce(
    (acc, variant) => _.extend(acc, { ['glass-' + variant[0]]: colorScale(variant[1], variant[1].alpha(variant[1].alpha() - 0.05)) }),
    {}
  ),
  // { swatch-blue-magenta: [blue, ..., magenta], ... }
  Object.keys(swatches)
    .map((self, _, others) => others.map((other) => [self, other]))
    .flat()
    .filter((stops) => stops[0] !== stops[1])
    .reduce((acc, stops) => _.extend(acc, { ['swatch-' + stops[0] + '-' + stops[1]]: colorScale(swatches[stops[0]], swatches[stops[1]]) }), {})
)

/** @type {import('tailwindcss').Config} */
module.exports = {
  content: {
    relative: true,
    files: [
      '*.{html,js}',
      './kotlin/**/*.{html,js}',
    ],
  },
  theme: {
    extend: {
      backgroundImage: _.extend(
        {
          'gradient-290': 'linear-gradient(290deg, var(--tw-gradient-stops))',
        },
        // { bg-swatch-blue-magenta: linear-gradient(rgb(40, 171, 226), ..., rgb(194, 31, 115)), ... }
        _.mapObject(colorScales, (stops) => `linear-gradient(${stops.join(',')})`)
      ),
      colors: _.extend(
        {
          bg: { light: colors.slate['50'], dark: colors.slate['800'] },
          text: { light: colors.slate['900'], dark: colors.slate['100'] },
          'glass-text': { light: colors.slate['900'], dark: colors.slate['100'] },
        },
        // { text-swatch-blue: { 50: "#...", 100: "#...", ... }, ... }
        Object.entries(swatches).reduce((acc, swatch) => _.extend(acc, { [`swatch-${swatch[0]}`]: colorShades(swatch[1]) }), {}),
        // { text-swatch-blue-magenta: { 0: "#...", 10: "#...", ..., 100: "#..." }, ... }
        _.mapObject(colorScales, (stops) => stops.reduce((acc, color, i) => _.extend(acc, { [i * 10]: color }), {}))
      ),
      screens: {
        // CSS device and capability media queries, https://css-tricks.com/touch-devices-not-judged-size/
        'pointer-course': { raw: '(pointer: coarse)' },
        'pointer-fine': { raw: '(pointer: fine)' },
        'pointer-none': { raw: '(pointer: none)' },
        'hover-hover': { raw: '(hover: hover)' },
        'hover-none': { raw: '(hover: none)' },
        'any-pointer-course': { raw: '(any-pointer: coarse)' },
        'any-pointer-fine': { raw: '(any-pointer: fine)' },
        'any-pointer-none': { raw: '(any-pointer: none)' },
        'any-hover-hover': { raw: '(any-hover: hover)' },
        'any-hover-on-demand': { raw: '(any-hover: on-demand)' },
        'any-hover-none': { raw: '(any-hover: none)' },
      },
      spacing: {
        'safe-top': 'env(safe-area-inset-top)',
        'safe-bottom': 'env(safe-area-inset-bottom)',
        'safe-left': 'env(safe-area-inset-left)',
        'safe-right': 'env(safe-area-inset-right)',
      },
    },
  },
  plugins: [
    require('@tailwindcss/typography'),
    require('@tailwindcss/forms'),
    require('@tailwindcss/aspect-ratio'),
    require('tailwind-heropatterns')({
      variants: [],
      patterns: [],
      colors: _.extend(
        {
          default: colors.slate['500'],
          light: glass.light.hex(),
          dark: glass.dark.hex(),
        },
        // { bg-hero-diagonal-stripes-blue: ..., ... }
        _.mapObject(swatches, (swatch) => swatch.hex())
      ),
      opacity: {
        default: 0.4,
      },
    }),
    require('tailwindcss-animate'),
  ],
}
