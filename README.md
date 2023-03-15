# weblab4

## Description 
This project contains two sides: backend (Spring) and frontend (Vue.js). They are connected through the REST API. 
That part of the project is the backend side, which sends an array of certain dots, that are appropriate to the request (format JSON) of the client side. 
Authorization are provided by Spring Security with JWT tokens. User can sign in or sign up in the system and only after that he are able to send requests. 
After each registration or login server send the token, that must be saved on the client side. Then token must be included in the "Authorization" header in each request,
because this is how server can identify user and give him what he wants. 
Also I am using Spring Data JPA with PostgreSQL driver to store the data.
