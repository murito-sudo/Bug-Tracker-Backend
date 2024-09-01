# Bug Tracker Application
Backend App to track Bugs and Tickets within company's projects.

# Technologies Used
Java, Spring Boot, Spring JPA, Spring Security, MySQL, Mockito, Docker.

# How to run the app
- Docker Installed and Running
- Java 17 or Higher

## How to run the app on Docker
- Clone this repository in your local machine.
- Once the project is cloned to your local machine. Go to the project directory folder and run these commands:
 
### Pull Image and create container for MySQL
```
  docker pull luise120/mysql:latest
```
then
```
  docker-compose -f docker-compose-mysql.yml up -d
```

### Pull image and create container for the Spring Boot app:
```
  docker pull luise120/bug-tracker:latest
```
then
```
  docker-compose up -d
```

### Run the Containers
- Before running the spring-boot app container, make sure both MySQL and Mongo containers are running.

# How to run the app locally
- Clone this repository in your local machine.
- Run the project on an IDE or by executing the .jar file on /target/bug-api-1.0.0.jar

# Testing
- For testing purposes, such as: testing new and current functionalities and more, go to application.properties and comment these lines:
  ```
  spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
  spring.jpa.hibernate.ddl-auto=update
  ```
- then uncomment
  ```
  spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
  spring.jpa.hibernate.ddl-auto=create
  ```
  

# Usage
  Wiki Coming Soon

# Docker Images
- [Spring Boot App](https://hub.docker.com/r/luise120/bug-tracker).
- [MySQL Image](https://hub.docker.com/r/luise120/mysql).


# Front-End Coming Soon
