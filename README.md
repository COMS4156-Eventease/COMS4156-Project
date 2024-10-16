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

