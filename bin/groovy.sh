#!/bin/bash

cd `dirname $0`/..

if [ ! -f bin/classpath ];
then
    echo "Configuring classpath to bin/classpath"
    gradle -q classpath >bin/classpath
fi

groovysh -cp src/main:resources/main:`cat bin/classpath`

