admtool
=======

This is the admin tool to perform administration taskds on the appform CRF tool (https://github.com/telekosmos/appform)

### Build
First, you need to generate a jar file from [appform-dbfixer project](https://github.com/telekosmos/appform-dbfixer), which contains classes used to manipulate subject, questinnaire and user data (CRUD operations). 
Then, you as the target is to build a WAR file, you can use your fave Java IDE supporting server side development (_JetBrains IntelliJ Idea_ recommended) and build the application or take advantage of the `build.xml` ant file provided to do the same with or without an IDE.

Note that _classpath_ may require your attention in order everything works.

The preferred java version to build the application is 1.6.0_65.

### Deploy
The generated WAR file is deployed in __Apache Tomcat/6.0.35__. In theory, as this app fulfills the WAR file specification, it could be dropped in any WAR container. Usually, WAR containers are propietary products, so changes in some configuration files may be required.