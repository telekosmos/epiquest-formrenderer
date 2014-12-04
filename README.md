epiquest-formrenderer
=====================

The form renderer engine for __EpiQuest__. This is basically a couple of [Groovy](http://groovy.codehaus.org) classes which uses specific Groovy features missing in Java (like the chance to work with DSLs) to build an at-least-easy-to-read form engine based on the data pulled from the database.

## Build
Both an __ant build__ file (`formrenderer.xml`) and configuration files to use with Jetbrains IntelliJ Idea (which feaures Groovy support) are provided to generate the Jar file

## Integration
The genereated Jar files has to be included in the `WEB-INF/lib` directory of the [EpiQuest CRF tool](https://github.com/telekosmos/appform) in order to be used as a library by the main application. This is already done and it should be done again every time this library is updated.
