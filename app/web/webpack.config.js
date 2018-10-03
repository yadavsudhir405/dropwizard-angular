const path = require("path");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const CopyWebpackPlugin = require("copy-webpack-plugin");


const entry = {
    app: "./src/index.ts",
};
const  output = {
    filename: '[name].js',
    chunkFilename: '[id].chunk.js',
    path: path.resolve(__dirname, 'dist'),

};
const rules = [
    {
        test : /\.tsx?$/,
        use: ['awesome-typescript-loader', 'angular2-template-loader'],
        exclude: /node_modules/
    },
    {
        test:/\.html$/,
        use: 'html-loader',
        exclude: path.resolve(__dirname,"src/index.html")
    },
    {
        test:/\.(jpg|png)/,
        use: [
            {
                loader: 'file-loader',
                options: {
                    name: '[path][name].[ext]'
                }
            }
        ]
    }

];
const plugins = [
    new HtmlWebpackPlugin({
        minify: false,
        template: path.resolve(__dirname, "src/index.html"),
    })
];
const config = {
    "entry": entry,
    "output": output,
    "module":{
        rules: rules
    },
    "plugins": plugins,
    "resolve": {
        extensions: ['.ts','.js']
    },
    devtool: "inline-source-map",
    devServer: {
        contentBase: path.join(__dirname, 'dist'),
        historyApiFallback: true,
        port: 8081,
    }
};

module.exports = config;
