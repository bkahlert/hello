// default at https://github.com/tailwindlabs/tailwindcss/blob/master/stubs/config.full.js

const _ = require('underscore')
const filterKeys = (obj, fn) => _.extend({}, ...Object.keys(obj).map((key) => (fn(key) ? { [key]: obj[key] } : {})))
const mapEntries = (obj, fn, ...objects) => _.extend({}, ...Object.entries(obj).map((e) => fn(e)), ...objects)

const opacities = mapEntries([0, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.95, 1], ([, v]) => ({ [`${v * 100}`]: v }))
const defaultColors = filterKeys(require('tailwindcss/colors'), (name) => !/[A-Z]/.test(name)) // Filters deprecated colors
const defaultShades = Object.keys(defaultColors.red) // 50, 100, ..., 950
const chroma = require('chroma-js')
const shades = (color) => mapEntries(defaultShades, ([, shade]) => ({ [shade]: color.luminance(1 - shade / 1000).hex() }), { DEFAULT: color.hex() })
const flattenColors = (colors) =>
  mapEntries(colors, ([name, shades]) =>
    typeof shades === 'string' ? { [name]: shades } : mapEntries(shades, ([shade, color]) => ({ [`${name}-${shade}`]: color })),
  )

const colorGamuts = ['srgb', 'p3', 'rec2020']
const colorSpaces = ['srgb', 'hsl', 'hwb', 'srgb-linear', 'display-p3', 'lch', 'oklch', 'oklab', 'rec2020', 'a98-rgb', 'prophoto-rgb', 'xyz']
const swatches = mapEntries(
  {
    blue: chroma.oklch(0.6985, 0.133, 232.37),
    magenta: chroma.oklch(0.5424, 0.204, 356.06),
    yellow: chroma.oklch(0.8997, 0.192, 108.08),
  },
  ([name, color]) => ({ [`swatch-${name}`]: shades(color) }),
)

/** @type {import('tailwindcss').Config} */
module.exports = {
  content: {
    relative: true,
    files: ['*.{html,js}', './kotlin/**/*.{html,js}'],
  },
  theme: {
    // manual import because using `({ theme })` function leads to circular dependency
    backgroundImage: mapEntries(require('tailwindcss/defaultTheme').backgroundImage, ([k, v]) =>
      _.extend(
        { [k]: v },
        mapEntries(colorSpaces, ([, cs]) => ({
          [`${k}-in-${cs}`]: v
            .split('(')
            .map((s, i) => (i === 1 ? `in ${cs} ${s}` : s))
            .join('('),
        })),
      ),
    ),
    extend: {
      colors: swatches,
      textColor: { default: defaultColors.slate['900'], invert: defaultColors.slate['100'] },
      backgroundColor: { default: defaultColors.slate['50'], invert: defaultColors.slate['800'] },
      screens: _.extend(
        // For example `p3:`
        mapEntries(colorGamuts, ([, gamut]) => ({ [gamut]: { raw: `(color-gamut: ${gamut})` } })),
        // CSS device and capability media queries, https://css-tricks.com/touch-devices-not-judged-size/
        // For example `pointer-coarse:`
        mapEntries(
          {
            pointer: ['coarse', 'fine', 'none'],
            hover: ['hover', 'none'],
            'any-pointer': ['coarse', 'fine', 'none'],
            'any-hover': ['hover', 'on-demand', 'none'],
          },
          ([feature, values]) => mapEntries(values, ([, value]) => ({ [`${feature}-${value}`]: { raw: `(${feature}: ${value})` } })),
        ),
      ),
      // For example `p-safe-top`
      spacing: mapEntries(['top', 'bottom', 'left', 'right'], ([, side]) => ({ [`safe-${side}`]: `env(safe-area-inset-${side})` })),
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
        { default: defaultColors.gray['500'] },
        // { bg-hero-diagonal-stripes-red-50: ..., ... }
        flattenColors(defaultColors),
        // { bg-hero-diagonal-stripes-swatch-blue-50: ..., ... }
        flattenColors(swatches),
      ),
      opacity: _.extend(
        filterKeys(opacities, (k) => k === '40'),
        { default: 0.4 },
      ),
    }),
  ],
}
