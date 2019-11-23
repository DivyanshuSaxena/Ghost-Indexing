rm -rf ~/Installers/janusgraph
cd ~/Installers/
unzip janusgraph-0.4.0-hadoop2.zip
mv janusgraph-0.4.0-hadoop2 janusgraph
cp -r janusgraph_0.2.1/ext/commons-csv janusgraph/ext/
rm janusgraph/lib/org.apache.servicemix.bundles.commons-csv-1.0-r706900_3.jar

