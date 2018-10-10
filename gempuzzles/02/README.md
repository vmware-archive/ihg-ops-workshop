# Instructions

$GEMFIRE/bin/gfsh start locator --name=locator --dir=locator --port=10000

$GEMFIRE/bin/gfsh start server --name=datanode1 --dir=datanode1 --locators=localhost[10000] --cache-xml-file=datanode1/cache.xml --server-port=10100

$GEMFIRE/bin/gfsh start server --name=datanode2 --dir=datanode2 --locators=localhost[10000] --cache-xml-file=datanode2/cache.xml --server-port=10200

# The Problem

Differing definitions of the person region.  
