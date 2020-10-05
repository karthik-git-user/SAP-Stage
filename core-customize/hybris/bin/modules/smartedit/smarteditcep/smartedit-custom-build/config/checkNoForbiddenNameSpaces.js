module.exports = function() {
    return {
        config: function(data, conf) {

            var fatalMappings = conf.mappings.find(function(ele) {
                return ele.level === 'FATAL';
            });

            fatalMappings.patterns = fatalMappings.patterns || [];

            fatalMappings.patterns.push('!**/generated_*');

            return conf;
        }
    };
};
