logstash-gelf
=========================

[![Build Status](https://api.travis-ci.org/mp911de/logstash-gelf.svg)](https://travis-ci.org/mp911de/logstash-gelf) [![Coverage Status](https://img.shields.io/coveralls/mp911de/logstash-gelf.svg)](https://coveralls.io/r/mp911de/logstash-gelf) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/biz.paluch.logging/logstash-gelf/badge.svg)](https://maven-badges.herokuapp.com/maven-central/biz.paluch.logging/logstash-gelf)


Provides logging to logstash using the Graylog Extended Logging Format (GELF) for using with:

* [Java Util Logging](#jul)
* [log4j 1.2.x](#log4j)
* [log4j 2.x](#log4j2)
* [JBoss AS7/Wildfly (JBoss AS8) (mix of Java Util Logging with log4j MDC)](#jbossas7)
* [Logback](#logback)

See also http://logging.paluch.biz/ for further documentation.


Including it in your project
--------------

Maven:

    <dependency>
        <groupId>biz.paluch.logging</groupId>
        <artifactId>logstash-gelf</artifactId>
        <version>1.5.3</version>
    </dependency>

JBoss Module Download:

    <dependency>
        <groupId>biz.paluch.logging</groupId>
        <artifactId>logstash-gelf</artifactId>
        <version>1.5.3</version>
        <classifier>logging-module</classifier>
    </dependency>

or http://search.maven.org/remotecontent?filepath=biz/paluch/logging/logstash-gelf/1.5.3/logstash-gelf-1.5.3-logging-module.zip


<a name="jul"/>Java Util Logging GELF configuration
--------------
Properties:

```properties
handlers = biz.paluch.logging.gelf.jul.GelfLogHandler, java.util.logging.ConsoleHandler

.handlers = biz.paluch.logging.gelf.jul.GelfLogHandler, java.util.logging.ConsoleHandler
.level = INFO

biz.paluch.logging.gelf.jul.GelfLogHandler.host=udp:localhost
biz.paluch.logging.gelf.jul.GelfLogHandler.port=12201
biz.paluch.logging.gelf.jul.GelfLogHandler.facility=java-test
biz.paluch.logging.gelf.jul.GelfLogHandler.extractStackTrace=true
biz.paluch.logging.gelf.jul.GelfLogHandler.filterStackTrace=true
biz.paluch.logging.gelf.jul.GelfLogHandler.timestampPattern=yyyy-MM-dd HH:mm:ss,SSSS
biz.paluch.logging.gelf.jul.GelfLogHandler.maximumMessageSize=8192

# This are static fields
biz.paluch.logging.gelf.jul.GelfLogHandler.additionalFields=fieldName1=fieldValue1,fieldName2=fieldValue2
biz.paluch.logging.gelf.jul.GelfLogHandler.level=INFO
```


<a name="log4j"/>
log4j GELF configuration
--------------
Properties:

```properties
log4j.appender.gelf=biz.paluch.logging.gelf.log4j.GelfLogAppender
log4j.appender.gelf.Threshold=INFO
log4j.appender.gelf.Host=udp:localhost
log4j.appender.gelf.Port=12201
log4j.appender.gelf.Facility=java-test
log4j.appender.gelf.ExtractStackTrace=true
log4j.appender.gelf.FilterStackTrace=true
log4j.appender.gelf.MdcProfiling=true
log4j.appender.gelf.TimestampPattern=yyyy-MM-dd HH:mm:ss,SSSS
log4j.appender.gelf.MaximumMessageSize=8192

# This are static fields
log4j.appender.gelf.AdditionalFields=fieldName1=fieldValue1,fieldName2=fieldValue2

# This are fields using MDC
log4j.appender.gelf.MdcFields=mdcField1,mdcField2
log4j.appender.gelf.DynamicMdcFields=mdc.*,(mdc|MDC)fields
log4j.appender.gelf.IncludeFullMdc=true
```


XML:
```xml
<appender name="gelf" class="biz.paluch.logging.gelf.log4j.GelfLogAppender">
    <param name="Threshold" value="INFO" />
    <param name="Host" value="udp:localhost" />
    <param name="Port" value="12201" />
    <param name="Facility" value="java-test" />
    <param name="ExtractStackTrace" value="true" />
    <param name="FilterStackTrace" value="true" />
    <param name="MdcProfiling" value="true" />
    <param name="TimestampPattern" value="yyyy-MM-dd HH:mm:ss,SSSS" />
    <param name="MaximumMessageSize" value="8192" />
    
    <!-- This are static fields -->
    <param name="AdditionalFields" value="fieldName1=fieldValue1,fieldName2=fieldValue2" />
    
    <!-- This are fields using MDC -->
    <param name="MdcFields" value="mdcField1,mdcField2" />
    <param name="DynamicMdcFields" value="mdc.*,(mdc|MDC)fields" />
    <param name="IncludeFullMdc" value="true" />
</appender>
```
    
<a name="log4j2"/>
log4j2 GELF configuration
--------------

### Fields

Log4j v2 supports an extensive and flexible configuration in contrast to other log frameworks (JUL, log4j v1). This allows you to specify your needed fields you want to use in the GELF message. An empty field configuration results in a message containing only

 * timestamp
 * level (syslog level)
 * host
 * facility
 * message
 * short_message

You can add different fields:

 * Static Literals
 * MDC Fields
 * Log-Event fields (using Pattern Layout)

In order to do so, use nested Field elements below the Appender element.

### Static Literals

```xml
<Field name="fieldName1" literal="your literal value" />
```
    
### MDC Fields

```xml
<Field name="fieldName1" mdc="name of the MDC entry" />
```

### Dynamic MDC Fields

```xml
<DynamicMdcFields regex="mdc.*" />
```

In contrast to the configuration of other log frameworks log4j2 config uses one `DynamicMdcFields` element per regex (not separated by comma).

### Log-Event fields

See also: [Pattern Layout](http://logging.apache.org/log4j/2.x/manual/layouts.html#PatternLayout)

Set the desired pattern and the field will be sent using the specified pattern value. 

Additionally, you can add the host-Field, which can supply you either the FQDN hostname, the simple hostname or the local address.

Option | Description
--- | ---
host{["fqdn"<br/>"simple"<br/>"address"]} | Outputs either the FQDN hostname, the simple hostname or the local address. You can follow the throwable conversion word with an option in the form %host{option}. <br/> %host{fqdn} default setting, outputs the FQDN hostname, e.g. www.you.host.name.com. <br/>%host{simple} outputs simple hostname, e.g. www. <br/>%host{address} outputs the local IP address of the found hostname, e.g. 1.2.3.4 or affe:affe:affe::1. 

XML:
```xml    
<Configuration>
    <Appenders>
        <Gelf name="gelf" graylogHost="udp:localhost" graylogPort="12201" extractStackTrace="true"
              filterStackTrace="true" mdcProfiling="true" includeFullMdc="true" maximumMessageSize="8192"
              originHost="%host{fqdn}">
            <Field name="timestamp" pattern="%d{dd MMM yyyy HH:mm:ss,SSS}" />
            <Field name="level" pattern="%level" />
            <Field name="simpleClassName" pattern="%C{1}" />
            <Field name="className" pattern="%C" />
            <Field name="server" pattern="%host" />
            <Field name="server.fqdn" pattern="%host{fqdn}" />
            
            <!-- This is a static field -->
            <Field name="fieldName2" literal="fieldValue2" />
             
            <!-- This is a field using MDC -->
            <Field name="mdcField2" mdc="mdcField2" /> 
            <DynamicMdcFields regex="mdc.*" />
            <DynamicMdcFields regex="(mdc|MDC)fields" />
        </Gelf>
    </Appenders>
    <Loggers>
        <Root level="INFO">
            <AppenderRef ref="gelf" />
        </Root>
    </Loggers>
</Configuration>    
```    

<a name="jbossas7"/>
JBoss AS7 GELF/Wildfly GELF (JBoss AS8) configuration
--------------
You need to include the library as module (see download above), then add following lines to your configuration:

standalone.xml
```xml
<custom-handler name="GelfLogger" class="biz.paluch.logging.gelf.jboss7.JBoss7GelfLogHandler" module="biz.paluch.logging">
    <level name="INFO" />
    <properties>
        <property name="host" value="udp:localhost" />
        <property name="port" value="12201" />
        <property name="facility" value="java-test" />
        <property name="extractStackTrace" value="true" />
        <property name="filterStackTrace" value="true" />
        <property name="mdcProfiling" value="true" />
        <property name="timestampPattern" value="yyyy-MM-dd HH:mm:ss,SSSS" />
        <property name="maximumMessageSize" value="8192" />
        
        <!-- This are static fields -->
        <property name="additionalFields" value="fieldName1=fieldValue1,fieldName2=fieldValue2" />
        
        <!-- This are fields using MDC -->
        <property name="mdcFields" value="mdcField1,mdcField2" />
        <property name="dynamicMdcFields" value="mdc.*,(mdc|MDC)fields" />
        <property name="includeFullMdc" value="true" />
    </properties>
</custom-handler>
```

<a name="logback"/>
Logback GELF configuration
--------------
logback.xml Example:

```xml
<!DOCTYPE configuration>

<configuration>
    <contextName>test</contextName>
    <jmxConfigurator/>

    <appender name="gelf" class="biz.paluch.logging.gelf.logback.GelfLogbackAppender">
        <host>udp:localhost</host>
        <port>12201</port>
        <facility>java-test</facility>
        <extractStackTrace>true</extractStackTrace>
        <filterStackTrace>true</filterStackTrace>
        <mdcProfiling>true</mdcProfiling>
        <timestampPattern>yyyy-MM-dd HH:mm:ss,SSSS</timestampPattern>
        <maximumMessageSize>8192</maximumMessageSize>
        
        <!-- This are static fields -->
        <additionalFields>fieldName1=fieldValue1,fieldName2=fieldValue2</additionalFields>
        
        <!-- This are fields using MDC -->
        <mdcFields>mdcField1,mdcField2</mdcFields>
        <dynamicMdcFields>mdc.*,(mdc|MDC)fields</dynamicMdcFields>
        <includeFullMdc>true</includeFullMdc>
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>INFO</level>
        </filter>
    </appender>

    <root level="DEBUG">
        <appender-ref ref="gelf" />
    </root>
</configuration>
```

Versions/Dependencies
--------------
This project is built against following dependencies/versions:

* json-simple 1.1.1
* log4j 1.2.14
* log4j2 2.0
* Java Util Logging JDK Version 1.6
* logback 1.0.13
* slf4j-api 1.7.5
* jedis 2.5.1 (includes commons-pool2 2.2)

Settings
--------------
Following settings can be used:

### Basic Properties

* `host` (since version 1.2.0, Mandatory): Hostname/IP-Address of the Logstash or Redis Host
    * tcp:(the host) for TCP, e.g. tcp:127.0.0.1 or tcp:some.host.com
    * udp:(the host) for UDP, e.g. udp:127.0.0.1 or udp:some.host.com
    * [redis](#redis)://\[:REDISDB_PASSWORD@\]REDISDB_HOST:REDISDB_PORT/REDISDB_NUMBER#REDISDB_LISTNAME , e.g. redis://:donttrustme@127.0.0.1:6379/0#myloglist or if no password needed redis://127.0.0.1:6379/0#myloglist
    * (the host) for UDP, e.g. 127.0.0.1 or some.host.com
* `port` (since version 1.2.0, Optional): Port, default 12201
* `graylogHost` (until version 1.1.0, Mandatory): Hostname/IP-Address of the Logstash Host
* `graylogPort` (until version 1.1.0, Optional): Port, default 12201
* `originHost` (Optional): Originating Hostname, default FQDN Hostname
* `extractStackTrace` (Optional): Post Stack-Trace to StackTrace field, default false
* `filterStackTrace` (Optional): Perform Stack-Trace filtering (true/false), default false
* `facility` (Optional): Name of the Facility, default logstash-gelf
* `threshold`/`level` (Optional): Log-Level, default INFO

### Advanced Properties

* `filter` (Optional): Class-Name of a Log-Filter, default none
* `mdcProfiling` (Optional): Perform Profiling (Call-Duration) based on MDC Data. See MDC Profiling, default false
* `additionalFields` (Optional): Post additional fields. Example: .GelfLogHandler.additionalFields=fieldName=Value
* `mdcFields` (Optional): Post additional fields, pull Values from MDC. Name of the Fields are comma-separated mdcFields=Application,Version,SomeOtherFieldName
* `dynamicMdcFields` (Optional): Dynamic MDC Fields allows you to extract MDC values based on one or more regular expressions. Multiple regex are comma-separated. The name of the MDC entry is used as GELF field name.
* `includeFullMdc` (Optional): Include all fields from the MDC, default false

MDC Profiling
--------------
MDC Profiling allows to calculate the runtime from request start up to the time until the log message was generated. You must set one value in the MDC:

`profiling.requestStart.millis` Time Millis of the Request-Start (Long or String)

Two values are set by the Log Appender:

 * `profiling.requestEnd` End-Time of the Request-End in Date.toString-representation
 * `profiling.requestDuration` Duration of the request (e.g. 205ms, 16sec)

<a name="redis"/>Notes on redis Connection
--------------
 * IMPORTANT: for getting your logstash config right it is vital to know that we do LPUSH (list push and not channel method)
 * The redis connection is done through jedis (https://github.com/xetorthio/jedis)
 * The Url used as connection property is a java.net.URI , therefore it can have all nine components. we use only the following:
   * scheme    (fixed: redis, directly used to determine the to be used sender class)
   * user-info (variable: only the password part is used since redis doesnt have users, indirectly used from jedis)
   * host      (variable: the host your redis db runs on, indirectly used from jedis)
   * port      (variable: the port your redis db runs on, indirectly used from jedis)
   * path      (variable: only numbers - your redis db number, indirectly used from jedis)
   * fragment  (variable: the listname we push the log messages via LPUSH, directly used)

License
-------
* [The MIT License (MIT)] (http://opensource.org/licenses/MIT)
* Contains also code from https://github.com/t0xa/gelfj

Contributing
-------
Github is for social coding: if you want to write code, I encourage contributions through pull requests from forks of this repository. 
Create Github tickets for bugs and new features and comment on the ones that you are interested in.


