## Friend Management API

### Background

For any application with a need to build its own social network, "Friends Management" 
is a common requirement which usually starts off simple but can grow in complexity 
depending on the application's use case.

Usually, applications would start with features like "Friend", "Unfriend", "Block", 
"Receive Updates" etc.

### Design

#### Table

##### Users table
The USERS table stores the users uniquely identified by their email addresses.

##### Relationship table
The RELATIONSHIP table stores relationships and the status of the relationships 
between two users.

The statuses of the relationship are:
* is friend
* is blocked
* is subscribed to update

A new row will be created for every new connection a user has with another user with
the following rules:
* A relationship will be created for each of the users when one friends another, 

  e.g. A friends B

  A ----- B
  
  B ----- A

* Only a relationship will be create when a user blocks/subscribed to another user, 

  e.g. A blocks/subscribes B

  A ----- B 

#### API
We will check the email(s) with the USERS table in every API calls and return an error if
the user email is not found with the exception of the make friend API call. The user record
will be created automatically if it is not found because there is no other way to create the
user record. 

The autolink library is a convenient library used to extract the email addresses from the 
text instead of writing my own implementation.

The H2 database is chosen for its simplicity for the test. However it can be switch easily 
with other database of your choice.

Fields validations are performed via Hibernate Validator whenever possible, as it provides a
consistent way of validation and the validation rules can be read easily.

Query DSL is chosen because it provides type safety and builder like query mechanism. It 
provides a consistent way to query over SQL when working in a team.

### Running the Project
To run the project, you need to download Docker (https://store.docker.com/search?type=edition&offering=community)
* Checkout the project
* Open the shell terminal
* Navigate to the project folder (where the Dockerfile resides) and run the following command:
````
docker build -t friend-management-demo .
````
* To start the application, run the following command
````
docker run -p 8080:8080 friend-management-demo
````