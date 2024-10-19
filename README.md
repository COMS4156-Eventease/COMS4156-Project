# COMS4156-Project
Repository for COMS 4156 24F, Team Scrum Masters 

EventEase is a flexible RESTful service designed to simplify and automate the complexities of event management. EventEase offers a seamless solution to manage everything from event creation to event registration. With endpoints for task management, RSVP handling, and real-time reporting, EventEase empowers various applications with robust event management capabilities, allowing organizers and hosters to focus on delivering great user experiences. EventEase API makes it easy to integrate sophisticated event planning tools into any system, no matter the size or scope of your event.

To distinguish from regular event-management services and make it useful for elderly users, Eventease provides SMS and email reminders for events, allowing elderly users to stay informed without relying on complex apps. Additionally, a simple RSVP system could be integrated, enabling one-click RSVP via email or SMS, reducing the need for navigating complicated interfaces. The API would also support caregiver collaboration, allowing caregivers to manage event details, send RSVPs, or receive notifications on behalf of elderly users. Lastly, an option for simplified, large-text invitations or event details could be offered to ensure better accessibility for users with vision impairments or low digital literacy.


## Technologies Used
- **Spring Boot**: 
- **Spring MVC**: For structuring the application with the MVC pattern.
- **MySQL**: As the relational database for data storage.



### Installation 

## Prerequisite
- **Java 17**
- **Maven**
- **Spring Boot 3.3.4**
- **Ubuntu 24**
  
## Installing the Repository
Open the terminal in the folder in which you wish to clone the repository and enter the following command:
```
git clone https://github.com/LouisLu00/COMS4156-Project.git
cd COMS4156-Project
```
## Building and running a local instance

The instructions below are for Ubuntu 24.04, however they should be the same for all platforms supported by Java and MySQL.
To build and run the service, install the following:

- **Maven 3.9.5**: [Download Maven](https://maven.apache.org/download.cgi) and follow the installation steps. Ensure the `bin` directory is added to your system's path.
- **JDK 17**: [Download JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html), as it is the recommended version for this project.
- **MySQL 8.0**: [Download MySQL](https://dev.mysql.com/downloads/mysql/) and follow the installation steps. Ensure the `bin` directory is added to your system's path.
- **IntelliJ IDE**: [Download IntelliJ](https://www.jetbrains.com/idea/download/?section=windows) or use any IDE you prefer.


## Changes to be done before starting the Springboot application

## Start your MySQL server

The below command is for Ubuntu
``` bash
sudo systemctl start mysql
mysql -u root -p
```

## Changing application.properties
Going to the application.properties in the directory src/main/resources and updating application.properties with your username and password 

``` bash
# UPDATE THIS BELOW ACCORDING TO YOUR SETTINGS
spring.datasource.username=root
spring.datasource.passWord=root 
```

To run the application, use:
```bash
mvn spring-boot:run 
```

## For creating a test coverage report and code style check
Generate test coverage report:
```bash
mvn jacoco:report
```

Currently the branch coverage sits at 61%:
![image](https://github.com/user-attachments/assets/02905c92-01c0-43b1-8c5b-a6424ca00a8b)


Run code style checks:
```
mvn checkstyle:check
```

Currently there are no style reports:
![image](https://github.com/user-attachments/assets/1a22e18a-5186-41fa-bd20-a812f0b7296c)



# Automated CI/CD Pipeline

This project has an automatic CI/CD pipeline set up using GitHub Actions. The pipeline is triggered on every commit and pull request across the repository.

The pipeline does the following:
- Compile the Java application
- Run unit tests using `mvn test`
- Runs and publishes a JaCoCo test coverage report as an artifact
- Checks code style using Checkstyle


# Development lifecycle

For every new feature, a new branch should be created. Once that branch is ready to be merged, at least one other maintainer will need to approve it before it is able to be merged. 


# Postman Test Documentation 

The Postman test published documentation can be found in this link


## Created Endpoints
### Event Management
#### POST /api/events
* Expected Input Parameters:
  * Request Parameters:
    * organizerId (Long): The ID of the organizer (user creating the event) passed as a query parameter.
  * Request Body:
    * event (Object):
      * name (String): Name of the event.
      * description (String): Description of the event.
      * location (String): Location where the event is held.
      * date (String): The date of the event (in YYYY-MM-DD format).
      * time (String): The time of the event (in HH:MM format).
      * capacity (int): Maximum number of participants allowed.
      * budget (int): Budget for the event.
* Expected Output: A JSON object containing the organizerId and the eventId of the newly created 
      event.
* Operation: Creates a new event with the specified details and sets the organizer for the event.
* Upon Success:
  * HTTP 201 Status Code with:
  *  `{
    "organizerId": "The organizer's user ID",
    "eventId": "The newly created event's ID"
    }`
* Upon Failure:
  * HTTP 404 Status Code with "Organizer not found" if the provided organizerId is invalid.
  * HTTP 500 Status Code with an error message in the response body in case of any other server 
    error.
  
#### GET /api/events/{eventId}
* Expected Input Parameters:
  * Path Parameter:
    * eventId (Long): The ID of the event to retrieve.
* Expected Output: A JSON object representing the event details.

* Operation: Retrieves the details of a specific event by its ID.

* Upon Success:
  * HTTP 200 Status Code with the full details of the event in the response body.
* Upon Failure:
  * HTTP 404 Status Code with "Event not found" if the event with the provided eventId does not 
  exist.
  * HTTP 500 Status Code with "An unexpected error has occurred" in the response body.

#### GET /api/events
* Expected Input Parameters:

  * Query Parameters:
    * startDate (String): The start date of the range in YYYY-MM-DD format.
    * endDate (String): The end date of the range in YYYY-MM-DD format.
* Expected Output: A list of event objects that fall within the specified date range.
* Operation: Retrieves a list of events between the specified start and end dates.
* Upon Success:
  * HTTP 200 Status Code with the list of events in the response body.
* Upon Failure:
  * HTTP 400 Status Code if the date parameters are not provided or invalid.
  * HTTP 500 Status Code with "An unexpected error has occurred" in the response body.

#### PATCH /api/events/{eventId}
* Expected Input Parameters:
  * Path Parameter:
    * eventId (Long): The ID of the event to update.
  * Request Body:
    * event (Object):
      * name (String): Name of the event.
      * description (String): Description of the event.
      * location (String): Location where the event is held.
      * date (String): The date of the event (in YYYY-MM-DD format).
      * time (String): The time of the event (in HH:MM format).
      * capacity (int): Maximum number of participants allowed.
      * budget (int): Budget for the event.
* Expected Output: A string indicating the result of the operation.
* Operation: Updates the specified event's details. Only the provided fields will be updated.
* Upon Success:
  * HTTP 200 Status Code with "Event updated successfully" in the response body.
* Upon Failure:
  * HTTP 404 Status Code with "Event not found" if the event with the provided eventId does not exist.
  * HTTP 400 Status Code with "Failed to update event" in case of invalid input or other errors.


### RSVP Management 

#### POST /api/events/{eventId}/rsvp/{userId}
* POST RSVP for an event
* Expected Path Variables: eventId (String), userId(String)
* Expected Output: RSVP Object
* Upon Success: HTTP 201 Status Code is returned 
* Upon Failure: 
  * HTTP 400 Status Code is returned with the message indicating RSVP already exists
  * HTTP 404 Status Code is returned along with the message indicating user or event not found.
  * HTTP 500 Internal Server Error for other issues

#### GET /api/events/{eventId}/attendees
* Retrieves the list of attendees for a given event, identified by eventId.
* Expected Path Variables: eventId (String)
* Expected Output: List of RSVP Object
* Upon Success: 200 OK with the list of attendees 
* Upon Failure:
  * HTTP 404 Status Code is returned if the event is not found.
  * HTTP 500 Internal Server Error for other issues

#### DELETE /api/events/{eventId}/rsvp/cancel/{userId}
* Cancels an RSVP for a user to a specified event.
* Expected Path Variables: eventId (String), userId(String)
* Expected Output: Success Message (String)
* Upon Success: 200 OK with a successful cancellation message.
* Upon Failure:
  * HTTP 404 Status Code is returned if the event/user/rsvp is not found.
  * HTTP 500 Internal Server Error for other issues



### Task Management
#### POST /api/events/{eventId}/tasks
* Creates a new task for the specified event and assigns it to a user.
* Expected Input Parameters:
  * Path Parameters:
    * eventId (String): The ID of the event for which the task is being created.
  * Query Parameters:
    * userId (String): The ID of the user creating the task.
    * Request Body (JSON):
    * name (String): Name of the task.
    * description (String): Description of the task.
    * status (String): Status of the task. Valid values: PENDING, COMPLETED, IN_PROGRESS, CANCELLED.
    * dueDate (String): Due date of the task in YYYY-MM-DD HH:MM:SS format.
* Upon Success:
  * HTTP 201 Status Code with:
  *  `{
     "taskId": "The newly created task's ID",
     "eventId": "The event's ID",
     "userId": "The user's ID"
     }`
* Upon Failure:
  * HTTP 404 Status Code is returned along with the message indicating user or event not found.
  * HTTP 500 Internal Server Error for other issues.
    
#### GET /api/events/{eventId}/tasks
* Retrieves all tasks associated with a specific event.
* Expected Input Parameters:
  * Path Parameters:
    * eventId (String): The ID of the event to retrieve tasks from.
* Upon Success:
  * HTTP 200 Status Code with list of tasks associated with event.
* Upon Failure:
  * HTTP 404 Status Code is returned along with the message indicating event not found.
  * HTTP 500 Internal Server Error for other issues.

#### GET /api/events/{eventId}/tasks/{taskId}
* Retrieves a specific task by its ID and associated event.
* Expected Input Parameters:
  * Path Parameters:
    * taskId (String): The ID of the event and specific task to retrieve.
* Upon Success:
  * HTTP 200 Status Code with task and its respective details.
* Upon Failure:
  * HTTP 404 Status Code is returned along with the message indicating event or task not found.
  * HTTP 500 Internal Server Error for other issues.

#### PATCH /api/events/{eventId}/tasks/{taskId}/status
* Updates the status of a specific task within an event.
* Expected Input Parameters:
  * Path Parameters:
    * eventId (String): The ID of the event containing the task that is being updated.
    * taskId (String): The ID of the specific task being updated.
  * Query Parameters:
    * status (String): Status of the task. Valid values: PENDING, COMPLETED, IN_PROGRESS, CANCELLED.
* Upon Success:
  * HTTP 200 Status Code with task and its respective details.
* Upon Failure:
  * HTTP 400 Status Code is returned if the request body does not contain a valid status enumeration.
  * HTTP 404 Status Code is returned along with the message indicating event or task not found.
  * HTTP 500 Internal Server Error for other issues.

#### PATCH /api/events/{eventId}/tasks/{taskId}/user
* Updates the assigned user for a specific task within an event.
* Expected Input Parameters:
  * Path Parameters:
    * eventId (String): The ID of the event containing the task that is being updated.
    * taskId (String): The ID of the specific task being updated.
  * Query Parameters:
    * userId (String): User ID to be assigned task to.
* Upon Success:
  * HTTP 200 Status Code with task and its respective details.
* Upon Failure:
  * HTTP 404 Status Code is returned along with the message indicating user, event, or task not found.
  * HTTP 500 Internal Server Error for other issues.

#### DELETE /api/events/{eventId}/tasks/{taskId}
* Deletes a specific task associated with an event.
* Expected Input Parameters:
  * Path Parameters:
    * eventId (String): The ID of the event containing the task deleting.
    * taskId (String): The ID of the specific task being deleted.
* Upon Success:
  * HTTP 200 Status Code with successful deletion message.
* Upon Failure:
  * HTTP 404 Status Code is returned along with the message indicating event or task not found.
  * HTTP 500 Internal Server Error for other issues.

## User Management

### POST /api/users/add
* Adds a new user to the system.
* Expected Input Parameters:
  * Request Body (JSON):
    * firstName (String): The first name of the user.
    * lastName (String): The last name of the user.
    * email (String): The email address of the user.
    * phone (String, optional): The phone number of the user.
    * role (String, optional): The role of the user (e.g., ADMIN, USER).
* Upon Success:
  * HTTP 200 Status Code with the message "User saved successfully."
* Upon Failure:
  * HTTP 400 Bad Request for invalid input data.
  * HTTP 500 Internal Server Error for other issues.



### GET /api/users/list
* Retrieves a list of users based on filter criteria.
* Expected Input Parameters:
  * Query Parameters:
    * firstName (String, optional): The first name to filter users.
    * lastName (String, optional): The last name to filter users.
    * email (String, optional): The email address to filter users.
    * phone (String, optional): The phone number to filter users.
    * role (String, optional): The role to filter users (e.g., ADMIN, USER).
* Upon Success:
  * HTTP 200 Status Code with a list of users.
* Upon Failure:
  * HTTP 500 Internal Server Error for other issues.



### PATCH /api/users/update/{id}
* Updates an existing user's details.
* Expected Input Parameters:
  * Path Parameters:
    * id (Long): The ID of the user to be updated.
  * Request Body (JSON):
    * firstName (String, optional): The new first name of the user.
    * lastName (String, optional): The new last name of the user.
    * email (String, optional): The new email address of the user.
    * phone (String, optional): The new phone number of the user.
    * role (String, optional): The new role of the user (e.g., ADMIN, USER).
* Upon Success:
  * HTTP 200 Status Code with the message "User updated successfully."
* Upon Failure:
  * HTTP 404 Status Code if the user is not found.
  * HTTP 500 Internal Server Error for other issues.



### DELETE /api/users/delete/{id}
* Deletes a specific user from the system.
* Expected Input Parameters:
  * Path Parameters:
    * id (Long): The ID of the user to be deleted.
* Upon Success:
  * HTTP 200 Status Code with the message "User deleted successfully."
* Upon Failure:
  * HTTP 404 Status Code if the user is not found.
  * HTTP 500 Internal Server Error for other issues.
