narcis-graphql
===========
[![Build Status](https://travis-ci.org/DANS-KNAW/narcis-graphql.png?branch=master)](https://travis-ci.org/DANS-KNAW/narcis-graphql)


SYNOPSIS
--------

    narcis-graphql run-service


DESCRIPTION
-----------

Narcis GraphQL


ARGUMENTS
---------

    Options:

       -h, --help      Show help message
       -v, --version   Show version of this program

    Subcommand: run-service - Starts NARCIS Graphql as a daemon that services HTTP requests
       -h, --help   Show help message
    ---

EXAMPLES
--------

    narcis-graphql run-service


INSTALLATION AND CONFIGURATION
------------------------------


1. Unzip the tarball to a directory of your choice, typically `/usr/local/`
2. A new directory called narcis-graphql-<version> will be created
3. Add the command script to your `PATH` environment variable by creating a symbolic link to it from a directory that is
   on the path, e.g. 
   
        ln -s /usr/local/narcis-graphql-<version>/bin/narcis-graphql /usr/bin



General configuration settings can be set in `cfg/application.properties` and logging can be configured
in `cfg/logback.xml`. The available settings are explained in comments in aforementioned files.


BUILDING FROM SOURCE
--------------------

Prerequisites:

* Java 8 or higher
* Maven 3.3.3 or higher

Steps:

        git clone https://github.com/DANS-KNAW/narcis-graphql.git
        cd narcis-graphql
        mvn install
