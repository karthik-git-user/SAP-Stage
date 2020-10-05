/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
const {
    compose,
    webpack: {
        multiEntry
    }
} = require('../../smartedit-build/builders');

const {
    smarteditcontainer
} = require('../../smartedit-build/config/webpack/webpack.e2e.shared.config');

const paths = require('../paths');

module.exports = compose(
    multiEntry('jsTarget', paths.tests.outerE2ETestFiles)
)(smarteditcontainer);
