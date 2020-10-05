/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* jshint esversion: 6 */
const webpack = require('webpack');
const path = require('path');

module.exports = {

    // ########################################
    // ##### Configured through CLI with ######
    // "mode": "production",   
    // "devtool": "source-map",
    // ########################################
    "entry": path.resolve(__dirname, "./src/ssoAuthenticationInjector.ts"),

    "output": {
        "path": path.resolve(process.cwd(), "web/webroot/smarteditcep/js"),
        "filename": "ssoauthenticationinjector.js"
    },
    "resolve": {
        "modules": [
            path.resolve(process.cwd(), "./node_modules"),
            path.resolve(__dirname, "./src"),
            path.resolve(process.cwd(), "web/features")
        ],
        "extensions": [
            ".ts",
            ".js"
        ],
        "alias": {
            "smarteditcommons": path.resolve(process.cwd(), "smartedit-build/lib/smarteditcommons/src"),
            "smarteditcepcontainer": path.resolve(process.cwd(), "web/features/smarteditcepContainer"),
        }
    },
    "stats": {
        "assets": true,
        "colors": true,
        "modules": true,
        "reasons": true,
        "errorDetails": true,
        "warningsFilter": (warning) => {
            // workaround for:
            // https://github.com/TypeStrong/ts-loader/issues/751
            // https://github.com/angular/angular/issues/21560
            return /export '.*'( \(reexported as '.*'\))? was not found in/.test(warning) ||
                /System.import/.test(warning);
        }
    },
    "module": {
        "rules": [{
            "test": /\.ts$/,
            "loader": "ts-loader",
            "options": {
                "configFile": path.resolve(__dirname, "./tsconfig.json")
            }
        }]
    },
    "plugins": [
        new webpack.ContextReplacementPlugin(
            /angular(\\|\/)core/, // The (\\|\/) piece accounts for path separators in *nix and Windows
            require('path').resolve(__dirname, './src'), {}
        )
    ]
};
