# 🎓 UniPortal - University Management System

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-brightgreen?style=for-the-badge&logo=spring)
![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED?style=for-the-badge&logo=docker)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Template-005F0F?style=for-the-badge&logo=thymeleaf)

**A modern, full-stack university management system with professional UI/UX design**

[Features](#-features) • [Installation](#-installation) • [Usage](#-usage) • [Architecture](#-architecture) • [API Reference](#-api-reference)

</div>

---

## 📖 Overview

UniPortal is a comprehensive university management system built with Spring Boot that enables seamless interaction between students and teachers. The application features a modern, professional UI with glassmorphism effects, smooth animations, and an intuitive sidebar navigation system.

### Key Highlights

- 🎨 **Professional UI/UX** - Modern glassmorphism design with smooth animations
- 🔐 **Secure Authentication** - BCrypt password hashing with Spring Security
- 👥 **Role-Based Access** - Separate dashboards for students and teachers
- 📚 **Course Management** - Full CRUD operations for courses
- 📊 **Real-time Analytics** - Dashboard statistics and metrics
- 🐳 **Docker Ready** - Complete containerization with Docker Compose

---

## ✨ Features

### For Students
| Feature | Description |
|---------|-------------|
| 📊 **Dashboard Analytics** | View enrolled courses, total credits, and available courses at a glance |
| 📚 **Course Enrollment** | Browse and enroll in available courses with one click |
| 📖 **My Courses** | View and manage enrolled courses |
| 🚪 **Drop Courses** | Easily drop courses when needed |

### For Teachers/Admin
| Feature | Description |
|---------|-------------|
| 📊 **Dashboard Overview** | Monitor total courses, credits, students, and enrollments |
| ➕ **Create Courses** | Add new courses with title and credit hours |
| 🗑️ **Delete Courses** | Remove courses from the system |
| 👨‍🎓 **Student Management** | Add, view, and delete student accounts |
| 📈 **Statistics** | Real-time metrics on student enrollments |

---

## 🛠️ Tech Stack

### Backend
- **Java 17** - Modern Java with latest features
- **Spring Boot 4.0.2** - Latest Spring Boot framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database ORM with Hibernate 7.2.1
- **PostgreSQL 15** - Robust relational database

### Frontend
- **Thymeleaf** - Server-side template engine
- **Modern CSS3** - Glassmorphism, flexbox, grid layouts
- **Vanilla JavaScript** - Dynamic interactions without frameworks
- **Google Fonts (Inter)** - Professional typography

### DevOps
- **Docker** - Container runtime
- **Docker Compose** - Multi-container orchestration
- **Maven** - Build automation

---

## 📦 Installation

### Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running
- Git (optional, for cloning)

### Quick Start with Docker

1. **Clone the repository**
   ```bash
   git clone https://github.com/k-i-mahi/SpringBoot_apk.git
   cd SpringBoot_apk
   ```

2. **Build the application**
   ```bash
   # On Windows
   .\mvnw.cmd clean package -DskipTests
   
   # On Linux/Mac
   ./mvnw clean package -DskipTests
   ```

3. **Start with Docker Compose**
   ```bash
   docker-compose up --build -d
   ```

4. **Access the application**
   - Open your browser and navigate to: **http://localhost:9090**

### Manual Setup (Without Docker)

1. **Install PostgreSQL 15** and create a database:
   ```sql
   CREATE DATABASE university;
   CREATE USER admin WITH PASSWORD 'admin';
   GRANT ALL PRIVILEGES ON DATABASE university TO admin;
   ```

2. **Update `application.yaml`** with your database credentials

3. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

---

## 🚀 Usage

### Demo Credentials

| Role | Email | Password |
|------|-------|----------|
| 👨‍🎓 Student | `mahi@mail.com` | `password` |
| 👨‍🎓 Student | `student@mail.com` | `password` |
| 👨‍🏫 Teacher | `teacher@mail.com` | `password` |
| 👑 Admin | `admin@mail.com` | `password` |

### Login Page Features
- **Quick Access Buttons** - Click on demo accounts to auto-fill credentials
- **Animated Background** - Floating shapes with smooth animations
- **Glassmorphism Design** - Modern frosted glass effect
- **Error Handling** - Clear feedback for invalid login attempts

### Student Dashboard
1. **View Statistics** - See enrolled courses, credits, and available courses
2. **Browse Courses** - Scroll through all available courses
3. **Enroll** - Click "Enroll" button to join a course
4. **Drop Course** - Click "Drop" to unenroll from a course

### Teacher Dashboard
1. **View Metrics** - Monitor courses, credits, students, and enrollments
2. **Add Course** - Fill in title and credits, click "Add Course"
3. **Delete Course** - Click delete button on any course
4. **Manage Students** - Add new students or remove existing ones

---

## 🏗️ Architecture

### Project Structure
```
SpringBoot_apk/
├── src/
│   └── main/
│       ├── java/com/example/student_teacher/
│       │   ├── StudentTeacherApplication.java    # Main entry point
│       │   ├── config/
│       │   │   └── SecurityConfig.java           # Spring Security config
│       │   ├── controller/
│       │   │   ├── StudentController.java        # Student REST APIs
│       │   │   ├── TeacherController.java        # Teacher REST APIs
│       │   │   └── WebController.java            # View controllers
│       │   ├── entity/
│       │   │   ├── Course.java                   # Course entity
│       │   │   ├── Student.java                  # Student entity
│       │   │   ├── Teacher.java                  # Teacher entity
│       │   │   └── Role.java                     # Role enum
│       │   ├── repository/
│       │   │   ├── CourseRepository.java
│       │   │   ├── StudentRepository.java
│       │   │   └── TeacherRepository.java
│       │   └── security/
│       │       └── CustomUserDetailsService.java # Auth service
│       └── resources/
│           ├── application.yaml                  # App configuration
│           ├── data.sql                          # Seed data
│           └── templates/
│               ├── login.html                    # Login page
│               ├── student-dashboard.html        # Student UI
│               └── teacher-dashboard.html        # Teacher UI
├── docker-compose.yml                            # Docker services
├── Dockerfile                                    # App container
├── pom.xml                                       # Maven dependencies
└── README.md                                     # Documentation
```

### Database Schema

```
┌──────────────┐     ┌─────────────────┐     ┌──────────────┐
│   STUDENT    │     │ STUDENT_COURSES │     │    COURSE    │
├──────────────┤     ├─────────────────┤     ├──────────────┤
│ id (PK)      │────<│ student_id (FK) │     │ id (PK)      │
│ name         │     │ course_id (FK)  │>────│ title        │
│ email (UK)   │     └─────────────────┘     │ credit       │
│ password     │                              │ teacher_id   │>──┐
│ role         │                              └──────────────┘   │
└──────────────┘                                                 │
                                              ┌──────────────┐   │
                                              │   TEACHER    │   │
                                              ├──────────────┤   │
                                              │ id (PK)      │<──┘
                                              │ name         │
                                              │ email (UK)   │
                                              │ password     │
                                              │ role         │
                                              └──────────────┘
```

---

## 📡 API Reference

### Student Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/student/dashboard` | View student dashboard |
| `POST` | `/student/courses/enroll/{id}` | Enroll in a course |
| `POST` | `/student/courses/drop/{id}` | Drop a course |

### Teacher Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/teacher/dashboard` | View teacher dashboard |
| `POST` | `/teacher/courses/add` | Create a new course |
| `POST` | `/teacher/courses/delete/{id}` | Delete a course |
| `GET` | `/teacher/students` | Get all students (JSON) |
| `POST` | `/teacher/students` | Add a new student (JSON) |
| `DELETE` | `/teacher/students/{id}` | Delete a student |

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/login` | Login page |
| `POST` | `/login` | Authenticate user |
| `GET` | `/logout` | Logout user |

---

## 🎨 UI/UX Design Features

### Design System
- **Color Palette**: 
  - Primary: `#6366f1` (Indigo)
  - Secondary: `#8b5cf6` (Purple)
  - Success: `#10b981` (Emerald)
  - Accent: `#06b6d4` (Cyan)

### Visual Effects
- **Glassmorphism** - Frosted glass effect with `backdrop-filter: blur()`
- **Gradient Backgrounds** - Smooth color transitions
- **Floating Animations** - Subtle movement for visual interest
- **Hover Transitions** - Interactive feedback on buttons and cards

### Responsive Design
- Sidebar navigation (collapsible on mobile)
- Fluid grid layouts
- Touch-friendly button sizes

---

## 🔧 Configuration

### Application Properties (`application.yaml`)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/university
    username: admin
    password: admin
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
  sql:
    init:
      mode: always

server:
  port: 8080
```

### Docker Compose Services

| Service | Image | Port |
|---------|-------|------|
| `postgres` | `postgres:15` | `5432:5432` |
| `app` | Custom build | `9090:8080` |

---

## 🧪 Testing

```bash
# Run unit tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

---

## 📋 Troubleshooting

### Common Issues

1. **Port already in use**
   ```bash
   # Stop existing containers
   docker-compose down
   # Or change port in docker-compose.yml
   ```

2. **Database connection failed**
   - Ensure PostgreSQL container is running
   - Check credentials in `application.yaml`

3. **Login not working**
   - Password for all demo accounts is `password`
   - Clear browser cookies and try again

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Mahi**
- GitHub: [@k-i-mahi](https://github.com/k-i-mahi)

---

<div align="center">

**⭐ Star this repository if you found it helpful!**

Made with ❤️ using Spring Boot

</div>
