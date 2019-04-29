# Movies Integration Project

## ToDos
- Enrich XML with fields from REST service [done]
- Add XSD and validate CMM message (move failures to dead letter) [done]
- add JSON validation and dead letter
- add message # and aggregator [ip]
- Clean up dependancies
- Exception Handling
- Use External ApacheMQ
- Resend on fail
- Unit tests
- Deploy to FUSE ESB
- update diagram

## Execution

	mvn camel:run
	

## Unit Tests


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
Pipes and Filters
Content Based Router
- Splitter: split csv file into 1 msg per row
Aggregator
Resequencer
Message Broker

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
