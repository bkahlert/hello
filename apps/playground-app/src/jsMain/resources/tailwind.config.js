// default at https://github.com/tailwindlabs/tailwindcss/blob/master/stubs/config.full.js

const colors = require('tailwindcss/colors')
const chroma = require('chroma-js')

const blue = chroma.oklch(0.6985, 0.133, 232.37)
const magenta = chroma.oklch(0.5424, 0.204, 356.06)
const yellow = chroma.oklch(0.8997, 0.192, 108.08)

const helloBlue = {
  '50': chroma.oklch(0.05, 0.133, 233.02).hex(),
  '100': chroma.oklch(0.1, 0.133, 233.02).hex(),
  '200': chroma.oklch(0.2, 0.133, 233.02).hex(),
  '300': chroma.oklch(0.3, 0.133, 233.02).hex(),
  '400': chroma.oklch(0.4, 0.133, 233.02).hex(),
  '500': chroma.oklch(0.5, 0.133, 233.02).hex(),
  '600': chroma.oklch(0.6, 0.133, 233.02).hex(),
  DEFAULT: blue.hex(),
  '700': chroma.oklch(0.7, 0.133, 233.02).hex(),
  '800': chroma.oklch(0.8, 0.133, 233.02).hex(),
  '900': chroma.oklch(0.9, 0.133, 233.02).hex(),
}

const helloMagenta = {
  '50': chroma.oklch(0.05, 0.204, 356.06).hex(),
  '100': chroma.oklch(0.1, 0.204, 356.06).hex(),
  '200': chroma.oklch(0.2, 0.204, 356.06).hex(),
  '300': chroma.oklch(0.3, 0.204, 356.06).hex(),
  '400': chroma.oklch(0.4, 0.204, 356.06).hex(),
  '500': chroma.oklch(0.5, 0.204, 356.06).hex(),
  DEFAULT: magenta.hex(),
  '600': chroma.oklch(0.6, 0.204, 356.06).hex(),
  '700': chroma.oklch(0.7, 0.204, 356.06).hex(),
  '800': chroma.oklch(0.8, 0.204, 356.06).hex(),
  '900': chroma.oklch(0.9, 0.204, 356.06).hex(),
}

const helloYellow = {
  '50': chroma.oklch(0.05, 0.192, 108.08).hex(),
  '100': chroma.oklch(0.1, 0.192, 108.08).hex(),
  '200': chroma.oklch(0.2, 0.192, 108.08).hex(),
  '300': chroma.oklch(0.3, 0.192, 108.08).hex(),
  '400': chroma.oklch(0.4, 0.192, 108.08).hex(),
  '500': chroma.oklch(0.5, 0.192, 108.08).hex(),
  '600': chroma.oklch(0.6, 0.192, 108.08).hex(),
  '700': chroma.oklch(0.7, 0.192, 108.08).hex(),
  '800': chroma.oklch(0.8, 0.192, 108.08).hex(),
  DEFAULT: yellow.hex(),
  '900': chroma.oklch(0.9, 0.192, 108.08).hex(),
}

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
      backgroundImage: {
        'gradient-290': 'linear-gradient(290deg, var(--tw-gradient-stops))',
        brand: `linear-gradient(290deg, ${chroma.scale([magenta, blue]).mode('oklch').padding(0.15).colors(15).join(',')})`,
        test: `linear-gradient(290deg, ${chroma.scale([blue, yellow]).mode('oklch').padding(0.15).colors(15).join(',')})`,
      },
      colors: {
        'hello-blue': helloBlue,
        'hello-magenta': helloMagenta,
        'hello-yellow': helloYellow,
      },
      screens: {
        // CSS device and capability media queries, https://css-tricks.com/touch-devices-not-judged-size/
        'pointer-course': { 'raw': '(pointer: coarse)' },
        'pointer-fine': { 'raw': '(pointer: fine)' },
        'pointer-none': { 'raw': '(pointer: none)' },
        'hover-hover': { 'raw': '(hover: hover)' },
        'hover-none': { 'raw': '(hover: none)' },
        'any-pointer-course': { 'raw': '(any-pointer: coarse)' },
        'any-pointer-fine': { 'raw': '(any-pointer: fine)' },
        'any-pointer-none': { 'raw': '(any-pointer: none)' },
        'any-hover-hover': { 'raw': '(any-hover: hover)' },
        'any-hover-on-demand': { 'raw': '(any-hover: on-demand)' },
        'any-hover-none': { 'raw': '(any-hover: none)' },
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
    require('@tailwindcss/line-clamp'),
    require('@tailwindcss/aspect-ratio'),
    require('tailwind-heropatterns')({
      variants: [],
      patterns: [],
      colors: {
        default: colors.slate['500'],
        blue: helloBlue.DEFAULT,
        magenta: helloMagenta.DEFAULT,
        yellow: helloYellow.DEFAULT,
      },
      opacity: {
        default: 0.4
      }
    }),
  ],
}
