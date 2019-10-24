# dita-asciidoc
DITA Open Toolkit plugin which can apply in the pre-process stage a custom ANT build file over the original topic contents.
As an example the plugin is configured to dynamically change ASCIIDOC files to DITA. 

Installation steps:

1. Install the **com.oxygenxml.ant.parser.dita** plugin in the DITA Open Toolkit: https://www.dita-ot.org/dev/topics/plugins-installing.html
1. The ASCIIDOC installation needs to be downloaded from: http://asciidoc.org/INSTALL.html
1. Install also a Python interpreter version 2.x on your local computer.
1. The build file **com.oxygenxml.ant.parser.dita/resources/build.xml** needs to be edited and the path "asciidoc.install.dir" needs to be set to the place 
1. A sample DITA Map can be found in the "asciiDocSamples" sample folder.

Copyright and License
---------------------
Copyright 2019 Syncro Soft SRL.

This project is licensed under [Apache License 2.0](https://github.com/oxygenxml/dita-asciidoc/blob/master/LICENSE)
