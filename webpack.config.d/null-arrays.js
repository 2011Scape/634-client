// Not part of the original client: runs null-arrays-loader.js over the compiled Kotlin modules
// (milestone 5; see that file). This file is appended to the generated webpack.config.js in
// build/js/packages/<name>/, four directories below the project.
;(function () {
    const path = require('path')
    config.module.rules.push({
        test: /\.m?js$/,
        include: path.resolve(__dirname, 'kotlin'),
        use: [{ loader: path.resolve(__dirname, '../../../../webpack.config.d/null-arrays-loader.js') }],
    })
})()
