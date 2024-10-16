# COMS4156-Project
Repository for COMS 4156 24F, Team Scrum Masters 

## Prerequisite
- **Java 17**
- **Maven**
- **Spring Boot 3.3.4**
- **Ubuntu 24**
  
## Technologies Used
- **Spring Boot**: 
- **Spring MVC**: For structuring the application with the MVC pattern.
- **MySQL**: As the relational database for data storage.

## Endpoints
### Event Management

### RSVP Management 

#### POST /events/{eventId}/rsvp/{userId}
* POST RSVP for an event
* Expected Path Variables: eventId (String), userId(String)
* Expected Output: RSVP Object
* Upon Success: HTTP 201 Status Code is returned 
* Upon Failure: 
  * HTTP 400 Status Code is retunred with the message indicating RSVP already exists
  * HTTP 404 Status Code is returned along with the message indicating user or event not found.
  * HTTP 500 Internal Server Error for other issues

#### GET /events/{eventId}/attendees
* Retrieves the list of attendees for a given event, identified by eventId.
* Expected Path Variables: eventId (String)
* Expected Output: List of RSVP Object
* Upon Success: 200 OK with the list of attendees 
* Upon Failure:
  * HTTP 404 Status Code is returned if the event is not found.
  * HTTP 500 Internal Server Error for other issues

#### DELETE /events/{eventId}/rsvp/cancel/{userId}
* Cancels an RSVP for a user to a specified event.
* Expected Path Variables: eventId (String), userId(String)
* Expected Output: Success Message (String)
* Upon Success: 200 OK with a successful cancellation message.
* Upon Failure:
  * HTTP 404 Status Code is returned if the event/user/rsvp is not found.
  * HTTP 500 Internal Server Error for other issues



### Task Management 

### User Management 