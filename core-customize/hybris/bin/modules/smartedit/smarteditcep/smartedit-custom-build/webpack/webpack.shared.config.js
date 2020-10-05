/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
const {
    resolve
} = require('path');

const {
    group,
    webpack: {
        entry,
        alias
    }
} = require('../../smartedit-build/builders');

const commonsAlias = alias('smarteditcepcommons', resolve('./jsTarget/web/features/smarteditcepcommons'));

const smartedit = group(
    commonsAlias,
    alias('smarteditcep', resolve('./jsTarget/web/features/smarteditcep'))
);
const smarteditContainer = group(
    commonsAlias,
    alias('smarteditcepcontainer', resolve('./jsTarget/web/features/smarteditcepContainer')),
);

module.exports = {
    ySmarteditKarma: () => group(
        smartedit
    ),
    ySmarteditContainerKarma: () => group(
        smarteditContainer
    ),
    ySmartedit: () => group(
        smartedit,
        entry({
            smarteditcep: resolve('./jsTarget/web/features/smarteditcep/smarteditcep.ts')
        })
    ),
    ySmarteditContainer: () => group(
        smarteditContainer,
        entry({
            smarteditcepContainer: resolve('./jsTarget/web/features/smarteditcepContainer/index.ts')
        })
    )
};
