/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* jshint unused:false, undef:false */
module.exports = function() {

    /***
     *  Naming:
     *  File or Files masks should end in File or Files,
     *  ex: someRoot.path.myBlaFiles = /root/../*.*
     *
     *  General rules:
     *  No copy paste
     *  No duplicates
     *  Avoid specific files when possible, try to specify folders
     *  What happens to smarteditcep, happens to smarteditcepContainer
     *  Try to avoid special cases and exceptions
     */
    var lodash = require('lodash');

    var paths = {};

    paths.tests = {};

    paths.tests.root = 'jsTests';
    paths.tests.testsRoot = paths.tests.root + '/tests';
    paths.tests.smarteditcepContainerTestsRoot = paths.tests.testsRoot + '/smarteditcepContainer';
    paths.tests.smarteditcepContainere2eTestsRoot = paths.tests.smarteditcepContainerTestsRoot + '/e2e';

    paths.tests.smarteditcepe2eTestFiles = paths.tests.root + '/e2e/**/*Test.js';
    paths.tests.smarteditcepContainere2eTestFiles = paths.tests.smarteditcepContainere2eTestsRoot + '/**/*Test.js';

    paths.tests.outerE2ETestFiles = paths.tests.smarteditcepContainere2eTestsRoot + '/**/outer*.ts';

    paths.tests.allE2eTSMocks = [
        paths.tests.outerE2ETestFiles
    ];

    paths.e2eFiles = [
        paths.tests.smarteditcepe2eTestFiles,
        paths.tests.smarteditcepContainere2eTestFiles
    ];

    /**
     * Code coverage
     */
    paths.coverage = {
        dir: './jsTarget/test/coverage',
        smarteditDirName: 'smartedit',
        smarteditcontainerDirName: 'smarteditcontainer',
        smarteditcommonsDirName: 'smarteditcommons'
    };

    return paths;

}();
