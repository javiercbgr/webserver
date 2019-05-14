# Web server with thread pooling
A multi-threaded (e.g. file-based) web server with thread-pooling
implemented in Java.

Forked from warchildmd's thread pooling at GitHub:
https://github.com/warchildmd/webserver <br/>
Keep-alive implementation based on jrudolph's at GitHub:
https://github.com/jrudolph/Pooling-web-server

New features:
- Added keep-alive implementation based on HTTP/1.1 and HTTP/1.0 headers received.
- Replaced the creation of a Thread when passing the request to the handler with just the Handler, as it is a runnable which is accepted by ThreadPoolExecutor.execute. The overhead removed helped increase the speed of transactions a 400%.
- Replaced gradle dependency management system with Maven.
- Added a batch script to compile and execute the server without any dependency management system or IDE.
- Added a batch script to load test the server with 20000 requests from 20 processes.
- Added handling speed monitoring capability implemented using a circular array.

Ideas I considered good and preserved are:
- WebApp abstraction, allowing quick implementations of other applications apart from file serving.
- Package modular design, nice folder hierarchy.
- HTTP classes.
- Monitoring, which was only modified to have the performance metrics.

Any class that implements `WebApp` interface can be plugged as the handler application. In this example
I'm using `FileServingApp` (serves static files from a document root) as my handler application. The single method
that must be implemented is `HttpResponse handle(HttpRequest request)`.

### Request flow
1. Server receives a request.
2. Server creates a Handler (Runnable) for this request.
2. Handler parses the request into a HttpRequest.
3. Handler passes the HttpRequest to the handler application.
4. Handler writes the response received from the handler application to the output stream.
5. Handler closes the streams.

A compiled version `webserver-1.0-SNAPSHOT.jar` can be found in the root directory.
 
`WebServer` - listens for connections and delegates them to worker threads.

`Handler` - handles a single request, by parsing it and sending it to the handler application.

`ExecutorMonitor` - prints server status once every 10 seconds.

`FileServingApp` - an application that serves static files from it's document root.

## Installation
`mvn package`

## Usage
`mvn verify` # Also performs install. <br />
or <br />
`create_run_server_jar.bat` # Also performs install. <br />
or <br />
`java -jar webserver-<version> <port> <threads> <document root>`
