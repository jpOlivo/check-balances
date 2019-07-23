# Getting Started

API Rest for checks and balances built on Java. It provides two endpoints:

* One to insert a new monetary transaction, money in or out, for a given user;
* One to return a user's current balance.

### Reference Documentation
For further reference, please consider the following sections:

#### Design

The design of this API is based on the Hexagonal Architecture (also called as ports and adapters). Exist four conceptual layers:

* **Domain**: Contains business logic (domain data structures and business processing rules). This module define and provide ports that are used to communicate with the world. The packages *com.nu.domain, com.nu.service and com.nu.repository* belong to this layer.


* **Infraestructure**: This layer contain implementation details (adapters) related with providing data (or with communicating with the world) for the the domain layer. The implementation of *UserAccountRepository* belong to this layer.


* **Api**: This layer contains adapters that translates calls from other systems to call logic from our domain layer. The package *com.nu.controller* belong to this layer.


* **Application**: Technical module. It wires all the previous modules and contains configuration for the application. The packages *com.nu and com.nu.config* belong to this layer.

The core business is modeling with three classes: *UserAccount, Transaction, OperationType*

A account is identified by the user name.

The money transactions (in-out) are performed onto an user account.

The interactions api-domain and domain-infraestructure are carried out through of the interfaces (ports) *UserAccountService* and *UserAccountRepository* respectively

To avoid that the remote calls are expensive, a set of DTO was defined.

There is  error global handler that treatment the them: *CustomGlobalExceptionHandler*

An In-Memory-Database is used for persist the data of core business.

A dataset is provided with information of four user accounts. It is available in */src/main/resources/data.sql*

##### Concurrency

The API controls the updates on resources using optimistic concurrency control through of ETag and If-Match HTTP headers. The idea:

1. When a client GETs a resource, the API sends the versioning information in the response header field ETag. The ETag contain just a revision number of resource.

2. The client must cache the received versioning information and when calling a PUT operation add it to the request header field If-Match.

3. The API must check if the condition “resource was not modified” is fulfilled.

4. The API inform to client the result of previous step for it to decide how to handle the conflict

On this implementation the revision number is managed by on JPA. Therefore JPA takes care of incrementing the revision number with every update.





#### API Rest

The API documentation can be found [here](http://localhost:8080/swagger-ui.html#)

The following examples illustrate how to use the defined endpoints:

1. Request on *getBalance* endpoint:

`$ curl -X GET "http://localhost:8080/api/accounts/juan/balance" -H "accept: */*"`

2. Request on *registerTransaction* endpoint:

`$ curl -X PUT "http://localhost:8080/api/accounts/juan/transactions" -H "accept: */*" -H "Content-Type: application/json" -H "If-Match: \"1\"" -d "{ \"amount\":100, \"operationType\": \"IN\"}"`


#### Tests

A set of unit and integration tests can be found under */src/test/java*

Use 'mvn test' for execute the unit tests  

`$ mvn test`

Use 'mvn verify' for execute the integration tests

`$ mvn verify`


#### Running the API

There is two alternatives, both depend maven.

1. From Docker

`$ sudo docker run -p 8080:8080 jpolivo/checks-balances`

2. From Spring Boot Maven plugin

`$ mvn spring-boot:run`

3. From Java (it requires Spring Boot Maven plugin to create an executable jar)

`$ java -jar target/checks-balances-0.0.1-SNAPSHOT.jar `

#### Further consideration
Of course this example just shows the prototypical implementation. When adapting it for a real-life scenario I suggest the following optimizations:

* Use modules for have a clear view of layers.
* Use of a logging system for recording of activity 
* Make wide use of javadoc and comments on the classes ;)
* Add alternatives paths on the tests for achieve major coverage
* Build a Docker image for running the API on it
* Use builders to build immutable objects where is necessary
