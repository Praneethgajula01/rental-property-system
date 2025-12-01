Rental Property Listing System

The Rental Property Listing System is a simple backend application built using Java Spring Boot, designed to manage rental property information through REST APIs. It allows users to add new properties, view all available listings, update existing details, and delete records when needed. The project follows a clean layered architecture with separate Controller, Service, Repository, and Entity packages, making the code easy to maintain and extend.

The application uses MySQL as the primary database, and Spring Data JPA is used for smooth interaction with the database. All APIs are fully tested through Postman to ensure correct behavior. To make the project easily deployable, both the Spring Boot application and the MySQL database are containerized using Docker and Docker Compose. This setup ensures consistent behavior across environments and eliminates manual configuration issues.

The project can be run either locally using Maven or inside Docker containers using a single command with Docker Compose. The codebase is structured for clarity, making it suitable for beginners while still reflecting good backend development practices. Future improvements may include adding authentication, implementing a booking module, building a frontend interface, and deploying the application to cloud platforms.
