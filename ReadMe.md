# Event Ticket Booking System

A backend application built with Java 21, Spring Boot, and MySQL that allows users to browse events, reserve seats, manage bookings, and receive booking confirmations after successful payment.

## Features

- **User Registration and Login:** Enables users to create accounts and authenticate securely using JWT.

- **Event Browsing:** Allows users to explore available events and select seats for booking.

- **Temporary Seat Reservations:** Holds selected seats for 10 minutes, giving users time to complete the booking process.

- **Booking Management:** Allows authenticated users to view their own bookings and cancel bookings where permitted.

- **Booking Confirmation:** Generates a booking confirmation after successful payment.

- **Persistent Data Storage:** Uses MySQL and Spring Data JPA to store and manage users, events, reservations, and booking records.

## Technology Stack

- Java 21 — Core programming language

- Spring Boot — Backend application framework

- Spring Web — RESTful API development

- Spring Data JPA — Database access and persistence

- Hibernate — Object-relational mapping

- MySQL 8 — Relational database

- Spring Security / JWT — Authentication and endpoint security

- Maven — Dependency management and build automation

## Booking Workflow

- Users register or log in to access the application.

- Users browse events and select their preferred seats.

- The system temporarily reserves the selected seats for 10 minutes.

- Users complete payment before the reservation expires.

- After successful payment, the system generates a booking confirmation.

- Users can view their bookings and cancel bookings according to the application's rules.

## API Documentation
This project uses Swagger UI to document and test the REST API endpoints interactively.

### How to Explore the API

1. Clone the repository and configure the MySQL database.

2. Run the Spring Boot application.

3. Open Swagger UI in your browser:

4. http://localhost:8080/swagger-ui/index.html

Note: Some endpoints require JWT authentication. Log in to obtain a token and authorize your requests in Swagger UI before accessing protected endpoints.


## Getting Started

### Prerequisites

- Java 21 or later

- MySQL 8

- Maven

- Database Setup

- Install and start MySQL.

- Create the application database.

- Configure the database connection and JWT secret using environment variables.


## Project Purpose

This project demonstrates backend development with Java and Spring Boot, RESTful API design, JWT authentication, relational database management, and time-limited seat reservation logic.