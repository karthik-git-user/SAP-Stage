/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {
    return {
        targets: [
            'sources',
            'dev'
        ],
        config: function(data, conf) {
            var paths = require('../paths');
            return {
                sources: {
                    files: [{
                        expand: true,
                        flatten: false,
                        src: [
                            'web/features/**/*.+(js|ts|scss|html)'
                        ],
                        dest: 'jsTarget/'
                    }]
                },
                dev: {
                    expand: true,
                    flatten: true,
                    src: ['jsTarget/*.js*(.map)'],
                    dest: 'web/webroot/smarteditcep/js'
                },
                e2e: {
                    expand: true,
                    flatten: false,
                    src: paths.tests.allE2eTSMocks,
                    dest: 'jsTarget/'
                }
            };
        }
    };
};
