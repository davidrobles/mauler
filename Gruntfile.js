module.exports = function(grunt) {

    "use strict";

    grunt.initConfig({

        jasmine: {
            src: "src/**/*.js",
            specs: "test/**/*spec.js",
            helpers: "test/helpers/*.js"
        }

    });

    grunt.loadNpmTasks("grunt-contrib-jasmine");
    grunt.registerTask("default", "jasmine");

}