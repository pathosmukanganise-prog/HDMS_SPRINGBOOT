# Help Desk Management System

A Spring Boot help desk application for logging, tracking, assigning, and resolving support tickets.

The project includes role-based access control, file attachments, ticket comments, a tiled ticket dashboard, and an admin analytics page with graphical reporting.

## Features

- Secure login with Spring Security
- Two seeded accounts for first-time access
- Create, search, assign, update, resolve, and delete tickets
- Role-aware access to ticket details and attachments
- Ticket comments for collaborators on the same ticket
- Tiled dashboard and personal ticket tracking views
- Admin user management
- Admin analytics dashboard with charts and CSV export

## Tech Stack

- Java 17+
- Spring Boot 3
- Spring MVC
- Thymeleaf
- Spring Data JPA
- Spring Security
- MySQL
- Bootstrap 5
- Chart.js

## Default Accounts

The application seeds these users on startup if they do not already exist:

- `admin` / `admin123`
- `user` / `1234`

## Configuration

Current application settings are in [application.properties](C:/Users/it/IdeaProjects/helpdesk-secured/src/main/resources/application.properties:1).

Important defaults:

- Server port: `8081`
- Database: `jdbc:mysql://localhost:3306/helpdesk_db`
- Multipart upload limit: `10MB`

Update the datasource values before running if your local MySQL credentials differ.

## Run Locally

### 1. Create the database

Create a MySQL database named `helpdesk_db`.

### 2. Review database credentials

Edit `src/main/resources/application.properties` if needed:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/helpdesk_db
spring.datasource.username=user
spring.datasource.password=YES
```

### 3. Start the application

```bash
mvn spring-boot:run
```

Or build the jar and run it:

```bash
mvn package
java -jar target/hdms-1.0.jar
```

### 4. Open the app

Visit:

```text
http://localhost:8081
```

## Main Screens

- `/login` - sign in
- `/` - main ticket dashboard
- `/track` - personal ticket tracking page
- `/admin/users` - user administration for admins
- `/admin/reports` - graphical analytics dashboard for admins

## Admin Analytics

The admin reports page includes:

- KPI cards for total, open, resolved, and resolution rate
- Status distribution chart
- Tickets-by-creator chart
- Tickets-by-assignee chart
- Ticket detail table
- CSV export from `/admin/reports/tickets.csv`

## Project Structure

```text
src/main/java/com/helpdesk
  config/        Security configuration
  controller/    MVC controllers
  model/         JPA entities
  repository/    Data repositories
  service/       Business logic

src/main/resources
  templates/     Thymeleaf views
  application.properties
```

## Notes

- Ticket attachments are stored in the local `uploads/` folder.
- JPA schema mode is set to `update`, so tables are created or adjusted automatically.
- There are currently no automated tests in the repository.

## Future Improvements

- Add automated tests for controllers and services
- Add ticket priority, categories, and SLA tracking
- Add audit history and richer reporting filters
- Move secrets out of source-controlled config
