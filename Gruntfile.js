module.exports = function(grunt) {

    grunt.loadNpmTasks('grunt-contrib-jshint');

    grunt.initConfig({
        jshint: {
            options: {
                curly: true,
                eqeqeq: true
            },
            target1: ['Gruntfile.js', 'src/**/*.js']
        }
    });

    grunt.registerTask('default', ['jshint']);

};