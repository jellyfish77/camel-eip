# Movies Integration Project

## Commands

### Build/Install

	mvn clean install -U
	(from parent)

Skip unit tests:

	mvn clean install -U -Dmaven.test.skip=true

### Execution

Run Camel Spring configurations in a forked JVM from Maven:

	mvn camel:run
	
	mvn camel:run  2>&1 | tee run.log
	
Run in same JVM as Maven (faster startup but potential classpath issues):
	
	mvn camel:embedded
	
Skip Unit Tests:

	mvn camel:run -Dmaven.test.skip=true 2>&1 | tee run.log

### Dependancies

### Web Service Injection

	curl  -v -H 'Content-Type: application/xml; charset="utf-8"' http://localhost:8888/cmminjection -d "<Movie><Title>Avengers: Age of Ultron</Title><ImdbId>tt2395427</ImdbId><Gross>458991599</Gross><Format><Type>movie</Type><Color>Color</Color><Language>English</Language><AspectRatio>2.35</AspectRatio><Runtime>141</Runtime></Format><Production><Countries><Country>USA</Country></Countries><Budget>250000000</Budget><Year>2015</Year><ReleaseDate>2015-05-01Z</ReleaseDate><DvdDate>2015-10-02Z</DvdDate><Website>http://marvel.com/avengers</Website></Production><Genres><Genre>Action</Genre><Genre>Adventure</Genre><Genre>Sci-Fi</Genre></Genres><Plot><Description>When Tony Stark and Bruce Banner try to jump-start a dormant peacekeeping program called Ultron, things go horribly wrong and it's up to Earth's mightiest heroes to stop the villainous Ultron from enacting his terrible plan.</Description><PlotKeywords><PlotKeyword>artificial intelligence</PlotKeyword><PlotKeyword>based on comic book</PlotKeyword><PlotKeyword>captain america</PlotKeyword><PlotKeyword>marvel cinematic universe</PlotKeyword><PlotKeyword>superhero</PlotKeyword></PlotKeywords></Plot><Poster>https://m.media-amazon.com/images/M/MV5BMTM4OGJmNWMtOTM4Ni00NTE3LTg3MDItZmQxYjc4N2JhNmUxXkEyXkFqcGdeQXVyNTgzMDMzMTg@._V1_SX300.jpg</Poster><Awards>7 wins &amp; 45 nominations.</Awards><UserVotes>462669</UserVotes><NumUsersReviewed>1117</NumUsersReviewed><Rating>PG-13</Rating><ImdbLink>http://www.imdb.com/title/tt2395427/?ref_=fn_tt_tt_1</ImdbLink><Country>USA</Country><FacebookLikes>118000</FacebookLikes><NumPosterFaces>4</NumPosterFaces><Director><Name>Joss Whedon</Name><FacebookLikes>0</FacebookLikes></Director><Writers><Writer>Joss Whedon, Stan Lee (based on the Marvel comics by), Jack Kirby (based on the Marvel comics by), Joe Simon (character created by: Captain America), Jack Kirby (character created by: Captain America), Jim Starlin (character created by: Thanos)</Writer></Writers><NumReviews>635</NumReviews><Cast><Actors><Actor><Biling>1</Biling><Name>Chris Hemsworth</Name><FacebookLikes>26000</FacebookLikes></Actor><Actor><Biling>2</Biling><Name>Robert Downey Jr.</Name><FacebookLikes>21000</FacebookLikes></Actor><Actor><Biling>3</Biling><Name>Scarlett Johansson</Name><FacebookLikes>19000</FacebookLikes></Actor><Actor><Name>Mark Ruffalo</Name></Actor><Actor><Name>Chris Evans</Name></Actor></Actors></Cast><Ratings><Rating><Source>Internet Movie Database</Source><Value>7.3/10</Value></Rating><Rating><Source>Rotten Tomatoes</Source><Value>75%</Value></Rating><Rating><Source>Metacritic</Source><Value>66/100</Value></Rating></Ratings></Movie>"

### Unit Tests

	mvn test -Dtest=IntegrationTests 2>&1 | tee IntegrationTests.log	
	mvn test -Dtest=SplitCsvRouteMockTests#testSplitCsvWithMock 2>&1 | tee testSplitCsvWithMock.log
	mvn test -Dtest=SplitCsvRouteMockTests#testSplitCsvWithMockAndSkip 2>&1 | tee testSplitCsvWithMockAndSkip.log

### ActiveMQ
	
	activemq stop && activemq start

### Hawt.io

Run Hawt.io console to interrogate ActiveMQ JMX exposed by Jolokia (JSON/HTTP):

	java -jar /opt/hawtio-app-2.6.0.jar

### Other

Copy files containing multiple patterns to path:

	find . -type f -name "*.java" | xargs grep -li 'CamelSpringTestSupport' | xargs grep -li 'mock' | xargs grep -li 'AbstractXmlApplicationContext' | xargs cp -t /home/otto/temp/camel_tests/spring/mock

Where -lir:

	-l, --files-with-matches  print only names of FILEs with selected lines
	-i, --ignore-case         ignore case distinctions
	-r, --recursive           like --directories=recurse

Get # files matching by piping result to word count program:
	
	 | wc -l


## Operation

![Integration Flow](pictures/movies.png?raw=true "Integration Flow")

### From CSV File
Read CSV input file
Store in input queue (with WireTap to message log)
Split to individual record
Enrich data with JSON webservice call based on Title and map to XML
Send to Enriched Queue (with WireTap to message log)

### From Enriched Queue
If english language send to english queue (with WireTap to message log)
Else send to non-english queue (with WireTap to message log)

### From English Queue
Map to XML CDM using XSLT
Validate against XSD
- Errors go to dead letter channel (with WireTap to message log)
Publish to XML CMM Topic  (with WireTap to message log)

### From Non-English Queue
Map XML to JSON CDM 
Validate against XSD
- Errors go to dead letter channel  (with WireTap to message log)
Publish to  JSON CDM  Topic  (with WireTap to message log)

### From SOAP Webservice
 (provides a way to inject XML CMM messages)
Validate against XSD
- Errors go to dead letter channel  (with WireTap to message log)
Publish to XML CMM Topic  (with WireTap to message log)

### From REST Webservice
 (provides a way to inject JSON CMM messages)
Validate against XSD???
- Errors go to dead letter channel  (with WireTap to message log)
Publish to JSON CMM Topic  (with WireTap to message log)

### From XML CMM Topic
Send to XML CMM Queue (with WireTap to message log)

### From JSON CMM Topic
Map to XML CMM and send to XML CMM Queue (with WireTap to message log)

### From XML CMM Queue
Send to Commit Queue (with WireTap to message log)
Send to CSV File Queue (with WireTap to message log)

### From Commit Queue
Build DM commit (single record) (Spring Bean) (with WireTap to message log)
Commit record 

### From CSV File Queue
Aggregate all records back together
Write to CSV Aggregated queue (with WireTap to message log)

### From CSV Aggregated Queue
Build CSV Record  from XML CMM
Write CSV File with current timestamp appended to FN (with WireTap to message log)

## Source Data

- CSV "IMDB 5000 Movie Datase" dataset sourced from:  from https://data.world

	"movie_title"   
	"color"   
	"num_critic_for_reviews"  
	"movie_facebook_likes"   
	"duration"   
	"director_name"   
	"director_facebook_likes"   
	"actor_3_name"   
	"actor_3_facebook_likes"   
	"actor_2_name"   
	"actor_2_facebook_likes"   
	"actor_1_name"   
	"actor_1_facebook_likes"   
	"gross"   
	"genres"   
	"num_voted_users"   
	"cast_total_facebook_likes"   
	"facenumber_in_poster"   
	"plot_keywords"   
	"movie_imdb_link"   
	"num_user_for_reviews"   
	"language"   
	"country"   
	"content_rating"   
	"budget"   
	"title_year"   
	"imdb_score"   
	"aspect_ratio"  

- IMDB REST Service: http://omdbapi.com/

		{
			"Title":"A Few Good Men",
			"Year":"1992",
			"Rated":"R",
			"Released":"11 Dec 1992",
			"Runtime":"138 min",
			"Genre":"Drama, Thriller",
			"Director":"Rob Reiner",
			"Writer":"Aaron Sorkin (play), Aaron Sorkin (screenplay)",
			"Actors":"Tom Cruise, Jack Nicholson, Demi Moore, Kevin Bacon",
			"Plot":"Military lawyer Lieutenant Daniel Kaffee defends Marines accused of murder. They contend they were acting under orders.",
			"Language":"English, French",
			"Country":"USA",
			"Awards":"Nominated for 4 Oscars. Another 10 wins & 26 nominations.",
			"Poster":"https://m.media-amazon.com/images/M/MV5BMmRlZDQ1MmUtMzE2Yi00YTkxLTk1MGMtYmIyYWQwODcxYzRlXkEyXkFqcGdeQXVyNTI4MjkwNjA@._V1_SX300.jpg",
			"Ratings": [
				{"Source":"Internet Movie Database","Value":"7.7/10"},
				{"Source":"Rotten Tomatoes","Value":"82%"},
				{"Source":"Metacritic","Value":"62/100"}
			],
			"Metascore":"62",
			"imdbRating":"7.7",
			"imdbVotes":"212,785",
			"imdbID":"tt0104257",
			"Type":"movie",
			"DVD":"07 Oct 1997",
			"BoxOffice":"N/A",
			"Production":"Columbia Pictures",
			"Website":"N/A",
			"Response":"True"
		}
		

## EIPs Demonstrated
### Messaging Endpoints
Message Endpoint
Polling Consumer
Durable Subscriber
Service Activator
Event Driven Consumer

### Messaging Channels
Message Channel
Point-to-point Channel
Channel Adapter
Dead Letter Channel
Guaranteed Delivery
Message Bus

### Message Construction
Message
Document Message
Event Message
Correlation Identifier
Message Sequence

### Message Routing
- Pipes and Filters
- Content Based Router
- Splitter: split csv file into 1 msg per row
- Aggregator
- Resequencer
- Message Broker
- Multicast

### Message Transformation
Message Translator
Content Enricher
Content Filter
Canonical Data Model

### System Management
WireTap
Detour
Message Store
Test Message
Channel Purger
Message History

## Setup

### Database Scripts
