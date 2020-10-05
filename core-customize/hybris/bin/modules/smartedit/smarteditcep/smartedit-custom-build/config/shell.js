/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {
    return {
        targets: [
            'prodbuildssoauthenticationinjector',
            'devbuildssoauthenticationinjector'
        ],
        config: function(data, conf) {
            const paths = require('../paths');

            return {
                ...conf,
                prodbuildssoauthenticationinjector: {
                    command: 'webpack --mode production --config web/features/ssoAuthentication/webpack.conf.js'
                },
                devbuildssoauthenticationinjector: {
                    command: 'webpack --mode development --devtool source-map --config web/features/ssoAuthentication/webpack.conf.js'
                }
            };
        }
    };
};
