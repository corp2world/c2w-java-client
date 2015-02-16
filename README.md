### Corp2World Java Client Library
#### Brief Developer Guide

##### 1. Introduction

www.Corp2World.com communication service provides simple and easy to use REST API to send notification messages over different communication channels like email, Google Talk, SMS, phone calls etc. 

This library provides more convinient higher level Java API to communicate to Corp2World service, as well as some additional add-ons for different 3rd party libraries and frameworks. Please read documentation about service at https://corp2world.com/Home/DocumentationEn .

Current version has the following modules:
* c2w-java-client-core : core library to send messages by calling Corp2World.com REST API
* c2w-java-client-log4j : Log4j Appender to send log messages to Corp2World.com service
* c2w-java-client-log4j2 : Log4j 2 Appender to send log messages to Corp2World.com service

These modules are available in the Maven central repository:

```
<dependency>
    <groupId>com.corp2world</groupId>
    <artifactId>c2w-java-client-core</artifactId>
    <version>1.0.0</version>
</dependency>

<dependency>
    <groupId>com.corp2world</groupId>
    <artifactId>c2w-java-client-log4j</artifactId>
    <version>1.0.0</version>
</dependency>

<dependency>
    <groupId>com.corp2world</groupId>
    <artifactId>c2w-java-client-log4j2</artifactId>
    <version>1.0.0</version>
</dependency>
```

For other build frameworks, supporting Maven artifact repositories, use the standard approach to add dependencies to your project.

You can also download binary distribution from <a href="https://corp2world.com/Downloads/DownloadsList">Corp2World Download page</a> and add JARs mannualy to your project classpath.


##### 2. Accessing Corp2World REST API

In order to access REST API provided by Corp2World , you need to register an account on www.corp2world.com, setup at least one communication channel and generate an API Token in profile settings. This API Token is used to authorize your requests to the service. Token consists of 'Token ID' and 'Token Key' which you can specify in one of the following ways:

 * pass as '-D' java options:
 ```
 java -Dcom.c2w.client.token=<your_token_id>  -Dcom.c2w.client.key=<your_token_secret_key> ....
```

 * set as system properties in Java (somewhere in your application initialization logic):
 ```
 System.setProperty("com.c2w.client.token", "<your_token_id>");
 System.setProperty("com.c2w.client.key", "<your_token_secret_key>");
 ```

##### 3. Initialize Communication Service

You can obtain service instance from factory and initialize it by calling 'start' method:
```
Service service = ServiceFactory.getService();
service.start();
```

##### 4. Sending Message

First, you need to create a Message with topic (subject) and main message text:  
```
Message message = new Message("Hello Corp2World!", "I'm your Java program and I'm saying you Hello!" ); 
```

Optionally, you can add additional properties (key-value pairs) which can provide additional useful information for the message recipient:
```
message.getProperties().setProperty("Planet", "Earth");
message.getProperties().setProperty("Color", "Green");
...
```

Optionally, you can add dialog, or user response, options to the message if you expect the user to respond:
```
message.addDialogOption("Hello");
message.addDialogOption("Wow");
message.addDialogOption("Crazy");
```

Finally, you can send message:
```
Result result = service.send(message);
```

You can check if your request was successful and print out ID assigned to your message:
```
System.out.println("Message sent with result: "+result.getStatus());
System.out.println("Response: "+result.getResponse());
System.out.println("Message ID: "+result.getProperty("messageId"));
```

If you sent a 'dialog' message with user response options you can check if any user (who received this message) has responded:
```
List<MessageResponse> responseList = service.waitForResponse(Long.parseLong(result.getProperty("messageId")), 600);
```
In the call above you have to pass two arguments:
 - long: message ID (can be taken from the result of 'send message' call)
 - timeout in seconds for how long to wait for the response.
Keep in mind, that this is a synchronous call and your thread will be blocked untill there is at least a single response from the user , or untill the specified waiting time expires.

##### 5. Closing Service

Before terminating the program you need to stop the service in order to release resources:
```
service.stop();
```

##### 6. Sending Message From Command Line

The library includes a demo class 'SendMessage' with 'main()' method, which can be used to send a message from command line. You need to have 3rd party dependency libraries in your class path (please see dependencies in '.\pom.xml' and 'c2w-java-client-core\pom.xml'). Below is an example of executing 'SendMessage' class from command line:
```
java com.c2w.client.core.SendMessage \
-Dcom.c2w.client.token=<your_token_id>  \
-Dcom.c2w.client.key=<your_token_key> \
"Hello Corp2World!"  "I'm your Java program and I'm saying you Hello!" \
"p_Planet=Earth" "p_Color=Green" \
"d_Hello" "d_Wow" "d_Crazy"
```

In the example above the first program argument is message subject and second argument is message text.
Additional message properties are passed as:
```
'p_<property name>=<property value>'
```
 And user response options are passed as:
 ```
 "d_<user response option>"
 ```
If user response options are specified,  the program will wait for user response for 10 minutes.
Please have a look into source code if any questions, it is quite simple.

##### 7. Advanced Service Configuration Parameters

Below is the full list of the supported configuration parameters:
- com.c2w.service.url : URL of the Corp2World message service (default is: https://www.corp2world.com:9443/rest)
- com.c2w.client.token : client token ID, used for the authentication (required) 
- com.c2w.client.key : client token key, used for the authentication (required)
- com.c2w.truststore.file : key-store file with trusted Corp2World server certificate. In most cases should not be required (optional)
- com.c2w.truststore.password : key-store password for trusted Corp2World server certificate. In most cases should not be required (optional)
- com.c2w.service.proxy.host : proxy host (optional, if proxy is used to access Internet)
- com.c2w.service.proxy.port : proxy port (optional, if proxy is used to access Internet)
- com.c2w.service.proxy.user : proxy user name (optional, if proxy is used to access Internet)
- com.c2w.service.proxy.password : proxy password (optional, if proxy is used to access Internet)

##### 8. Using Log4j Appender 

This appender is located in 'c2w-java-client-log4j' module and can be used to send logging message directly to Corp2World.com service. Below is shown a sample Appender configuration:

```
# Category with Corp2World appender
log4j.category.com.test=info, C2W
...
# Corp2World Appender
log4j.appender.C2W=com.c2w.client.log4j.Corp2WorldAppender
log4j.appender.C2W.ApiToken=API|1122...
log4j.appender.C2W.ApiKey=AABB11...
log4j.appender.C2W.BufferSize=100
log4j.appender.C2W.TopicPattern=Log %-5p Message
log4j.appender.C2W.Layout=org.apache.log4j.PatternLayout
log4j.appender.C2W.Layout.ConversionPattern=%d %p [%t] %c{10} (%M:%L) - %m%n
```

* ApiToken - your API access token (from your Corp2World profile settings)
* ApiKey - your API access key (from your Corp2World profile settings)
* BufferSize - this appender sends messages asynchronously and uses internal buffer. If buffer is full the logging event will be ignored
* TopicPattern - used to format the message topic
* Layout - used to format the message text

##### 9. Using Log4j2 Appender

This appender is located in 'c2w-java-client-log4j2' module and can be used to send logging message directly to Corp2World.com service. Below is shown a sample Appender configuration:

```
<Configuration status="warn" packages="com.c2w.client.log4j2">
...
<Appenders>
 ...
 <Corp2World name="C2W"
     apiToken="API|1122..."
     apiKey="AABB11...."
     topicPattern="Log  %-5p Message"
     bufferSize="100" >
     <PatternLayout pattern="%d %-5p [%t] %C{2} (%F:%L) - %m%n"/>
    </Corp2World>
    ...
</Appenders>

 <Loggers>
    ...
    <Logger name="com.test" level="warn" additivity="false">
    	<AppenderRef ref="C2W"/>
    </Logger>
  </Loggers>

```

* ApiToken - your API access token (from your Corp2World profile settings)
* ApiKey - your API access key (from your Corp2World profile settings)
* BufferSize - this appender sends messages asynchronously and uses internal buffer. If buffer is full the logging event will be ignored
* TopicPattern - used to format the message topic
* Layout - used to format the message text
