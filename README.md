# Requirements:
Java 21, Docker

## How to start the application:
1. Run "docker compose up" command in cmd in main application folder
2. Wait for Dynamodb to initialize on docker
3. Edit application configuration and set "Active Profiles" to "local"
4. Build and start the application through IDE
5. Test the APIs with swagger on [localhost:8080/swagger](http://localhost:8080/swagger-ui/index.html#/)
6. To run integration tests Docker application must be on.

# Notes:

## Decisions:
1. If I understood correctly the order is supposed to store the list of requested assets and not be separate for each requested asset
2. Created an endpoint to mark the order as completed and update user's portfolio (would suggest using scheduler, but that depends on real-life requirements)
3. Written some comments in the code for detailed information about implementation of methods/classes, and also suggested improvements
