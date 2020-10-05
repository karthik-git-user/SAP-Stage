/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {

    return {
        targets: [
            'e2e',
        ],
        config: function(data, conf) {
            var paths = require('../paths');

            conf.e2e = {
                src: [
                    'jsTarget/' + paths.tests.allE2eTSMocks
                ]
            };
            return conf;
        }
    };
};
