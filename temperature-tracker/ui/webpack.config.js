var path = require('path');
var webpack = require("webpack");

module.exports = {
    entry: {
        app: './src/app.js',
    },
    plugins: [
        new webpack.ProvidePlugin({$: 'jquery', jQuery: 'jquery'})
    ],
    output: {
        path: path.join(__dirname, '../src/main/webapp/'),
        filename: 'app.js'
    },
    devServer: {
        path: path.join(__dirname, '../src/main/webapp/'),
    },
    resolveLoader: {
        moduleExtensions: ["-loader"]
    },
    watch: true,
    module: {
        loaders: [
            {
                test: /.jsx?$/,
                loader: 'babel-loader',
                exclude: /node_modules/,
                query: {
                    presets: ['es2015', 'react', 'stage-2']
                }
            },
            {
                test: require.resolve("react"),
                loader: "expose-loader?React"
            },
            {
                test: require.resolve("react-dom"),
                loader: "expose-loader?ReactDOM"
            }
        ]
    },
    resolve: {
        alias: {
            app: path.resolve(__dirname, 'src'),
        }
    },
};
