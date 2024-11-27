module.exports = {
  outputDir: '../../../target/classes/static',

  chainWebpack: config => {
    config.plugins.delete('pwa')
    config.plugins.delete('prefetch')
  }
}
