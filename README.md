## WANDERLUST REST API

​

EVENTS

DELETE  /{user_id}/events/{event_id}

DELETE  /{user_id}/events/{event_id}/

GET     /{user_id}/events/{event_id}

GET     /{user_id}/events/{event_id}/eventInfo

GET     /{user_id}/events/{event_id}/subscriber_count

GET     /{user_id}/events/{event_id}/subscribers

POST    /{user_id}/events/{event_id}/eventInfo

PUT     /{user_id}/events/{event_id}

PUT     /{user_id}/events/{user_id}/subscribing/{target} (*demonstration purposes only)



USERS

GET     /{user_id}                     

DELETE  /{user_id}                     

POST    /{user_id}/posts

GET     /{user_id}/AllOtherUsers

GET     /{user_id}/AllUsers

GET     /{user_id}/subscribers         

GET     /{user_id}/subscribers_count   

GET     /{user_id}/subscribing         

GET     /{user_id}/subscribing_count   

GET     /{user_id}/posts               

GET     /{user_id}/messageBoard       

PUT     /{user_id}                    

PUT     /{user_id}/subscribing/{target} (*demonstration purposes only)

DELETE  /{user_id}/unsubcribe/{target} (*demonstration purposes only)

---------------------------------------------------------------------------------------------
APRIL 4TH 2017 (Latest API)

    DELETE  /users/{user_id} (com.wanderlust.resources.UserResource)
    DELETE  /users/{user_id}/subscribing/{target} (com.wanderlust.resources.UserResource)
    GET     /users/AllUsers (com.wanderlust.resources.UserResource)
    GET     /users/{user_id} (com.wanderlust.resources.UserResource)
    GET     /users/{user_id}/AllOtherUsers (com.wanderlust.resources.UserResource)
    GET     /users/{user_id}/messageBoard (com.wanderlust.resources.UserResource)
    GET     /users/{user_id}/posts (com.wanderlust.resources.UserResource)
    GET     /users/{user_id}/requests (com.wanderlust.resources.UserResource)
    GET     /users/{user_id}/subscribers (com.wanderlust.resources.UserResource)
    GET     /users/{user_id}/subscribing (com.wanderlust.resources.UserResource)
    GET     /users/{user_id}/subscribing_count (com.wanderlust.resources.UserResource)
    POST    /users/{user_id} (com.wanderlust.resources.UserResource)
    POST    /users/{user_id}/posts (com.wanderlust.resources.UserResource)
    POST    /users/{user_id}/subscribeRequest/{target} (com.wanderlust.resources.UserResource)
    PUT     /users/{target}/ARequest/{user_id} (com.wanderlust.resources.UserResource)
    PUT     /users/{target}/RRequest/{user_id} (com.wanderlust.resources.UserResource)
    PUT     /users/{user_id} (com.wanderlust.resources.UserResource)
    PUT     /users/{user_id}/about (com.wanderlust.resources.UserResource)
    PUT     /users/{user_id}/age (com.wanderlust.resources.UserResource)
    PUT     /users/{user_id}/fname (com.wanderlust.resources.UserResource)
    PUT     /users/{user_id}/interests (com.wanderlust.resources.UserResource)
    PUT     /users/{user_id}/language (com.wanderlust.resources.UserResource)
    PUT     /users/{user_id}/lname (com.wanderlust.resources.UserResource)
    PUT     /users/{user_id}/origin (com.wanderlust.resources.UserResource)
    PUT     /users/{user_id}/places (com.wanderlust.resources.UserResource)
    PUT     /users/{user_id}/publicSubscribing/{target} (com.wanderlust.resources.UserResource)
    DELETE  /events/{event_id} (com.wanderlust.resources.EventResource)
    DELETE  /events/{event_id}/members (com.wanderlust.resources.EventResource)
    GET     /events/AllEvents (com.wanderlust.resources.EventResource)
    GET     /events/{event_id} (com.wanderlust.resources.EventResource)
    GET     /events/{event_id}/members (com.wanderlust.resources.EventResource)
    PUT     /events/{event_id} (com.wanderlust.resources.EventResource)
    PUT     /events/{event_id}/about (com.wanderlust.resources.EventResource)
    PUT     /events/{event_id}/author (com.wanderlust.resources.EventResource)
    PUT     /events/{event_id}/coordinates (com.wanderlust.resources.EventResource)
    PUT     /events/{event_id}/languages (com.wanderlust.resources.EventResource)
    PUT     /events/{event_id}/location (com.wanderlust.resources.EventResource)
    PUT     /events/{event_id}/member (com.wanderlust.resources.EventResource)
    PUT     /events/{event_id}/name (com.wanderlust.resources.EventResource)
