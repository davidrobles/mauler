Mauler
======

A JavaScript library for turn-based games.

Building Mauler
-------------------------------------------------------------------------------
To build your own version of Mauler you will need to install:

- the [Node.js](http://nodejs.org/) JavaScript runtime and [npm](https://npmjs.org/) package manager
- the [Grunt](http://gruntjs.com/) task manager

Once the Node.js package manager has been installed (using the installer from their website),
we need to install Grunt and the Grunt CLI (Command Line Interface).

Open a [Terminal](http://www.apple.com/osx/apps/all.html#terminal) or
a [Commmand Prompt](http://en.wikipedia.org/wiki/Command_Prompt) and
type:

    $ npm install -g grunt-cli

then install the Mauler required dependencies:

    $ cd mauler
    $ npm install

Once all the above done, we are ready to build Mauler:

    $ cd mauler (if not already in the mauler directory)
    $ grunt

Both plain and minified library will be available under the "build" directory.