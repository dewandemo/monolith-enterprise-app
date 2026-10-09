# Snowman — Employee Management System (EMS)

[![Build Status](https://travis-ci.org/colinbut/enterprise-application.svg?branch=master)](https://travis-ci.org/colinbut/enterprise-application)

Internal backend service for employee, project, and client records. Snowman is the
system of record consumed by Payroll and Invoicing via scheduled JMS messages, and
exposes its own REST API for internal tooling and reporting.

## Table of Contents

- [Preamble](#preamble)
- [Pre-requisites](#prerequisites)
- [Software Architecture](#architecture)
- [Database Design](#db-design)
- [Data Access](#data-access)
    - [Java - JDBC](#jdbc)
    - [Spring - JdbcTemplate](#jdbctemplate)
    - [Object Relational Mapping](#orm)
- [Messaging](#messaging)
- [Caching](#caching)
- [Uber Jar](#uber-jar)
- [Embedded Jetty](#embedded-jetty)
- [Database Migration](#database-migration)
- [Scalability](#scalability)
- [Clustering](#clustering)
- [Resiliency](#resiliency)

### <a name="preamble"></a>Preamble

Snowman tracks Employees, Projects, Clients, and the assignments between them. It is the
upstream source of truth that Payroll and Invoicing depend on, and is also wired up to a
notification topic that other internal systems subscribe to for change events.
  
![Image of a Snowman](etc/snowman.jpeg)  
  
Snowman exposes its functionality via REST (-like) endpoints as a backend web service.
There is no UI; downstream consumers are other internal services and batch tooling.

### <a name="prerequisites"></a>Pre - Requisites

- Java JDK 7
- Maven
- MySQL
 
 
1. Start up MySQL Server 
2. run `run.sh` script

### <a name="architecture"></a>Software Architecture

Rather than a traditional 3-layer architecture, the service is built using a Hexagonal
Architecture (Ports and Adapters). The core domain containing the main business logic
forms the inner layer, and the application infrastructure (Database, Message Queues,
REST endpoints) forms the outer layer.

![Image of a Hexagonal Architecture](etc/HexagonalArchitecture.png)  

This is how the system components fit together:

![Image of System Components](etc/SystemComponents.png)

### <a name="db-design"></a>Database Design

![Image of ER diagram](etc/entity-relationship.png)

Database table structure:

![Image of Table Diagram](etc/relation-table-schema.png)

### <a name="data-access"></a>Data Access

Rather than having a logical Data Access Layer within a 3 layered architecture, in a hexagonal architecture data access are
in an outer layer. 

#### <a name="jdbc"></a>Java - JDBC

One way of accessing the db from the application is using raw JDBC. This is quite low level. See ApplicationInfoDaoImpl.java

#### <a name="spring-jdbctemplate"></a>Spring - JDBC

The application framework in Spring provides a level of abstraction of data access by giving the usage of JdbcTemplate. This
class from Spring implements the GoF Template design pattern. By using this class, it relieves away a lot of the 'pain' of writing 
so called 'boilerplate' code in setting up db connections, exception handling etc. See UserDaoImpl.java

#### <a name="orm"></a>Object-Relational-Mapping(ORM)

Object Relational Mapping (ORM) is the mapping of relational database tables to objects and vice-versa. 
We use JPA standard along the the Hibernate implementation to achieve this. Model objects (Entities) are part of thus 'anemic'
data model. The DAOs encapsulates Hibernate EntityManager to do the basic CRUD operations.

### <a name="messaging"></a>Messaging

Communication to 3rd party external systems is primarily achieved via messaging using JMS & ActiveMQ as the 
implementing underlying Messaging System (Message Brokers, Message Queues, Topics).
 
Apache ActiveMQ is JMS compliant belonging to open source apache foundation. One of the commonly used MQ out there. 

### <a name="caching"></a>Caching

Client lookups are cached via Ehcache to reduce load on the database for frequently
accessed, slow-changing reference data.

### <a name="uber-jar"></a>Uber Jar

As part of the deployment process, the application is packaged up as an "uber" runnable
executable jar. This jar contains all dependencies and can be run from the command line with:

```java
java -jar target/Snowman.jar
```

This whole process is achieved by the [Maven Shade Plugin](https://maven.apache.org/plugins/maven-shade-plugin/)

### <a name="embedded-jetty"></a>Embedded Jetty

Rather than deploying to a full JavaEE application server (JBoss/Wildfly, WebSphere, WebLogic, Geronimo etc)
or a separate Web/JSP container (Tomcat, Jetty, UnderTow), the application embeds its own HTTP listener
("Embedded Jetty").

All dependencies are bundled into the Uber jar, so the application does not depend on
JavaEE libraries being provided by the hosting platform.

### <a name="database-migration"></a>Database Migration

Database updates are implemented using patches via changesets with a database migration tool in [liquibase](http://www.liquibase.org/)
to execute them in order to 'patch' up the database.

To run, execute the maven liquibase plugin by...

Running update patches:

```xml
mvn liquibase:update
```

And to rollback those particular patches:

```xml
mvn liquibase:rollback -Dliquibase.rollbackCount=1
```

### <a name="scalability"></a>Scalability

1. Horizonantal Scalability
2. Vertical Scalability

#### Horizontal Scalability a.k.a "Scaling Out"

To support horizontal scalability you run multiple instances of the same application.

You can do this manually like:

```java
java -jar -Dport=[port number] target/Snowman.jar
```

where port number is an unused port

#### Veritcal Scalability a.k.a "Scaling Up"

This option is limited kind of a way. You can opt to run this application in a higher spec 
machine. Alternatively, you can give more memory to the JVM by tuning the min and max 
parameters like the following:

```java
java -jar -Xms256m -Xmx2048m target/Snowman.jar
```

### <a name="clustering"></a>Clustering

No clustering support. This application does not run in a managed cluster today; horizontal
scalability (running multiple independent instances behind a load balancer) is used instead
of a clustered deployment.

See [Scalability](#scalability) for more info.

### <a name="resiliency"></a>Resiliency / Fault Tolerance

1. Failover and Recovery
2. Disaster and Recovery

#### Failover and Recovery

No automated failover is supported.

If you are running multiple instances, a failed node can be removed manually by killing
its process:

```bash
kill -9 [pid]
```

where pid is the process id of the particular application node server.

There is no automated recovery, since there is no clustering layer. See [Clustering](#clustering).

#### Disaster and Recovery

DR is not automated and requires manual intervention to bring the application (or group of
applications) back up. The current approach assumes two sites — a Primary and a Standby —
with only the Primary active at any given time.

If the Primary site is considered failed, it should be shut down and the Standby site started
up in its place.

## Ownership

Maintained by the Snowman service team. For architecture questions or production issues,
see the on-call rotation or contact the team directly.
