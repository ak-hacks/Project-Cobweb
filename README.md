Project-Cobweb @ Pan-Pearson Hackathon, 2012
============================================

Introduction
------------

We are building an interlinked web of Companies and People. 
The hack will involve mashup of Companies and Directors data API with FT Content Search API. This will expose the data in a much more visual and discoverable way. Currently, this data is presented in plain old tabular format which doesn't facilitate data and relationship discovery. 

[Hack Slide Deck](https://docs.google.com/presentation/d/1yUuWhNhbkR_2KMiuMsuq6u1hbhw7ssh21e9mVGumNSU/present#slide=id.p "Hack Slide Deck")

Tech Stack
----------
* Neo4j Graph Database
* Java
* D3 Javascript Library
* HTML, JS, CSS
* Apache Tomcat 6 

Configuration
=============
* DB Path is configured in [appconfig.properties](src/main/resources/appconfig.properties)
* Logging levels can be configured in [log4j.properties](src/main/resources/log4j.properties)

Populating the DB
=================
Run the DBPopulator standalone service to populate the DB with some initial data into the prototype.
<pre>
mvn exec:java -Dexec.mainClass="com.ft.hack.cobweb.service.DBPopulator"
</pre>