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
- [API Endpoints](#api-endpoints)
  - [Authentication](#authentication)
  - [User Management](#user-management)
  - [Aircraft Management](#aircraft-management)
  - [Airport Management](#airport-management)
  - [Route Management](#route-management)
  - [Flight Management](#flight-management)
  - [Booking Management](#booking-management)
- [Security](#security)
- [Testing](#testing)
- [Deployment](#deployment)
- [Contributing](#contributing)

## Overview

SkyRoute is a full-featured flight booking platform that provides comprehensive functionality for both customers and administrators. The system supports user registration, flight search, booking management, and administrative operations with role-based access control.

## Features

### Core Functionality

- **User Management**: Complete user registration, authentication, and profile management
- **Flight Search**: Advanced flight search with multiple filters
- **Booking System**: Full booking lifecycle with passenger management
- **Administrative Panel**: Comprehensive admin interface for system management
- **Role-Based Access**: USER and ADMIN roles with appropriate permissions

### Technical Features

- **RESTful API**: Well-documented REST endpoints
- **JWT Authentication**: Secure token-based authentication
- **Image Upload**: Cloudinary integration for image management
- **Email Notifications**: Automated email system for bookings
- **Database Migrations**: Flyway-based database versioning
- **Comprehensive Testing**: Unit and integration tests
- **Docker Support**: Containerized deployment
- **Kubernetes Ready**: Production deployment configuration

## Technology Stack

### Backend

![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence
- **Flyway** - Database migrations


### Database

  ![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)
  ![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)
- **H2** - In-memory database for development/testing
### External Services

![Cloudinary](https://img.shields.io/badge/Cloudinary-3448C5?style=for-the-badge&logo=Cloudinary&logoColor=white)
- **JavaMail** - Email notifications
- **SMTP** - Email delivery

### Development Tools

![Maven](https://img.shields.io/badge/apachemaven-C71A36.svg?style=for-the-badge&logo=apachemaven&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Kubernetes](https://img.shields.io/badge/kubernetes-%23326ce5.svg?style=for-the-badge&logo=kubernetes&logoColor=white)

## Project Structure

```
src/
├── main/
│   ├── java/com/skyroute/skyroute/
│   │   ├── aircraft/          # Aircraft management
│   │   ├── airport/           # Airport management
│   │   ├── auth/              # Authentication
│   │   ├── booking/           # Booking system
│   │   ├── cloudinary/        # Image upload service
│   │   ├── email/             # Email notifications
│   │   ├── flight/            # Flight management
│   │   ├── route/             # Route management
│   │   ├── security/          # Security configuration
│   │   ├── shared/            # Shared utilities
│   │   ├── user/              # User management
│   │   └── swagger/           # API documentation
│   └── resources/
│       ├── db/migration/      # Database migrations
│       ├── static/            # Static resources
│       └── application*.yml   # Configuration files
└── test/                      # Test classes
```

## Database Schema

### Entities

- **User**: User accounts with roles and profile information
- **Aircraft**: Aircraft information (capacity, model, manufacturer)
- **Airport**: Airport data (code, city, image)
- **Route**: Flight routes connecting airports
- **Flight**: Flight details (number, schedule, price, availability)
- **Booking**: Booking records with passenger information

### Key Relationships

- User → Booking (One-to-Many)
- Aircraft → Flight (One-to-Many)
- Route → Flight (One-to-Many)
- Flight → Booking (One-to-Many)
- Airport → Route (Many-to-One for origin/destination)

## API Documentation

The API is fully documented using Swagger/OpenAPI. Once the application is running, you can access the interactive documentation at:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker (optional, for containerized deployment)
- MySQL (for production)

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

## API Endpoints

### Authentication

| Method | Endpoint             | Description       | Access        |
| ------ | -------------------- | ----------------- | ------------- |
| POST   | `/api/auth/register` | Register new user | Public        |
| POST   | `/api/auth/login`    | User login        | Public        |
| POST   | `/api/auth/refresh`  | Refresh JWT token | Public        |
| POST   | `/api/auth/logout`   | User logout       | Authenticated |

### User Management

| Method | Endpoint          | Description               | Access        |
| ------ | ----------------- | ------------------------- | ------------- |
| POST   | `/api/users`      | Create user               | Public        |
| GET    | `/api/users/{id}` | Get user by ID            | Authenticated |
| GET    | `/api/users`      | Get all users (paginated) | Admin         |
| PUT    | `/api/users/{id}` | Update user               | Admin         |
| DELETE | `/api/users/{id}` | Delete user               | Admin         |

### Aircraft Management

| Method | Endpoint              | Description        | Access |
| ------ | --------------------- | ------------------ | ------ |
| POST   | `/api/aircrafts`      | Create aircraft    | Admin  |
| GET    | `/api/aircrafts`      | Get all aircrafts  | Public |
| GET    | `/api/aircrafts/{id}` | Get aircraft by ID | Public |
| PUT    | `/api/aircrafts/{id}` | Update aircraft    | Admin  |
| DELETE | `/api/aircrafts/{id}` | Delete aircraft    | Admin  |

### Airport Management

| Method | Endpoint             | Description       | Access |
| ------ | -------------------- | ----------------- | ------ |
| POST   | `/api/airports`      | Create airport    | Admin  |
| GET    | `/api/airports`      | Get all airports  | Public |
| GET    | `/api/airports/{id}` | Get airport by ID | Public |
| PUT    | `/api/airports/{id}` | Update airport    | Admin  |
| DELETE | `/api/airports/{id}` | Delete airport    | Admin  |

### Route Management

| Method | Endpoint           | Description     | Access |
| ------ | ------------------ | --------------- | ------ |
| POST   | `/api/routes`      | Create route    | Admin  |
| GET    | `/api/routes`      | Get all routes  | Public |
| GET    | `/api/routes/{id}` | Get route by ID | Public |
| PUT    | `/api/routes/{id}` | Update route    | Admin  |
| DELETE | `/api/routes/{id}` | Delete route    | Admin  |

### Flight Management

#### Admin Endpoints

| Method | Endpoint                  | Description             | Access |
| ------ | ------------------------- | ----------------------- | ------ |
| POST   | `/api/admin/flights`      | Create flight           | Admin  |
| GET    | `/api/admin/flights/{id}` | Get flight by ID        | Admin  |
| GET    | `/api/admin/flights/page` | Get flights (paginated) | Admin  |
| PUT    | `/api/admin/flights/{id}` | Update flight           | Admin  |
| DELETE | `/api/admin/flights/{id}` | Delete flight           | Admin  |

#### Public Endpoints

| Method | Endpoint              | Description      | Access |
| ------ | --------------------- | ---------------- | ------ |
| GET    | `/api/flights/search` | Search flights   | Public |
| GET    | `/api/flights/{id}`   | Get flight by ID | Public |

### Booking Management

| Method | Endpoint                                   | Description                   | Access        |
| ------ | ------------------------------------------ | ----------------------------- | ------------- |
| GET    | `/api/bookings`                            | Get all bookings (paginated)  | Admin         |
| GET    | `/api/bookings/user`                       | Get user bookings (paginated) | User          |
| GET    | `/api/bookings/{id}`                       | Get booking by ID             | Authenticated |
| POST   | `/api/bookings`                            | Create booking                | Authenticated |
| PUT    | `/api/bookings/{id}/status`                | Update booking status         | Authenticated |
| POST   | `/api/bookings/{id}/confirm`               | Confirm booking               | Authenticated |
| POST   | `/api/bookings/{id}/cancel`                | Cancel booking                | Authenticated |
| PUT    | `/api/bookings/{id}/passenger-names`       | Update passenger names        | Authenticated |
| PUT    | `/api/bookings/{id}/passenger-birth-dates` | Update birth dates            | Authenticated |
| DELETE | `/api/bookings/{id}`                       | Delete booking                | Authenticated |

## Security

### Authentication

- JWT-based authentication with access and refresh tokens
- Token blacklisting for secure logout
- Password encryption using BCrypt

### Authorization

- Role-based access control (USER, ADMIN)
- Method-level security with @PreAuthorize
- Context-aware access control

### Security Features

- CORS configuration for frontend integration
- CSRF protection disabled for API usage
- Stateless session management
- Secure password handling

## Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

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

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request


