# Smart Campus Sensor \& Room Management API



A RESTful web service built with JAX-RS (Jersey 3.1.3) deployed on Apache Tomcat for managing campus rooms, sensors, and historical sensor readings. The API follows REST architectural principles with proper resource nesting, meaningful HTTP status codes, and a resilient error-handling strategy.



Module: 5COSC022W – Client-Server Architectures  

Base URI: http://localhost:8080/smart-campus-api/api/v1



### &#x20;1. API Overview



The API models three primary entities:



Room – a physical space on campus (id, name, capacity, sensorIds)

Sensor – a device deployed inside a room (id, type, status, currentValue, roomId)

SensorReading – a historical measurement recorded by a sensor (id, timestamp, value)



Data is stored exclusively in thread-safe in-memory data structures 

(ConcurrentHashMap). No database is used as required by the specification.



#### &#x20;Resource Map



|Method |Path|Description|
|-|-|-|
|GET|/api/v1|Discovery endpoint with API metadata|
|GET|/api/v1/rooms|List all rooms|
|POST|/api/v1/rooms|Create a new room|
|GET|/api/v1/rooms/{roomId}|Get a specific room|
|DELETE|/api/v1/rooms/{roomId}|Delete a room (409 if sensors exist)|
|GET|/api/v1/sensors|List all sensors (supports ?type= filter)|
|POST|/api/v1/sensors|Register a sensor (422 if roomId invalid)|
|GET|/api/v1/sensors/{sensorId}|Get a specific sensor|
|GET|/api/v1/sensors/{sensorId}/readings|Get reading history|
|POST|/api/v1/sensors/{sensorId}/readings| Add a reading|





#### &#x20;Error Handling



All error responses return a consistent JSON body produced by dedicated ExceptionMapper providers. A global ExceptionMapper catches all unexpected errors so stack traces never reach the client.



### &#x20;2. Project Structure



src/main/java/com/smartcampus/

├── AppConfig.java                     @ApplicationPath("/api/v1") + class registration

├── StartupListener.java               Displays startup message on deployment

├── model/

│   ├── Room.java

│   ├── Sensor.java

│   └── SensorReading.java

├── store/

│   └── DataStore.java                 Thread-safe ConcurrentHashMap store

├── resource/

│   ├── DiscoveryResource.java

│   ├── RoomResource.java

│   ├── SensorResource.java

│   └── SensorReadingResource.java

├── exception/

│   ├── RoomNotEmptyException.java

│   ├── LinkedResourceNotFoundException.java

│   ├── SensorUnavailableException.java

│   └── mappers/

│       ├── RoomNotEmptyMapper.java

│       ├── LinkedResourceNotFoundMapper.java

│       ├── SensorUnavailableMapper.java

│       └── GlobalExceptionMapper.java

└── filter/

└── LoggingFilter.java



src/main/webapp/

├── META-INF/

│   └── context.xml

└── WEB-INF/

└── web.xml



### 3\. Build and Run Instructions



#### &#x20;Prerequisites



\- Java 17 or newer

\- Apache Maven 3.6+

\- Apache Tomcat 10.1+

\- Port 8080 available



#### &#x20;Build



Right-click the project in NetBeans → "Clean and Build" Or from Command Prompt:



mvn clean install



#### Run

#### 

Right-click the project in NetBeans → "Run"



NetBeans will automatically deploy to Tomcat and start the server.



You should see in the NetBeans output panel:



&#x09;==================================

&#x20;       Smart Campus API running at

&#x20;       http://localhost:8080/smart-campus-api/api/v1

&#x20;       To stop: click the red button in NetBeans

&#x20;       ==================================



The API is now available at:



http://localhost:8080/smart-campus-api/api/v1



#### Stop the server



Click the "red square button" in the NetBeans output panel to stop Tomcat.



### 4\. Sample curl Commands



#### 4.1 Discovery endpoint



&#x20;curl -X GET http://localhost:8080/smart-campus-api/api/v1



#### 4.2 Create a Room 



&#x20;curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors 

&#x09;

&#x09;Content-Type: application/json

&#x09;{ "id":"TEMP-001",

&#x09;  "type":"Temperature",

&#x09;  "status":"ACTIVE",

&#x09;  "currentValue":0.0,

&#x20;         "roomId":"LIB-301" }



#### 4.3 Create a Sensor



&#x20;curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors 

&#x09;

&#x09;Content-Type: application/json

&#x09;{ "id":"TEMP-001",

&#x09;  "type":"Temperature",

&#x09;  "status":"ACTIVE",

&#x09;  "currentValue":0.0,

&#x09;  "roomId":"LIB-301" }



#### 4.4 Add a Sensor Reading



&#x20;curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/TEMP-001/readings 

&#x09;

&#x09;Content-Type: application/json

&#x09;{ "value":23.5 }



#### 4.5 Filter Sensors by Type 



&#x20;curl -X GET "http://localhost:8080/smart-campus-api/api/v1/sensors?type=Temperature"



### 5\. Conceptual Report



###### 5.1 \[Part 1.1] – In your report, explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton?



The default of the JAX-RS is to create a new resource class instance to all the incoming requests in the form of the HTTP. This is known as per-request scope. The instance is constructed as the request is received, and deconstructed as the response is sent. The runtime does not treat resource classes as singletons unless annotated with @Singleton.



This has a direct impact on how shared data should be dealt with. As every request has its own instance of a resource, any data stored in the resource as an instance variable is lost on completion of the request. The information that has to be transferred between requests like the list of rooms and sensors has to be stored externally of the resource class in a shared location.



The DataStore class in this project has all common data in the static fields which include the instances of ConcurrentHashMap of rooms, sensors and readings. ConcurrentHashMap is chosen since Tomcat requests are handled in a thread pool that is, multiple requests may share DataStore at the same time. A simple HashMap would not be concurrently safe and could cause data corruption, or inconsistent reads. ConcurrentHashMap offers thread safety with no wide synchronisation blocks required, good performance without sacrificing safety.



###### 5.2 \[Part 1.2] – Why is the provision of "Hypermedia" (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?



HATEOAS (Hypermedia As The Engine Of Application State) implies that API responses do not only contain raw data but also links to other related resources and actionable things. The API endpoint endpoint of this API at GET /api/v1 serves as a links map, indicating clients where to locate rooms and sensors, allowing clients not to need to guess or memorise URL formats.



There are several strengths of hypermedia over static documentation. With change of URL, statical documentation is no longer relevant and clients have to change their hard coded paths. Under hypermedia, the server has links, hence one is free to modify his URI structure without breaking the clients. It also eases API exploration - a developer can start at the root endpoint and navigate the entire API as you would navigate a web site, using links. This reduces the time to onboard and the API is easier to use by new developers.



###### 5.3 \[Part 2.1] – When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects?



When GET /api/v1/rooms gives the complete Room object for all entries, a single request provides the client with everything needed - name, capacity, and sensor list. This is convenient but does not scale well. With thousands of rooms each containing many sensors, the payload grows very large, consuming high network bandwidth and increasing parsing time on the client.



Returning only IDs makes responses small and quick, but requires the client to make additional requests to get information about every room, constituting what is referred to as the N+1 request problem - one request to get the list, then one per room to get details.



The sensible compromise is to return lightweight summaries in the collection (ID and name only) while leaving full details to individual resource endpoints such as GET /rooms/{roomId}. This project returns full objects since the added complexity is not justified at this scale, but this would be the natural first optimisation under real production load.



###### 5.4 \[Part 2.2] – Is the DELETE operation idempotent in your implementation?



Yes, the implementation of DELETE /api/v1/rooms/{roomId} is idempotent. Idempotency means that making the same request several times results in the same server state as making it once.



The initial DELETE request deletes the room and returns 204 No Content. When the same request is sent again, the room no longer exists and so 404 Not Found is returned. The response code differs, however the server state is the same - the room does not exist in either case. No additional damage is done by the second request.



This is significant because the HTTP definition of idempotency concerns server state, not response codes. Retries due to network problems or client bugs do not introduce unwanted side effects, which is the safety guarantee that matters in production systems.



###### 5.5 \[Part 3.1] – We explicitly use the @Consumes (MediaType.APPLICATION\_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?



The @Consumes(MediaType.APPLICATION\_JSON) annotation declares that the POST sensor endpoint only accepts requests with Content-Type: application/json. If a client sends text/plain or application/xml, JAX-RS rejects the request before the resource method is invoked and automatically returns a 415 Unsupported Media Type response.



This means content validation is done at the framework level rather than inside business logic. The resource method never receives malformed input, eliminating a whole category of possible errors. The 415 response also gives clients a clear, accurate message that their Content-Type header is wrong, which is far more useful than a vague parsing error buried inside a 400 Bad Request.



###### 5.6 \[Part 3.2] – You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/v1/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?



Path parameters identify a specific resource. Query parameters describe how a collection should be viewed. Filtering belongs to the second category, making @QueryParam the semantically correct choice.



A URL like /api/v1/sensors/type/CO2 incorrectly suggests that type/CO2 is a specific resource identifier, on the same level as a sensor ID. This is misleading. In contrast, /api/v1/sensors?type=CO2 clearly communicates filtering the sensors collection by type equals CO2.



Query parameters also compose naturally. Filtering by multiple criteria is simple: /sensors?type=CO2\&status=ACTIVE. Path parameters would require deeply nested segments that explode combinatorially. Additionally, HTTP caching infrastructure already treats query parameters as variations of the same resource collection, which is exactly the relationship the API intends to express.



###### 5.7 \[Part 4.1] – Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path in one massive controller class?



A sub-resource locator is a method annotated with @Path but without an HTTP verb. It does not return a Response but an instance of another resource class, and JAX-RS continues routing to that object. In this project, SensorResource delegates /{sensorId}/readings to a new SensorReadingResource, passing the sensor ID through the constructor.



The first benefit is separation of concerns. SensorResource handles sensor logic. Everything about reading history lives in SensorReadingResource. Each class has a single clear responsibility.



The second benefit is contextual state. The sensor ID is passed into SensorReadingResource at construction time, so every method inside already knows which sensor it is working with, without needing to repeat @PathParam on each method.



The third benefit is testability. SensorReadingResource can be unit tested independently by constructing it directly with a test sensor ID, without needing the full JAX-RS routing infrastructure.



In large APIs, this pattern is the difference between maintainable focused classes and one huge controller that becomes impossible to navigate over time.



###### 5.8 \[Part 5.2] – Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?



404 Not Found is used in the request URI - that is, the endpoint at the URL is not found. The endpoint of /api/v1/sensors is a valid endpoint that is present on the server when a client POSTs a sensor with a roomId that is not present. Sending 404 would be deceiving since it would seem the endpoint itself has been killed.



The 422 Unprocessable Entity is employed when the server knows the correct format of the request and the endpoint is not invalid, but cannot process the request because of a semantic matter in the payload. The JSON is valid, the mandatory fields exist, yet the mentioned roomId does not match with a known room. This is a payload referential integrity issue and that is exactly what 422 is intended to warn of.



As a developer, a response with 422 status obviously means that there is something wrong with the data and helps to debug the request body. A 404 would cause developers to go off on routing configuration, spending time on the entirely incorrect issue.



###### 5.9 \[Part 5.4] – From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?



Exposing raw Java stack traces to external clients is a serious security vulnerability. A stack trace reveals the internal package structure of the application, informing an attacker what frameworks and patterns are in use. It reveals third party library class names and versions, which can be cross-referenced with public CVE databases to discover known vulnerabilities in those exact versions.



Server file paths are also disclosed by stack traces, helping attackers probe for misconfigured directories or backup files. Most dangerously, they reveal application control flow - an attacker sending crafted inputs can observe which internal methods are reached and where validation logic sits, giving them a map to build targeted exploits.



This leak is closed by the GlobalExceptionMapper in this project. It catches all unexpected Throwables, logs the full stack trace server-side for legitimate debugging, and returns only a generic 500 Internal Server Error to the client. Defenders keep the information they need; attackers get nothing useful.



###### 5.10 \[Part 5.5] – Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?



Logging is a cross-cutting concern - it applies to all requests and responses regardless of the endpoint. Placing Logger.info() calls inside every resource method creates several problems. Every new endpoint is an opportunity to forget the logging call. Different developers format log entries differently, making logs inconsistent and unreliable during incidents. Changing the log format requires editing every resource class individually.



Implementing ContainerRequestFilter and ContainerResponseFilter in a single @Provider class solves all of this. The filter runs automatically for every request JAX-RS routes, so no endpoint can accidentally opt out. The log format is defined once and is uniform across the entire API. Adding a new endpoint adds no new logging code - it is covered automatically. The resource methods stay focused purely on business logic, making them shorter and easier to read and maintain.



