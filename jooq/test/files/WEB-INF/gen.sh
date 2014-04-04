#!/bin/sh

# Workaround for the issue with ANT and jOOQ (http://stackoverflow.com/questions/18185040/jooq-ant-codegeneration-not-working)
# 
# Florin.

libs=$( echo ../../../lib/*.jar . | sed 's/ /:/g')

OPT="-Xmx128m -Xss512k -XX:+UseCompressedOops"
java $OPT -cp "$libs" org.jooq.util.GenerationTool codegen.xml $1 $2 $3 $4 $5 $6
