# tMyTicketService
Implement a simple ticket service that facilitates the discovery, temporary hold, and final reservation of seats within a high-demand performance venue.
# Project Title TicketService
Reminder Rest web service using spring boot 2.1, job scheduler  2- swagger ui- spring 5, hibernate and  h2 DB. And AssertJ for junit

We need to add a create a venue, register a customer and a crea a show before we can book seats for a show. Although the example was a venue with 1 stage and 1 floor of 9(rows)x33(cols), I designed the app to support multiple venues/stages and floors.

I also has a strategy pattern and factory to add more seat selection base on a strategy, for now I will only use use 

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

1- Download the project: git clone https://github.com/jose-rafael-marcano/tMyTicketService.git .

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



### Installing

A step by step series of examples that tell you how to get a development env running

Say what the step will be

```
1- Download the project: git clone https://github.com/jose-rafael-marcano/tMyTicketService.git .

2- mvn clean install

3- mvn spring-boot:run
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

Click exeute  and you will see that the Response is http status 201(created) with below json

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

we are using optimistic locking to handle concurrency request or race conditions so do not modify version hibernate will handle that value on behalf of app automatically.


Recall the venue Id and Stage id from step 1, this is used for show configuration. 

click exeute button 

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

Request 

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
   
{
  "contiguousSeat": false,
  "customerId": "de2bf5c1-7a91-4540-a3b3-d8477f2d9de7",
  "seatsToHeld": 4,
  "showId": "5d44b4e8-43ce-4dc5-8e47-7ced0ebaecca",
  "stage": "79593ffd-3744-489b-9943-e1b20d2c2226",
  "venueId": "bec7a02b-bc12-46cb-b700-a5ea37730123"
}


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

now that we have a held service we can reserve the tickets 


click POST /ticketservice/booking/reservation/ Reserve all seats held given a venue id, stage and seat id.

{
  "contiguousSeat": false,
  "customerId": "de2bf5c1-7a91-4540-a3b3-d8477f2d9de7",
  "seatsToHeld": 4,
  "showId": "5d44b4e8-43ce-4dc5-8e47-7ced0ebaecca",
  "stage": "79593ffd-3744-489b-9943-e1b20d2c2226",
  "venueId": "bec7a02b-bc12-46cb-b700-a5ea37730123"
}

response 


200 reserved 




now we can commit the tickets with the exact same request used for reserve.

NOTE if we don't reserve the tickets within  60 seconds of execute the held api, the scheduled job will release the seats so anyone can pick those seats again. If you reserve it you have 10 minutes to buy the tickets otherwise the scheduler will release the seats.


``` REQUEST:
The json used to create a reminder is 
{
  "id": 0,
  "name": "test1",
  "description": "description ",
  "dueDate": "2018-07-29T09:00:32.568Z",
  "status": "DONE"
}
dueDate cannot be a past date because service will trigger a validation error.
status can only be NOTDONE or DONE otherwise service will throw a validation error.

```

``` RESPONSE:

{
    "dueDate": "2018-07-27T09:41:18.707-07:00",
    "error": false,
    "id": 3,
    "message": "need to finish before tomorrow first reminder",
    "status": "DONE",
    "type": "email type"
}

We need to keep the id(primary key in the database) number for future transactions, for instance if we need to update or delete a reminder the service will expect such id, if the id is incorrect or is not present in the database the application will respond back with an error. 
```

Steps for testing:
1-Open google crome and copy the url:http://localhost:8080/reminderservice/

2- click in any http method that you wish to test, for instance if we want to add a reminder, click in the POST

3- Click the bottom Try it out.

4- You will see how it is enable the json file, please see the comments regarding the date format, you don't need to add an id, the app will generate one automatically.

{
  "id": 0,
  "name": "string",
  "description": "string",
  "dueDate": "2018-07-27T15:12:56.958Z",
  "status": "NOTDONE"
}

5- Then click the botton execute, if you don't change the date see the error in the secction Responses
addReminder.arg0.dueDate - must be in the future
This is intentionally in order to demostate the validation of the field using jersey validation.

6- After changing the date and getting a successful response, take note of the id that will be used for update and delete operations later on,also see the dueDate field that will be used in the get method.

Request
{
  "id": 0,
  "name": "string",
  "description": "string",
  "dueDate": "2018-07-28T15:12:56.958Z",
  "status": "NOTDONE"
}

``` RESPONSE:


201	
Response body
Download
{
  "description": "string",
  "dueDate": "2018-07-28T08:12:56.958-07:00",
  "error": false,
  "id": 1,
  "message": "created reminder:1",
  "name": "string",
  "status": "NOTDONE",
  "tcn": 1,
  "type": "email type"
}
Response headers
 content-length: 181 
 content-type: application/json 
 date: Fri, 27 Jul 2018 15:19:37 GMT 
 server: Apache-Coyote/1.1 
```


7-Now we click in Get method, same thihg for the first time click Try it out.

if you see the response in step 6, we can query using the dueDate=2018-07-28T08:12:56.958-07:00 and/or the status=NOTDONE

8- For update or delete we repeat same operations, click the method, then Try it out for the first time

9- Update Reminder, click PUT method

``` REQUEST:
{
  "id": 1,
  "name": "Reminder1",
  "description": "reminder used for  zumba class",
  "dueDate": "2018-07-27T15:33:10.814Z",
  "status": "NOTDONE"
}

```



``` RESPONSE:

{
  "description": "reminder used for  zumba class",
  "dueDate": "2018-07-29T08:33:10.814-07:00",
  "error": false,
  "id": 1,
  "message": "Updated reminder:1",
  "name": "Reminder1",
  "status": "NOTDONE",
  "tcn": 0,
  "type": "email type"
}

Now we can see the changes in the get method, go to step 7 and use the value status=NOTDONE or/and "dueDate": "2018-07-29T08:33:10.814-07:00", repeat same steps for any update and get.
```


10- Delete a Reminder, click Delete method, click Try it out for first time, modify json with id.

``` REQUEST:
{
  "id": 1,
  "name": "string",
  "description": "string",
  "dueDate": "2018-07-27T15:41:24.146Z",
  "status": "NOTDONE"
}

See that we can pass other values in order to delete using other criterias, but for now, only modify the id with value 1 that we got in step 5,6.
```



``` RESPONSE:
Ther is  no body, only http status 204 if the record is deleted successfuly, you can validate the result in the database or using the get method in the ui.

Response:
Code	Details
204	
Response headers
 date: Fri, 27 Jul 2018 15:44:50 GMT 
 server: Apache-Coyote/1.1 


We can see the database going to http://localhost:8080/h2-console and executing the queries: select * from venue;
select * from stage;
select * from floor;
select * from address;

select * from booking ;
select * from shows;
select * from booking where expiration_time is not  null and expiration_time< sysdate();

select sysdate() from shows;


```



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


