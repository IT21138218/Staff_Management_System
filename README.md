# Staff Management System

A full-stack web application for managing staff: employees, departments, attendance, leave, payroll, performance and shift scheduling, with role-based access for different kinds of users.

- **Backend:** Spring Boot 3.2 (Java 17), Spring Security with JWT, Spring Data JPA, MySQL (or H2 for local development)
- **Frontend:** React 18, Vite 5, React Router, Axios

## Features

| Module | What it does |
|---|---|
| Authentication | Login and registration, JWT-based sessions, role-based route protection |
| Employees | Create, view, edit, activate and deactivate employee records, with profile photo upload |
| Departments | Manage departments and their staff |
| Attendance | Clock in/out, late-arrival detection, personal and team attendance views |
| Leave | Apply for leave, track leave balances, approve or reject requests |
| Payroll | Generate monthly payslips with tax and overtime calculations, view payslips |
| Performance | Set goals and record performance reviews |
| Scheduling | Shifts, roster management and shift swap requests |
| Notifications | In-app notifications (for example, when a leave request is approved) |
| Reports & dashboard | Summary statistics and reports for managers |

### User roles

`ADMIN`, `HR_MANAGER`, `SUPERVISOR`, `PAYROLL_OFFICER` and `EMPLOYEE`. Each role sees only the pages and actions it is allowed to use.

### Design patterns

The backend uses several classic design patterns, found in [`backend/src/main/java/com/sliit/sms/common/patterns`](backend/src/main/java/com/sliit/sms/common/patterns):

| Pattern | Where it's used |
|---|---|
| Singleton | `PayrollConfig`: one shared set of payroll settings (tax rate, overtime multiplier) |
| Strategy | `StandardSalaryStrategy` / `OvertimeSalaryStrategy`: interchangeable salary calculations |
| Builder | `PayslipBuilder`: step-by-step construction of payslips and their line items |
| Factory | `NotifierFactory`: creates in-app or email notifiers |
| Observer | `LeaveStatusPublisher` / `LeaveStatusObserver`: notifies employees when a leave request changes status |

## Project structure

```
Staff_Management_System/
├── backend/                  Spring Boot REST API (port 8080)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/sliit/sms/
│       │   ├── common/patterns/   Design pattern implementations
│       │   ├── config/            Security, JWT, CORS, data seeding
│       │   ├── controller/        REST controllers (/api/...)
│       │   ├── dto/               Request objects with validation
│       │   ├── entity/            JPA entities and enums
│       │   ├── exception/         Error handling
│       │   ├── repository/        Spring Data repositories
│       │   ├── service/           Service interfaces and implementations
│       │   └── util/
│       └── resources/             application.properties, application-h2.properties
└── frontend/                 React + Vite app (port 5173)
    └── src/
        ├── api/              Axios API clients, one per module
        ├── components/       Layout and shared UI components
        ├── context/          Authentication context
        └── pages/            Pages grouped by module
```

## Getting started

### Prerequisites

- Java 17 or newer
- Maven 3.8 or newer
- Node.js 18 or newer, with npm
- MySQL 8. Optional: you can use the built-in H2 database instead.

### 1. Run the backend

```bash
cd backend
```

**Option A: with MySQL (default)**

Make sure MySQL is running. The default connection is `root` / `root` on `localhost:3306`. Change it in [`backend/src/main/resources/application.properties`](backend/src/main/resources/application.properties) if yours is different. The `sms_db` database is created automatically.

```bash
mvn spring-boot:run
```

**Option B: with H2, no MySQL needed**

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

This stores data in `backend/data/`.

The API starts on **http://localhost:8080/api**.

### 2. Run the frontend

In a second terminal:

```bash
cd frontend
cp .env.example .env      # on Windows: copy .env.example .env
npm install
npm run dev
```

Open **http://localhost:5173**.

The frontend reads the API address from `VITE_API_BASE_URL` in `frontend/.env`. The default is `http://localhost:8080/api`.

## Demo accounts

On first start, the backend seeds sample departments, leave types, shifts and these users:

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `Admin@123` |
| HR Manager | `hr.manager` | `Hr@12345` |
| Supervisor | `supervisor` | `Super@123` |
| Payroll Officer | `payroll.officer` | `Payroll@123` |
| Employee | `employee` | `Employee@123` |

## Configuration

Main settings in `application.properties`:

| Property | Default | Purpose |
|---|---|---|
| `server.port` | `8080` | Backend port |
| `spring.datasource.*` | local MySQL, `root`/`root` | Database connection |
| `jwt.secret` | development value | Key used to sign login tokens. **Change before any real deployment.** |
| `jwt.expiration-ms` | `86400000` (24 h) | How long a login lasts |
| `sms.uploads.dir` | `./uploads` | Where employee photos are stored |
| `sms.attendance.work-start-time` | `09:00` | Start of the working day |
| `sms.attendance.late-grace-minutes` | `15` | Minutes after start before an arrival counts as late |
| `sms.payroll.tax-rate` | `0.10` | Tax rate applied to payslips |
| `sms.payroll.overtime-multiplier` | `1.5` | Overtime pay multiplier |

## Building for production

```bash
# Backend: creates backend/target/staff-management-system-2.0.0.jar
cd backend
mvn clean package
java -jar target/staff-management-system-2.0.0.jar

# Frontend: creates frontend/dist/
cd frontend
npm run build
```
