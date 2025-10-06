# SkyRoute - Flight Booking Platform

[![temp-Imageil-Xg8l.avif](https://i.postimg.cc/C5D6cGMc/temp-Imageil-Xg8l.avif)](https://postimg.cc/Lg9TXZ1P)

A comprehensive Spring Boot application for flight booking and management, built with modern Java technologies and best practices.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [API Documentation](#api-documentation)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Configuration](#configuration)
  - [Running the Application](#running-the-application)
- [API Endpoints examples](#api-endpoints-examples)
  - [Authentication](#authentication)
  - [User Management](#user-management)
  - [Aircraft Management](#aircraft-management)
  - [Airport Management](#airport-management)
  - [Route Management](#route-management)
  - [Flight Management](#flight-management)
  - [Booking Management](#booking-management)
- [Security](#security)
- [Email System](#email-system)
- [Testing](#testing)
- [Deployment](#deployment)
- [Contributing](#contributing)
- [Authors](#authors)

## Overview

SkyRoute is a full-featured flight booking platform that provides comprehensive functionality for both customers and administrators. The system supports user registration, flight search, booking management, and administrative operations with role-based access control.

### Key Highlights

- **Modern Architecture**: Clean layered architecture with Spring Boot
- **Secure Authentication**: JWT-based authentication with role-based access control
- **Email Notifications**: Beautiful HTML email templates for booking confirmations
- **Database Management**: Flyway migrations for version control
- **Comprehensive Testing**: Unit and integration tests with high coverage
- **RESTful API**: Well-documented REST endpoints with Swagger/OpenAPI
- **Container Ready**: Docker and Kubernetes deployment support

## Features

### Core Functionality

- **User Management**: Complete user registration, authentication, and profile management
- **Flight Search**: Advanced flight search with multiple filters and criteria
- **Booking System**: Full booking lifecycle with passenger management
- **Administrative Panel**: Comprehensive admin interface for system management
- **Role-Based Access**: USER and ADMIN roles with appropriate permissions
- **Email Notifications**: Automated email system with beautiful templates

### Technical Features

- **RESTful API**: Well-documented REST endpoints with comprehensive error handling
- **JWT Authentication**: Secure token-based authentication with refresh tokens
- **Image Upload**: Cloudinary integration for image management
- **Email System**: HTML email templates for booking confirmations and cancellations
- **Database Migrations**: Flyway-based database versioning and schema management
- **Comprehensive Testing**: Unit and integration tests with Mockito
- **Docker Support**: Containerized deployment with Docker Compose
- **Kubernetes Ready**: Production deployment configuration
- **API Documentation**: Interactive Swagger/OpenAPI documentation
- **Scheduled Tasks**: Automated flight availability updates

## Technology Stack

### Backend Technologies

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen.svg)
![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6.x-blue.svg)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-3.x-green.svg)
![JWT](https://img.shields.io/badge/JWT-0.12.6-black.svg)

- **Spring Boot 3.5.5** - Main framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence layer
- **Spring Mail** - Email notifications
- **Spring Validation** - Input validation
- **JWT (jjwt 0.12.6)** - Token-based authentication

### Database & Migration

![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)
![H2](https://img.shields.io/badge/H2-2.x-lightblue.svg)
![Flyway](https://img.shields.io/badge/Flyway-9.x-red.svg)

- **MySQL 8.0+** - Production database
- **H2 Database** - Development and testing
- **Flyway** - Database migrations and version control

### External Services

![Cloudinary](https://img.shields.io/badge/Cloudinary-2.0.0-blue.svg)
![JavaMail](https://img.shields.io/badge/JavaMail-SMTP-green.svg)

- **Cloudinary** - Image upload and management
- **JavaMail** - Email delivery service
- **SMTP** - Email transport protocol

### Development Tools

![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-green.svg)
![Lombok](https://img.shields.io/badge/Lombok-1.18+-pink.svg)
![JUnit](https://img.shields.io/badge/JUnit-5-purple.svg)
![Mockito](https://img.shields.io/badge/Mockito-5.x-orange.svg)

- **Maven** - Build and dependency management
- **Swagger/OpenAPI** - API documentation
- **Lombok** - Code generation and boilerplate reduction
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework for testing

## Project Structure

```
src/
├── main/
│   ├── java/com/skyroute/skyroute/
│   │   ├── aircraft/          # Aircraft management
│   │   │   ├── controller/    # REST endpoints
│   │   │   ├── dto/          # Data transfer objects
│   │   │   ├── entity/       # JPA entities
│   │   │   ├── repository/   # Data access layer
│   │   │   └── service/      # Business logic
│   │   ├── airport/          # Airport management
│   │   ├── auth/             # Authentication & authorization
│   │   ├── booking/          # Booking system
│   │   │   ├── controller/   # Booking endpoints
│   │   │   ├── dto/         # Booking DTOs
│   │   │   ├── entity/      # Booking entity
│   │   │   ├── enums/       # Booking status enums
│   │   │   ├── repository/  # Booking repository
│   │   │   ├── service/     # Booking business logic
│   │   │   └── specification/ # Search specifications
│   │   ├── cloudinary/      # Image upload service
│   │   ├── email/           # Email templates & service
│   │   │   ├── BookingEmailTemplates.java
│   │   │   ├── BookingConfirmationStatusEmailTemplates.java
│   │   │   ├── BookingCancellationEmailTemplates.java
│   │   │   ├── RegistrationEmailTemplates.java
│   │   │   ├── PasswordResetEmailTemplates.java
│   │   │   └── EmailService.java
│   │   ├── flight/            # Flight management
│   │   │   ├── controller/  # Flight endpoints
│   │   │   ├── dto/         # Flight DTOs
│   │   │   ├── entity/      # Flight entity
│   │   │   ├── helper/      # Flight utilities
│   │   │   ├── repository/  # Flight repository
│   │   │   ├── scheduler/   # Scheduled tasks
│   │   │   ├── service/     # Flight business logic
│   │   │   ├── specification/ # Flight search
│   │   │   └── validation/  # Flight validation
│   │   ├── route/            # Route management
│   │   ├── security/        # Security configuration
│   │   │   ├── details/     # User details service
│   │   │   └── jwt/         # JWT utilities
│   │   ├── shared/          # Shared utilities
│   │   │   ├── exception/   # Custom exceptions
│   │   │   └── util/        # Utility classes
│   │   ├── user/            # User management
│   │   └── swagger/         # API documentation
│   └── resources/
│       ├── db/migration/    # Database migrations
│       ├── static/          # Static resources
│       └── application*.yml # Configuration files
└── test/                    # Test classes
```

## Database Schema

### Core Entities

- **User**: User accounts with roles and profile information
- **Aircraft**: Aircraft information (capacity, model, manufacturer)
- **Airport**: Airport data (code, city, image)
- **Route**: Flight routes connecting airports
- **Flight**: Flight details (number, schedule, price, availability)
- **Booking**: Booking records with passenger information

### Key Relationships

- **User → Booking** (One-to-Many)
- **Aircraft → Flight** (One-to-Many)
- **Route → Flight** (One-to-Many)
- **Flight → Booking** (One-to-Many)
- **Airport → Route** (Many-to-One for origin/destination)

### Database Migrations

The project uses Flyway for database versioning with the following migrations:

- **V1**: Create users table
- **V2**: Create airports table
- **V3**: Create aircrafts table
- **V4**: Create routes table
- **V5**: Create flights table
- **V6**: Create booking table
- **V7**: Insert test data
- **V8**: Add password reset fields
- **V9**: Expand test data
- **V10**: Add Valencia airport and flights
- **V11**: Add one seat flight

## API Documentation

The API is fully documented using Swagger/OpenAPI. Once the application is running, you can access the interactive documentation at:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## Getting Started

### Prerequisites

- **Java 21** or higher
- **Maven 3.6** or higher
- **Docker** (optional, for containerized deployment)
- **MySQL 8.0+** (for production)

### Installation

1. **Clone the repository**

   ```bash
   git clone https://github.com/FF5-DreamTeam/skyroute.git
   cd skyroute
   ```

2. **Build the project**

   ```bash
   mvn clean install
   ```

3. **Set up environment variables**
   ```bash
   cp env.example .env
   # Edit .env with your configuration
   ```

### Configuration

#### Environment Variables

Create a `.env` file based on `env.example`:

```bash
# Database Configuration
DB_URL=jdbc:h2:mem:testdb
DB_USERNAME=sa
DB_PASSWORD=password

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-here-make-it-very-long-and-secure
JWT_EXPIRATION=3600000        # 1 hour
JWT_REFRESH_EXPIRATION=86400000 # 24 hours

# Mail Configuration
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-email-password

# Cloudinary Configuration
CLOUDINARY_CLOUD_NAME=your-cloudinary-cloud-name
CLOUDINARY_API_KEY=your-cloudinary-api-key
CLOUDINARY_API_SECRET=your-cloudinary-api-secret

# Application Configuration
SPRING_PROFILES_ACTIVE=local
```

#### Application Profiles

- **local**: H2 in-memory database for development
- **test**: Test configuration with H2
- **docker**: MySQL with Docker Compose

### Running the Application

#### Local Development

```bash
mvn spring-boot:run
```

#### With Docker

```bash
docker-compose up -d
```

The application will be available at `http://localhost:8080`

## API Endpoints examples

### Authentication

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login

### User Management

- `POST /api/users` - Create user
- `PUT /api/users/profile` - Update user profile

### Aircraft Management

- `GET /api/aircrafts` - Get all aircrafts
- `POST /api/aircrafts` - Create aircraft

### Airport Management

- `GET /api/airports` - Get all airports
- `POST /api/airports` - Create airport

### Route Management

- `GET /api/routes` - Get all routes
- `POST /api/routes` - Create route

### Flight Management

- `GET /api/flights/search` - Search flights by parameters
- `GET /api/flights/min-prices` - Get minimum prices by destinations

### Booking Management

- `POST /api/bookings` - Create booking
- `POST /api/bookings/{id}/confirm` - Confirm booking

## Security

### Authentication

- **JWT-based authentication** with access and refresh tokens
- **Token blacklisting** for secure logout
- **Password encryption** using BCrypt
- **Access Token**: 1 hour expiration
- **Refresh Token**: 24 hours expiration

### Authorization

- **Role-based access control** (USER, ADMIN)
- **Method-level security** with @PreAuthorize
- **Context-aware access control**
- **Resource ownership validation**

### Security Features

- **CORS configuration** for frontend integration
- **CSRF protection** disabled for API usage
- **Stateless session management**
- **Secure password handling**
- **Input validation** and sanitization

## Email System

SkyRoute includes a comprehensive email notification system with beautiful HTML templates:

### Email Templates

- **Registration Confirmation**: Welcome email for new users
- **Booking Created**: Initial booking confirmation
- **Booking Confirmed**: Admin confirmation notification
- **Booking Cancelled**: Cancellation notification with refund info
- **Password Reset**: Secure password reset links

### Template Features

- **Responsive Design**: Mobile-friendly HTML templates
- **Beautiful Styling**: Matches website design with gradients
- **Table-based Layout**: Maximum email client compatibility
- **Interactive Elements**: Call-to-action buttons
- **Professional Branding**: Consistent SkyRoute branding

### Email Configuration

```yaml
# Email settings in application.properties
spring.mail.host=${MAIL_HOST}
spring.mail.port=${MAIL_PORT}
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
```

## Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run tests with coverage
mvn test jacoco:report
```

### Test Coverage

- Unit tests for all service layers
- Integration tests for controllers
- Mock-based testing for isolated components
- Test profiles for different environments

## Deployment

### Docker Deployment

```bash
# Build Docker image
docker build -t skyroute:latest .

# Run with Docker Compose
docker-compose up -d
```

### Kubernetes Deployment

```bash
# Apply Kubernetes manifests
kubectl apply -f kubernetes/

# Check deployment status
kubectl get pods
kubectl get services
```

### Environment-Specific Configuration

- **Development**: H2 in-memory database
- **Testing**: H2 with test data
- **Production**: MySQL with proper configuration

## Contributing

1. **Fork the repository**
2. **reate a feature branch** (`git checkout -b feature/amazing-feature`)
3. **Commit your changes** (`git commit -m 'Add some amazing feature'`)
4. **Push to the branch** (`git push origin feature/amazing-feature`)
5. **Open a Pull Request**

### Development Guidelines

- **Write tests** for new features
- **Update documentation** for API changes
- **Follow code style** guidelines
- **Ensure all tests pass** before submitting PR

## Authors

- **Judit** - https://github.com/J-uds
- **Alexandra** - https://github.com/Alexandracoder
- **May** - https://github.com/may-leth
- **Nadiia** - https://github.com/tizzifona
- **Vitaliia** - https://github.com/vitaFlash

---

<div align="center">

**Made with ❤️ by the SkyRoute Dream Team**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

</div>
