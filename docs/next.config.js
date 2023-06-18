const withNextra = require('nextra')({
  theme: 'nextra-theme-docs',
  themeConfig: './theme.config.jsx',
})

const isProd = process.env.ENVIRONMENT === 'production'

module.exports = withNextra({
  images: {
    loader: 'akamai',
    path: '',
  },
  output: 'export',
  basePath: isProd ? '/scala-kube-operator-scaffold.g8' : undefined,
})
