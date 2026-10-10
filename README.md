# CareerConnect – Job Portal (Spring Boot + SQL Server)

A full-stack job portal where **employers** post jobs and manage applicants, **job seekers** search and apply,
and an **admin** moderates the platform. It has a server-rendered website and a JSON REST API.

**Tech stack:** Java 21 · Spring Boot 3.5 · Spring MVC · Spring Security 6 · Spring Data JPA / Hibernate ·
SQL Server · Thymeleaf · Bootstrap 5 · Maven · JUnit 5 / MockMvc · Postman

---

## Features

| Role | What they can do |
|---|---|
| **Visitor** | Browse and search jobs by keyword, location and job type (paginated), view job details |
| **Job seeker** | Sign up, apply with a cover letter, track status (Applied → Shortlisted → Interview → Hired / Rejected), withdraw |
| **Employer** | Post, edit, close/reopen and delete jobs, see applicant counts, review applicants, update their status |
| **Admin** | Dashboard with platform statistics, block/unblock users, remove any job |

Technical highlights:

- Role-based security with two filter chains: session + form login (with CSRF) for the website, stateless HTTP Basic for `/api/**`
- BCrypt password hashing, ownership checks (an employer can only touch their own jobs/applicants)
- Bean Validation on forms and API bodies; consistent JSON error responses (`400/401/403/404/409`)
- DTOs for API responses, so entities and passwords are never exposed
- Layered architecture: controller → service → repository → entity
- Demo data seeded automatically on first start; integration tests with MockMvc

## Project structure

```
src/main/java/com/jobportal
├── config/        SecurityConfig, DataSeeder (demo data)
├── model/         User, Job, JobApplication + enums (Role, JobType, ApplicationStatus)
├── repository/    Spring Data JPA repositories (custom JPQL search)
├── service/       Business logic: UserService, JobService, ApplicationService
├── web/           Website controllers (Thymeleaf pages)
├── api/           REST controllers + ApiExceptionHandler
├── dto/           Form objects and API request/response records
└── exception/     ResourceNotFoundException
src/main/resources
├── templates/     Thymeleaf pages (home, jobs, auth, employer, candidate, admin)
├── static/css/    style.css
└── application.properties
database/          SQL scripts for SSMS
postman/           Postman collection (25 requests with tests)
```

---

## 1. Set up SQL Server (SSMS)

1. Open **SSMS**, connect, click **New Query**, open `database/01_create_database.sql` and run it (F5).
   It creates the `jobportal_db` database and an optional `jobportal_user` login.
2. Make sure SQL Server accepts the app's connection:
   - **SQL Server authentication must be on:** in SSMS right-click the server → *Properties* → *Security* →
     select **SQL Server and Windows Authentication mode**. Restart the SQL Server service.
   - **TCP/IP on port 1433 must be on:** open **SQL Server Configuration Manager** → *SQL Server Network Configuration* →
     *Protocols for MSSQLSERVER* (or *SQLEXPRESS*) → enable **TCP/IP**. In its properties → *IP Addresses* → *IPAll*,
     set **TCP Port = 1433**. Restart the SQL Server service.
3. Open `src/main/resources/application.properties` and set your credentials:
   ```properties
   spring.datasource.username=jobportal_user
   spring.datasource.password=JobPortal@123
   ```
   (or use `sa` and your sa password). If you use **SQL Express** with a named instance, the URL is usually
   `jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=jobportal_db;encrypt=true;trustServerCertificate=true`
   — but setting TCP port 1433 as above and keeping `localhost:1433` is simpler.

The tables (`users`, `jobs`, `job_applications`) are created automatically when the app starts.

## 2. Import and run in Eclipse

1. **File → Import → Maven → Existing Maven Projects**, choose the `jobportal` folder, click *Finish*.
   Eclipse downloads the dependencies (first time takes a few minutes; needs internet).
2. Make sure Eclipse uses **JDK 21**: *Window → Preferences → Java → Installed JREs* (add a JDK 21 if missing),
   then right-click the project → *Properties → Java Compiler* → 21.
3. If you see red errors after import: right-click the project → **Maven → Update Project** (Alt+F5).
4. Open `JobPortalApplication.java` → right-click → **Run As → Java Application**.
5. Open **http://localhost:8080**.

Command line alternative: `mvn spring-boot:run`

**No SQL Server handy?** Run with the in-memory H2 database instead: in Eclipse *Run Configurations → Arguments →
Program arguments*: `--spring.profiles.active=h2` (or `mvn spring-boot:run -Dspring-boot.run.profiles=h2`).

### Demo accounts (created on first start)

| Role | Email | Password |
|---|---|---|
| Admin | admin@jobportal.com | admin123 |
| Employer | employer@techcorp.com | employer123 |
| Employer | hr@cloudnine.io | employer123 |
| Job seeker | candidate@gmail.com | candidate123 |
| Job seeker | sneha@gmail.com | candidate123 |

## 3. Test the API with Postman

1. In Postman: **Import** → choose `postman/CareerConnect.postman_collection.json`.
2. Start the app, then run the folders in order (or use **Run collection**). Each request has tests, and created
   ids are saved into the collection variables automatically.

### API reference

Auth is **HTTP Basic** (email + password). Public endpoints need no auth.

| Method | Endpoint | Who | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Create a CANDIDATE or EMPLOYER account |
| GET | `/api/auth/me` | Any user | Current account |
| GET | `/api/jobs?keyword=&location=&type=&page=&size=` | Public | Search open jobs (paged) |
| GET | `/api/jobs/{id}` | Public | Job details |
| POST | `/api/jobs` | Employer | Post a job |
| PUT | `/api/jobs/{id}` | Employer (owner) | Update a job |
| PATCH | `/api/jobs/{id}/toggle` | Employer (owner) | Close / reopen a job |
| DELETE | `/api/jobs/{id}` | Employer (owner) / Admin | Delete a job |
| GET | `/api/employer/jobs` | Employer | My posted jobs |
| GET | `/api/jobs/{id}/applications` | Employer (owner) | Applicants for a job |
| POST | `/api/jobs/{id}/apply` | Candidate | Apply (`{"coverLetter": "..."}`) |
| GET | `/api/applications/me` | Candidate | My applications |
| DELETE | `/api/applications/{id}` | Candidate (owner) | Withdraw |
| PATCH | `/api/applications/{id}/status?status=SHORTLISTED` | Employer (owner) | Update status |
| GET | `/api/admin/stats` | Admin | Platform statistics |
| GET | `/api/admin/users` | Admin | All users |
| PATCH | `/api/admin/users/{id}/toggle` | Admin | Block / unblock a user |

Error responses look like:
```json
{ "timestamp": "2026-10-08T11:30:00", "status": 400, "error": "Bad Request",
  "message": "Validation failed", "fieldErrors": { "title": "Job title is required" } }
```

## 4. Run the tests

`mvn test` (or right-click `JobPortalApplicationTests` → *Run As → JUnit Test*). Tests use the H2 profile, so SQL Server
isn't needed.

---

## Database design

```
users (id PK, full_name, email UNIQUE, password, role, phone, headline, enabled, created_at)
  │ 1
  │ *                                   
jobs (id PK, title, company_name, location, job_type, salary, experience, skills,
      description, active, posted_at, employer_id FK → users.id)
  │ 1
  │ *
job_applications (id PK, job_id FK → jobs.id, candidate_id FK → users.id,
                  cover_letter, status, applied_at, updated_at,  UNIQUE(job_id, candidate_id))
```

## Troubleshooting

| Problem | Fix |
|---|---|
| `Login failed for user 'sa'` | Wrong password, or SQL Server authentication mode is off (see step 1.2) |
| `The TCP/IP connection to the host localhost, port 1433 has failed` | Enable TCP/IP and set port 1433 in SQL Server Configuration Manager, restart the service |
| `Cannot open database "jobportal_db"` | Run `database/01_create_database.sql` first |
| `Port 8080 was already in use` | Change `server.port` in `application.properties` |
| Red errors in Eclipse after import | Maven → Update Project, and check the project uses JDK 21 |

## Resume description

**CareerConnect – Job Portal Web Application** | Java, Spring Boot, Spring Security, Spring Data JPA, SQL Server, Thymeleaf, REST, Postman
- Built a full-stack job portal with role-based access for admins, employers and job seekers, covering job posting, search, applications and a hiring pipeline.
- Secured the app with Spring Security: BCrypt passwords, form login with CSRF for the website and stateless HTTP Basic for the REST API, plus ownership checks on every write.
- Designed a normalized SQL Server schema (users, jobs, applications) with JPA/Hibernate and implemented paginated keyword, location and type search with JPQL.
- Exposed 17 REST endpoints with DTOs, Bean Validation and centralized JSON error handling; documented and tested them with a 25-request Postman collection and MockMvc integration tests.
