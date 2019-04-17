Utils 
============================

I added this project for common util classes.
-OQ

You will need to install to the local maven repo and add as a dependancy in maven projects.

## Add to POM ##

Ideally, it should be something like:

	<dependency>
		<groupId>com.camelinaction</groupId>
		<artifactId>utils-loggers</artifactId>
		<version>1.0.0</version>
		<scope>compile</scope>
	</dependency>

But I couldn't get maven to find the jar this way. So instead, I specified the path directly:

	<dependency>
		<groupId>com.camelinaction</groupId>
		<artifactId>utils-loggers</artifactId>
		<version>1.0.0</version>
		<!-- <scope>compile</scope> -->
		<scope>system</scope>
		<systemPath>/home/otto/.m2/repository/com/camelinaction/utils-loggers/1.0.0/utils-loggers-1.0.0.jar</systemPath>			
	</dependency>

