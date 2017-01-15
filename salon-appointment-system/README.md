# Salon Appointment System

### Use Case

This is a system to manage a business' clients and their appointments. Client information stored is first name, last name, phone number and email.
Each Appointment has a start time, end time and an optional comment.

There are 2 types of users: admins (a.k.a. business' employees) and clients.

   * Admin must have an email.
   * Admin can create, update and delete users.
   * Admin can create, update and delete appointments.  
   * Admin can view all appointments.
   * Client can only view their own appointments. Client must have an email on file to be able to view appointments.

### Technical Notes

This Application Uses Spring Data JPA, Spring Data Rest, Spring HATEOAS, Spring Cloud, Spring Security and Spring Boot. 

In-memory H2 JDBC URL - jdbc:h2:mem:testdb

[H2 Web Console](http://localhost:<port>/console)

[Eureka Console](http://localhost:1111)

[Registered serivces](http://localhost:1111/eureka/apps)
