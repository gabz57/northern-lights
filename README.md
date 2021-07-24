# Northern-lights

ℹ️ This is a work in progress, code coverage is not correct, but their content should be well constructed, and expose essential aspects.

##Modular monolith(s)

This project contains complete code samples showing Spring with reactive code.

- Code is mainly split into /apps and /libs
- Applications should only configure and glue other librairies contained in /libs 

## Northernlights-chat-api

Web application using webflux to expose a chat service:
- Some handlers via exposed via Functional routing
- One SSE endpoint exposing chat data to the user

### Storage

Currently no data is persisted, eveything is kept in memory (as a pet project, this will come later)

### Authentication principle:  
SSE endpoint cannot handle authentication directly and cannot received data excepted headers (javascript limitation in browser).  
Thus we first perform an authenticated POST prior to calling the SSE endpoint to generate a unique key, and use this key in header during SSE "connection" (the key is deleted after SSE client disconnect).

This allow the client to POST its current data status and then  retrieve only new data via SSE

### Build & Run

```
# To build the application
$ ./gradlew clean build
```
```
# To run the test
$ ./gradlew test
```
```
# To run the application
$ ./gradlew bootRun

# Then go to http://localhost:8080/ with your favorite browser to display the demo page
```