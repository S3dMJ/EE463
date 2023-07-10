# Project README

This README file provides an overview of the project, highlighting the difficulties faced, the requirements designed and realized, the requirements that were not realized, the project's build command, any special requirements, and comments about the observed performance differences between the two servers.

EE463 - BG4
Saad A. Al-Jehani [1935151]
Khaled M. Al-Dahasy [1935129]
Nawaf S. Al-Harbi [1936576]

## Difficulties Faced

During the project, we encountered several difficulties that negatively affected the outcome. Firstly, we faced issues with not planning for cases where we needed to rewrite an entire method or class. This lack of foresight caused setbacks in the project schedule.

Secondly, we did not foresee that performance would greatly differ between team members' computers. This discrepancy in performance affected the overall testing and debugging process, making it challenging to identify and resolve performance-related issues.

Lastly, we underestimated the complexity of debugging multithreaded programs. Multithreading introduced new types of bugs and made the debugging process more difficult and time-consuming.

## Requirements Designed and Realized

The following four key pieces of functionality were designed and successfully implemented in the given simple web server:

1. Conversion to a more responsive web server that uses a thread-pool architecture with one main thread, one monitoring thread, and a fixed number of worker threads.

2. Implementation of a bounded FIFO list of shared buffers to schedule requests to be served by worker threads. No ready-made Java ThreadPool utilities were used.

3. Implementation of two different strategies to handle overload situations as the buffers become full.

4. Addition of functionalities to make the new server more flexible, robust, and easy to use:
   - Server Input: The server accepts and validates a positional list of up to four optional command-line parameters specifying the port number, pool size, buffer size, and overload handling policy name. Default values are used if some parameters are not supplied or no command-line parameters are given at all.
   - Server Output: The server writes all its activity output to a log file called "webserver-log" instead of displaying it on the screen.
   - Server Health: The server monitors the life of the threads in the thread pool and replaces any dead threads by creating new ones to maintain the specified pool size.
   - Server Termination: The server can respond to a Ctrl-c signal typed at any time, causing the termination of all threads in the thread pool, the termination of the monitor thread, destruction of all semaphores, cleanup of memory, and graceful termination.

In addition, a benchmarking tool was used to measure the performance of the new server against the simple server.

## Requirements Designed but Not Realized

One requirement that we designed but failed to realize was related to handling concurrent requests. 
In cases where the number of concurrent requests highly exceeded the number of threads and the queue size, 
a bug was encountered. This bug resulted in the server refusing requests too fast, 
either due to the computer being fast or a lack of processor cores.

## Build Command and Special Requirements

To build the project, use the following command:

```
java -classpath .;webserve.jar WebServer [portnum] [threads] [buffers] [ovrld]
```

Please note that in some cases, it may be necessary to execute the following command before building:

```
javac -g -classpath .;webserve.jar; -d . *.java
```

## Observed Performance Differences

The observed performance differences between the simple server and the new server are as follows:

Error Handling: The simple server rarely encounters errors due to its simplicity, allowing it to perform tasks without running into issues most of the time.
However, the new server, especially under high concurrency, may encounter errors.
This is primarily because the new server is designed to be faster and capable of processing multiple requests simultaneously, 
which can lead to potential errors when the system is overloaded.

Response Time: The new server demonstrates improved performance in terms of responsiveness compared to the simple server.
The tests indicate that the new server, using overload handling policies such as DRPT and DRPH, achieves lower average reply times.
For instance, at the maximum error-free request rate of 20 concurrent requests, the reply time for DRPT is approximately 13.007 milliseconds, and for DRPH, it is around 13.856 milliseconds. In contrast, the simple server has a longer reply time of 21.248 milliseconds at the same maximum rate of 20 requests.

Refusals and Completed Requests: When examining the number of refusals and completed requests,
it is evident that the new server with DRPT and DRPH policies experiences an increase in refusals during high load situations.
a noticeable increase is observed in refusals for DRPH and DRPT, while the simple server and BLCK policy have zero refusals. 
This increase in refusals is attributed to worker threads getting "held" in the serve method of the serveWebRequest class when refusals occur concurrently, 
preventing workers from completing their tasks. On the other hand, completed requests for DRPH and DRPT are relatively low initially but increase gradually as workers conclude their serving after some refusals.