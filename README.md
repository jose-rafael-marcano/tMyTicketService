# tMyTicketService
Implement a simple ticket service that facilitates the discovery, temporary hold, and final reservation of seats within a high-demand performance venue.
# Project Title TicketService
Ticket service Rest web service using spring boot 2.1, job scheduler  2- swagger ui- spring 5, hibernate and  h2 DB. And AssertJ for junit

We need to add a create a venue, register a customer and a crea a show before we can book seats for a show. Although the example was a venue with 1 stage and 1 floor of 9(rows)x33(cols), I designed the app to support multiple venues/stages and floors.

I also has a strategy pattern and factory to add more seat selection base on a strategy, for now I will only use use 

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

1- Download the project: the project: git clone https://github.com/jose-rafael-marcano/tMyTicketService.git

2- mvn clean install

3- mvn spring-boot:run

4- we can see the api using swagger ui and test it.
http://localhost:8080/swagger-ui.html

You can see 4 countrollers using the ui:
Booking Controller
Customer Controller
Show Controller
Venue Controller

There is a Health endpoint using spring boot actuator.
http://localhost:8080/actuator/health (this is just validating that a venue was created, but it can be expanded to any part of the app that we want to validate that is up and running)


### Prerequisites

What things you need to install the software and how to install them

We need maven 3.5, java 8 sdk and java  runtime >=8

the project: git clone https://github.com/jose-rafael-marcano/tMyTicketService.git .


### Design Considerations
```
1- Using a optimistic locking solution to avoid blocking the database connections and affect performance for concurrence users and reduce chances of deadlock. 

the project: git clone https://github.com/jose-rafael-marcano/tMyTicketService.git .

2- Using a design patter to based on properties configuration inject the correct strategy used to pick the best seats, I am using a simple solution picking the seats based on seat number. If we need a new strategy we need to implement the Strategy interface change the configuration and factory.

3- Using a parameter("contiguousSeat") to determine if we will consider adjacents seats("contiguousSeat": true) if value is false("contiguousSeat": false) we just assign the available seats in order. If the value is true("contiguousSeat": true) then if we selected seat 4 seats but next one is not adjacent to the last one we stop the process and return the selected one so far.

4- Using a temp table for the process  with some primary key and some indexes. we execute one query with show id, venue id, stage id(an index to execute faster) and the result is sorted per seat number. We we commit the reserved seats the app will move the recors to ticket table so that this can be used as a history table and remove the record from booking tables to avoid performances issues with huge tables. So the booking table is temporary for instance if we cancel the show we can delete the records associated to the show.


5- We are using the scheduler and 4 controller on same projects but for production every controller and the scheduler can be separated in different microservices.

6-When we create a venue, these tables are updated:venue,stage, floor, address; and we have indexes and foreign keys defined using jpa.

7-When we create a show , these tables are updated:shows,booking. The id are UID, but for performance we use long for booking, this table is used to select the best seats and to handle the workflow held->reserve->commit. So there is no denormalization for booking and we have indexes to improve performance in addition to connection pool and optimistic locking. If we use a second level cache or even better external cache as redis/mencache so we can improve even more some queries. The app will benefit using this approach when you scale out the service, so you don't need to handle the state in the memory, it is just to process very quickly the best seats so that we don't need replication or synchronization between nodes. 

8- We are using normal design with spring microservice lightweight controllers, logic in service and no DAO(less junit), instead of DAO we are using names queries in JPA repositories.

9- Regardon big O for time complexity  is not more than numbers of rows * cols. we don't have stadiums with  100 floors so this can we considered constant and very few sport venues will have meny rows or seats, in addition to that we can configured how many seats a person can held for that show and it is a low number 10 or 20 so it is O(1), therefore there are no performance issues with complex polinomial algorithms so it should be fast. Regarding the memory we have few records it cannot be a big problem and if we need to scale it we can do it horizontally and load balance the load.

10- We can configure the time the scheduler will remove seat with held label and in a separate property the time to consider a clean up of reserved seats. By clean up, I mean releasing the seats associated to a customer id.

11- We can use a nonOnlySql DB but we need transactions and relational are stronger managing transactional data.

12- We are using spring test and assertj for junit. I am not covering so much code because of time restriction, so I chose one controler and some few class, but I demostrated how to do the integration class, junit the control com mockMvc, and a Health endpoint with spring boot actuators.

13- We are not using docker or Eureka or zuul gateway, neither localization and external property configuration due to time restrictions.





```


### Installing

A step by step series of examples that tell you how to get a development env running

Say what the step will be

```
1- Download the project: git clone https://github.com/jose-rafael-marcano/tMyTicketService.git .

2- mvn clean install

3- mvn spring-boot:run

4-Note: you will need to change the logback configuration in order to point your file to your specific location otherwise it will redirect logs to  C:/example/serviceticket/debug.log, so please the configuration accordingly in the logback file.

```



## Running the tests

Explain how to run the automated tests for this system

Executing : mvn clean install, will execute the junit test cases.


But we also have a h2 console and a swagger ui in order to test the api and this ui will also provide the api documentation.

In order to see the documentation and test the app we can use swagger ui located in: http://localhost:8080/http://localhost:8080/swagger-ui.html
Note: be sure that tomcat is up and running before calling swagger( mvn clean install and mvn spring-boot:run).

We can see the database using this url:http://localhost:8082/ and executing the queries: 
select * from venue;
select * from stage;
select * from floor;
select * from address;

select * from booking ;
select * from shows;
select * from booking where expiration_time is not  null and expiration_time< sysdate();

select sysdate() from shows;
select * from ticket;




There is an integration test that is creating a Venue, then a show inside the venue, registering a customer, and whith customer id,venue id, stage id and show id will held the seats, then reserve the seats and at the end it will buy the seats.
com.walmart.ticketservice.TestFlow.

If we run mvn clean install, we can validate that the integration test is running successfully.

but if you want to see step per step and validate the database we can go with following flow:

First step create a venue and the stages:

click in venue-controller 

http://localhost:8080/swagger-ui.html#/venue-controller


In our case we are using only one step and one floor but the app is design to support multiple stages and floors.


select POST /ticketservice/venue Request to create a Venue location

click option try it out and copy below request

Use this request.

``` REQUEST:  

{
	"name": "San Diego theatre",
	"stages": [{
		"name": "stage miramar",
		"floors": [{
			"floor": 1,
			"cols": 33,
			"rows": 9
		}]
	}],
	"address": {
		"street": "11513 Ford Av.",
		"state": "CA",
		"city": "San Diego",
		"zipCode": "92128"
	}

}

Click execute  and you will see that the Response is http status 201(created) with below json

```
``` RESPONSE:

{
  "stageDTOS": [
    {
      "stageId": "79593ffd-3744-489b-9943-e1b20d2c2226",
      "floors": [],
      "name": "stage miramar"
    }
  ],
  "venueId": "bec7a02b-bc12-46cb-b700-a5ea37730123",
  "name": "San Diego theatre"
}
```


We need to keep track of the venue id and stage id because it is used in next steps(For instance if we have a UI, the client will need to keep these values)

we can validate the values in the h2 database: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
User Nane:sa

select * from venue;
select * from stage;
select * from floor;
select * from address;

We can also use the get to check the values using swagger-ui 
GET /ticketservice/venue/{venueId} getVenue/

so click the get option and the click the "try it out" button.

In the option  venueId add the venue id of the first step(create venue), in our case it is d775c956-4919-4395-b9dc-493ea5304327.

you can the value, if you search with the wrong venue id the app will return the http method 404.

 
 you can delete the option or change it.
 
 
 
 Second step 
 once you created your venue and stored the venue id and stage id, you can create the show.
You cannot create the show twice, or create a show for the same artist/band at the same time. You cannot create the same show for the stage venue time twice.

using swagger-ui go to show-controller
click the option POST /ticketservice/show  Creat show and populate the booking table 

then click try it out.

Use this request: please note  "stageId": "79593ffd-3744-489b-9943-e1b20d2c2226" and  "venueId": "bec7a02b-bc12-46cb-b700-a5ea37730123" are the response values from first step.

```
``` REQUEST:

{
  "artist": "Beyonce",
  "duration": 120,
  "name": "best songs of Beyonce ",
  "priceTickets": 100,
  "stageId": "79593ffd-3744-489b-9943-e1b20d2c2226", 
  "status": "Opened",
  "time": "2018-11-13T20:46:28.928Z",
  "venueId": "bec7a02b-bc12-46cb-b700-a5ea37730123",
  "version": 0
}
```

we are using optimistic locking to handle concurrency request or race conditions so do not modify version field, hibernate will handle that value on behalf of app automatically.


Recall the venue Id and Stage id from step 1, this is used for show configuration. 

click execute button 

``` RESPONSE: 

Response:
{
  "id": "5d44b4e8-43ce-4dc5-8e47-7ced0ebaecca",
  "name": "best songs of Beyonce ",
  "artist": "Beyonce",
  "venueId": "bec7a02b-bc12-46cb-b700-a5ea37730123",
  "stageId": "79593ffd-3744-489b-9943-e1b20d2c2226",
  "time": [
    2018,
    11,
    13,
    20,
    46,
    28,
    928000000
  ],
  "duration": 120,
  "priceTickets": 100,
  "version": 0,
  "status": "Opened"
}

```

We will need to store the show id: "id": "5d44b4e8-43ce-4dc5-8e47-7ced0ebaecca"

we can validate the show in db using;
select * from shows;
select * from booking;

there is one show associated to our artist venue and stage id, and there are 297 seats(our example has 9 rows * 33 columns) available and ready for held/reservation and commit.

But before that we need to register the customet that will buy the tickets.


Steps 3

Register a customer

got to POST ticketservice/customer Request to create a costumer

same procedure as done before click the post then select try it out and use below request or your own request following the example:


``` REQUEST: 

{
  "address": {
    "city": "San Diego",
    "state": "CA",
    "street": "1134 border town Apt. #6",
    "zipCode": "92100"
  },
  "dateOfBirth": "1979-12-11T22:22:10.356Z",
  "lastFourOfCard": 1234,
  "name": "Jose R Marcano",
  "email":"jose.rafael.marcano.r@gmail.com"
}
```


Response is the customer id. 

de2bf5c1-7a91-4540-a3b3-d8477f2d9de7

we are going to use this id to held/reserve/buy seats 

you can see the value in customer table
select * from customer;



Last part booking process


Fourth step

now that we have the customer id and the show id we can start selecting the best available seats.

we are going to new the customer id the show id and stage venue id.


first we can see the seats available

go to booking-controller Booking Controller

GET /ticketservice/availables/{venueId}/{stageId}/{showId} Find all seats available given a venue id, stage and 
seat id. So seats not held/reserved or commited

   click try it out and add the path variables : venue id, stage id and show id.   
   
   "showId":5d44b4e8-43ce-4dc5-8e47-7ced0ebaecca
  "venueId": "bec7a02b-bc12-46cb-b700-a5ea37730123",
  "stageId": "79593ffd-3744-489b-9943-e1b20d2c2226",

   
   then we can call held service. we cannot reserve or buy without call held service
   

click
POST
/ticketservice/booking/hold/
Find all seats available given a venue id, stage and seat id and then held the best ones

try it out below example:
  

``` REQUEST:  
{
  "contiguousSeat": false,
  "customerId": "de2bf5c1-7a91-4540-a3b3-d8477f2d9de7",
  "seatsToHeld": 4,
  "showId": "5d44b4e8-43ce-4dc5-8e47-7ced0ebaecca",
  "stage": "79593ffd-3744-489b-9943-e1b20d2c2226",
  "venueId": "bec7a02b-bc12-46cb-b700-a5ea37730123"
}


```
``` RESPONSE:

response

[
  {
    "seatNumber": 1,
    "floor": 0,
    "col": 1,
    "row": 1,
    "seatId": 0,
    "price": 100,
    "status": null
  },
  {
    "seatNumber": 2,
    "floor": 0,
    "col": 2,
    "row": 1,
    "seatId": 0,
    "price": 100,
    "status": null
  },
  {
    "seatNumber": 3,
    "floor": 0,
    "col": 3,
    "row": 1,
    "seatId": 0,
    "price": 100,
    "status": null
  },
  {
    "seatNumber": 4,
    "floor": 0,
    "col": 4,
    "row": 1,
    "seatId": 0,
    "price": 100,
    "status": null
  }
]
```

now that we have a held service we can reserve the tickets 


click POST /ticketservice/booking/reservation/ Reserve all seats held given a venue id, stage and seat id.
``` REQUEST:
{
  "contiguousSeat": false,
  "customerId": "de2bf5c1-7a91-4540-a3b3-d8477f2d9de7",
  "seatsToHeld": 4,
  "showId": "5d44b4e8-43ce-4dc5-8e47-7ced0ebaecca",
  "stage": "79593ffd-3744-489b-9943-e1b20d2c2226",
  "venueId": "bec7a02b-bc12-46cb-b700-a5ea37730123"
}

```
``` RESPONSE:

response 


200 reserved 


```


Now we can commit the tickets with the exact same request used for reserve.
``` REQUEST:
{
  "contiguousSeat": false,
  "customerId": "de2bf5c1-7a91-4540-a3b3-d8477f2d9de7",
  "seatsToHeld": 4,
  "showId": "5d44b4e8-43ce-4dc5-8e47-7ced0ebaecca",
  "stage": "79593ffd-3744-489b-9943-e1b20d2c2226",
  "venueId": "bec7a02b-bc12-46cb-b700-a5ea37730123"
}

```


NOTE if we don't reserve the tickets within  60 seconds of execute the held api, the scheduled job will release the seats so anyone can pick those seats again. If you reserve it you have 10 minutes to buy the tickets otherwise the scheduler will release the seats.


We can see the database going to http://localhost:8080/h2-console and executing the queries: select * from venue;
select * from stage;
select * from floor;
select * from address;

select * from booking ;
select * from shows;
select * from booking where expiration_time is not  null and expiration_time< sysdate();

select sysdate() from shows;




## Deployment
mvn clean install (be sure that everyting is running)
mvn spring-boot:run

## Built With

* [Spring boot 2.1](https://jersey.github.io/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [Spring 5](https://spring.io/projects/spring-framework) - Spring framework for DI
* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) - Used to compile and run code
* [Hibernate 5](http://hibernate.org/orm/documentation/5.3/) - Used to store in DB
* [H2](https://h2database.com/html/main.html) -  In memory DB
* [AssertJ](https://joel-costigliola.github.io/assertj/) -  Assertion framework used to validate state in junit classes, it is also used mockito



## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We have first version of service.

## Authors

* **Jose R Marcano** - *Initial work* - 

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Thanks Johan Baer for your support.


