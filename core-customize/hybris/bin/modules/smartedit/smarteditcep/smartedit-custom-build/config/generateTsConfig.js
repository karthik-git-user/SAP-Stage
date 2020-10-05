/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* jshint esversion: 6 */
module.exports = function() {

    return {
        config: function(data, conf) {
            const lodash = require('lodash');

            const smarteditcepPaths = {
                "smarteditcep/*": ["web/features/smarteditcep/*"],
                "smarteditcepcommons": ["web/features/smarteditcepcommons"],
                "smarteditcepcommons*": ["web/features/smarteditcepcommons*"]
            };

            const yssmarteditmoduleContainerPaths = {
                "smarteditcepcontainer/*": ["web/features/smarteditcepContainer/*"],
                "smarteditcepcommons": ["web/features/smarteditcepcommons"],
                "smarteditcepcommons*": ["web/features/smarteditcepcommons*"]
            };

            const commonsLayerInclude = ["../../jsTarget/web/features/smarteditcepcommons/**/*"];
            const innerLayerInclude = commonsLayerInclude.concat(["../../jsTarget/web/features/smarteditcep/**/*"]);
            const outerLayerInclude = commonsLayerInclude.concat(["../../jsTarget/web/features/smarteditcepContainer/**/*"]);
            const commonsLayerTestInclude = ["../../jsTests/tests/smarteditcepcommons/unit/**/*"];
            const innerLayerTestInclude = commonsLayerTestInclude.concat(["../../jsTests/tests/smarteditcep/unit/features/**/*"]);
            const outerLayerTestInclude = commonsLayerTestInclude.concat(["../../jsTests/tests/smarteditcepContainer/unit/features/**/*"]);

            function addYsmarteditmodulePaths(conf) {
                lodash.merge(conf.compilerOptions.paths, lodash.cloneDeep(smarteditcepPaths));
            }

            function addYsmarteditmoduleContainerPaths(conf) {
                lodash.merge(conf.compilerOptions.paths, lodash.cloneDeep(yssmarteditmoduleContainerPaths));
            }

            // PROD
            addYsmarteditmodulePaths(conf.generateProdSmarteditTsConfig.data);
            addYsmarteditmoduleContainerPaths(conf.generateProdSmarteditContainerTsConfig.data);
            conf.generateProdSmarteditTsConfig.data.include = innerLayerInclude;
            conf.generateProdSmarteditContainerTsConfig.data.include = outerLayerInclude;

            // DEV
            addYsmarteditmodulePaths(conf.generateDevSmarteditTsConfig.data);
            addYsmarteditmoduleContainerPaths(conf.generateDevSmarteditContainerTsConfig.data);
            conf.generateDevSmarteditTsConfig.data.include = innerLayerInclude;
            conf.generateDevSmarteditContainerTsConfig.data.include = outerLayerInclude;

            // KARMA
            addYsmarteditmodulePaths(conf.generateKarmaSmarteditTsConfig.data);
            addYsmarteditmoduleContainerPaths(conf.generateKarmaSmarteditContainerTsConfig.data);
            conf.generateKarmaSmarteditTsConfig.data.include = innerLayerInclude.concat(innerLayerTestInclude);
            conf.generateKarmaSmarteditContainerTsConfig.data.include = outerLayerInclude.concat(outerLayerTestInclude);

            // IDE
            addYsmarteditmodulePaths(conf.generateIDETsConfig.data);
            addYsmarteditmoduleContainerPaths(conf.generateIDETsConfig.data);
            conf.generateIDETsConfig.data.include = conf.generateIDETsConfig.data.include.concat(["../../jsTests/tests/**/unit/**/*"]);

            return conf;
        }
    };

};
