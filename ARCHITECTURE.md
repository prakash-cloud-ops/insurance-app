# Insurance Policy Management System - Architecture & Features

## 📋 Executive Summary

A production-ready, full-stack insurance policy management system built with modern technologies, containerized using Docker, and designed for enterprise-level deployment. This system demonstrates best practices in software architecture, security, and DevOps workflows.

---

## 🏗️ System Architecture

### Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                         Client Browser                       │
│                    (http://localhost:3000)                   │
└────────────────────────────┬────────────────────────────────┘
                             │
                             │ HTTP/HTTPS
                             │
┌────────────────────────────▼────────────────────────────────┐
│                    Frontend Container                        │
│                    (Nginx + React SPA)                       │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  React 18 Application                                 │  │
│  │  - TailwindCSS Styling                                │  │
│  │  - Recharts Visualizations                            │  │
│  │  - React Router (Client-side routing)                 │  │
│  │  - Axios (API client with JWT interceptor)            │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                              │
│  Nginx:                                                      │
│  - Serves static React build                                │
│  - Proxies /api/* → backend:8080                            │
└────────────────────────────┬────────────────────────────────┘
                             │
                             │ REST API calls
                             │ (JWT in Authorization header)
                             │
┌────────────────────────────▼────────────────────────────────┐
│                    Backend Container                         │
│                   (Spring Boot 3.2.0)                        │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Spring Security Layer                                │  │
│  │  - JWT Token Validation                               │  │
│  │  - Authentication Filter                              │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  REST Controllers                                      │  │
│  │  - AuthController (/api/auth/*)                        │  │
│  │  - PolicyController (/api/policies/*)                  │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Service Layer                                         │  │
│  │  - Business logic                                      │  │
│  │  - Data transformation                                 │  │
│  └──────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Repository Layer (Spring Data JPA)                    │  │
│  │  - PolicyRepository                                    │  │
│  │  - UserRepository                                      │  │
│  └──────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────┘
                             │
                             │ JDBC Connection
                             │ (JPA/Hibernate ORM)
                             │
┌────────────────────────────▼────────────────────────────────┐
│                    Database Container                        │
│                        MySQL 8.0                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Tables:                                               │  │
│  │  - users (authentication)                              │  │
│  │  - policies (policy records)                           │  │
│  │                                                         │  │
│  │  Data:                                                  │  │
│  │  - 1 admin user (seeded)                               │  │
│  │  - 20 sample policies (seeded)                         │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                              │
│  Persistent Volume: mysql_data                              │
└─────────────────────────────────────────────────────────────┘

All containers connected via Docker Network: insurance-network
```

---

## 🔧 Technology Stack

### Frontend
| Technology | Version | Purpose |
|------------|---------|---------|
| React | 18.2.0 | UI framework |
| Vite | 5.0.8 | Build tool & dev server |
| TailwindCSS | 3.3.6 | Utility-first CSS framework |
| Recharts | 2.10.3 | Data visualization library |
| React Router | 6.20.0 | Client-side routing |
| Axios | 1.6.2 | HTTP client |
| React Hot Toast | 2.4.1 | Toast notifications |

### Backend
| Technology | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 3.2.0 | Application framework |
| Java | 17 | Programming language |
| Spring Security | 6.x | Authentication & authorization |
| Spring Data JPA | 3.x | Data persistence |
| Hibernate | 6.x | ORM framework |
| JJWT | 0.12.3 | JWT token handling |
| MySQL Connector | 8.x | Database driver |
| Spring Actuator | 3.x | Health checks & monitoring |

### Database
| Technology | Version | Purpose |
|------------|---------|---------|
| MySQL | 8.0 | Relational database |

### DevOps
| Technology | Version | Purpose |
|------------|---------|---------|
| Docker | 20.10+ | Containerization |
| Docker Compose | 1.29+ | Multi-container orchestration |
| Nginx | Alpine | Web server & reverse proxy |
| Maven | 3.9 | Backend build tool |
| npm | 10.x | Frontend package manager |

---

## 🎯 Application Features

### 1. Authentication & Security
- **JWT-based Authentication**
  - Secure token generation on login
  - Token stored in localStorage
  - Auto-logout on token expiration
  - Password encryption using BCrypt

- **Request Interceptors**
  - Automatic token injection in API requests
  - Global error handling for 401 responses

- **CORS Configuration**
  - Configured for cross-origin requests
  - Exposed Authorization headers

### 2. Dashboard Analytics
- **Statistics Cards**
  - Total policies count
  - Active policies count
  - Expired policies count
  - Pending policies count

- **Visual Analytics**
  - Bar chart: Policies by Type (Health, Life, Vehicle, Property)
  - Pie chart: Policies by Status (Active, Expired, Pending, Cancelled)

- **Real-time Data**
  - Live data fetched from backend
  - Automatic refresh on navigation

### 3. Policy Management (CRUD Operations)

#### Create Policy
- Form validation
- Required fields enforcement
- Date range validation (end date > start date)
- Auto-generated policy numbers (POL-YYYY-XXXX format)
- Success/error notifications

#### Read/List Policies
- Paginated table view (10 records per page)
- Multiple filters:
  - By status (Active/Expired/Pending/Cancelled)
  - By type (Health/Life/Vehicle/Property)
  - By holder name (search)
- Sortable columns
- Color-coded status badges:
  - 🟢 Green = Active
  - 🔴 Red = Expired
  - 🟡 Yellow = Pending
  - ⚫ Gray = Cancelled

#### Update Policy
- Pre-filled form with existing data
- Same validation as create
- Optimistic UI updates
- Success notifications

#### Delete Policy
- Soft delete (marks as CANCELLED)
- Confirmation dialog
- Maintains data integrity

### 4. Policy Data Model

```typescript
Policy {
  id: Long                    // Auto-generated
  policyNumber: String        // Unique (POL-YYYY-XXXX)
  holderName: String          // Required
  type: Enum                  // HEALTH, LIFE, VEHICLE, PROPERTY
  status: Enum                // ACTIVE, EXPIRED, PENDING, CANCELLED
  premiumAmount: BigDecimal   // Must be > 0
  startDate: LocalDate        // Required
  endDate: LocalDate          // Required (> startDate)
  createdAt: LocalDateTime    // Auto-generated
  updatedAt: LocalDateTime    // Auto-updated
}
```

### 5. User Experience

#### Design
- Professional dark theme (Slate color palette)
- Responsive layout (mobile, tablet, desktop)
- Intuitive sidebar navigation
- Breadcrumb navigation
- Loading states
- Error states

#### Notifications
- Success messages (green)
- Error messages (red)
- Auto-dismiss after 3 seconds
- Positioned top-right

#### Navigation
- Dashboard
- Policies (list view)
- Create Policy
- Edit Policy
- Logout

---

## 🔐 Security Implementation

### Backend Security
1. **Authentication**
   - BCrypt password hashing (cost factor: 10)
   - JWT tokens with HMAC-SHA256 signing
   - Token expiration: 24 hours (configurable)

2. **Authorization**
   - Filter chain protecting all /api/** endpoints
   - Public endpoints: /api/auth/**, /api/actuator/**
   - Role-based user object

3. **Data Protection**
   - SQL injection prevention (JPA/PreparedStatements)
   - No sensitive data in logs
   - Environment variable-based configuration

### Frontend Security
1. **Token Management**
   - Stored in localStorage (with expiration check)
   - Cleared on logout
   - Auto-removed on 401 response

2. **Input Validation**
   - Client-side validation on all forms
   - Server-side validation with Bean Validation

---

## 📡 API Endpoints

### Authentication
```
POST /api/auth/login
Request:  { username: string, password: string }
Response: { token: string, username: string, role: string }
```

### Policies
```
GET    /api/policies?page=0&size=10&status=ACTIVE&type=HEALTH&search=John
GET    /api/policies/{id}
POST   /api/policies
PUT    /api/policies/{id}
DELETE /api/policies/{id}
GET    /api/policies/stats
```

### Health Check
```
GET /api/actuator/health
Response: { status: "UP", components: {...} }
```

---

## 🐳 Docker Architecture

### Multi-Stage Builds

#### Backend Dockerfile
```dockerfile
Stage 1: Builder (maven:3.9-eclipse-temurin-17)
  - Download dependencies
  - Compile source code
  - Package JAR file

Stage 2: Runtime (eclipse-temurin:17-jre-alpine)
  - Copy JAR from builder
  - Run as non-root user (appuser)
  - Expose port 8080
  - Health check configured
```

#### Frontend Dockerfile
```dockerfile
Stage 1: Builder (node:20-alpine)
  - Install dependencies
  - Build production bundle
  - Optimize assets

Stage 2: Runtime (nginx:alpine)
  - Copy build artifacts
  - Configure nginx
  - Expose port 80
  - Health check configured
```

### Container Configuration

| Container | Port | Health Check | Restart Policy |
|-----------|------|--------------|----------------|
| MySQL | 3306 | mysqladmin ping | unless-stopped |
| Backend | 8080 | GET /actuator/health | unless-stopped |
| Frontend | 80 | wget localhost | unless-stopped |

### Volumes
- **mysql_data**: Persistent database storage

### Networks
- **insurance-network**: Bridge network for container communication

---

## ⚙️ Configuration Management

### Environment Variables

#### Backend
```yaml
DB_HOST: mysql            # Database hostname
DB_PORT: 3306             # Database port
DB_NAME: insurancedb      # Database name
DB_USER: root             # Database username
DB_PASSWORD: ********     # Database password
SERVER_PORT: 8080         # Application port
JWT_SECRET: ********      # JWT signing key
JWT_EXPIRATION: 86400000  # Token expiration (ms)
```

#### Frontend
```yaml
VITE_API_URL: http://localhost:8080  # Backend API URL
```

### Default Credentials
```
Username: admin
Password: admin123
```

---

## 📊 Data Seeding

### Automatic Seeding
- **On Application Start** (via CommandLineRunner)
  - 1 admin user with encrypted password
  - 20 sample policies with randomized:
    - Holder names (realistic names)
    - Policy types (distributed across all types)
    - Policy statuses (distributed across all statuses)
    - Premium amounts ($500 - $5000)
    - Date ranges (within past year)

---

## 🚀 Deployment

### Local Development
```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Remove volumes
docker-compose down -v
```

### Production Considerations

1. **Environment Variables**
   - Use secrets management (Kubernetes Secrets, AWS Secrets Manager)
   - Rotate JWT secrets regularly
   - Use strong database passwords

2. **Scaling**
   - Backend: Horizontal scaling with load balancer
   - Frontend: CDN distribution
   - Database: Read replicas, connection pooling

3. **Monitoring**
   - Spring Actuator metrics
   - Container health checks
   - Application logging (centralized)

4. **Security Enhancements**
   - HTTPS/TLS certificates
   - Rate limiting
   - API gateway
   - Database encryption at rest

---

## 🧪 Testing Strategy

### Current Implementation
- Manual testing via UI
- API testing via Postman/curl
- Docker container health checks

### Recommended Additions
- **Backend**
  - JUnit 5 unit tests
  - Spring Boot integration tests
  - Mockito for service layer
  - TestContainers for repository tests

- **Frontend**
  - Jest unit tests
  - React Testing Library
  - Cypress E2E tests

- **API**
  - Swagger/OpenAPI documentation
  - Contract testing

---

## 📈 Performance Optimizations

### Current Implementations
1. **Frontend**
   - Vite build optimization
   - Code splitting
   - Lazy loading
   - Gzip compression (nginx)

2. **Backend**
   - JPA query optimization
   - Pagination (10 records per page)
   - Connection pooling (HikariCP)

3. **Docker**
   - Multi-stage builds (reduced image size)
   - Layer caching
   - Alpine base images

### Recommended Improvements
- Redis caching layer
- Database indexing on commonly queried fields
- Response compression
- Frontend bundle size reduction

---

## 🔄 CI/CD Readiness

### Current State
- Dockerfiles ready for automation
- Environment variable-based configuration
- Health check endpoints

### Integration Points
- **GitHub Actions / Jenkins**
  - Build Maven artifact
  - Build Docker images
  - Run tests
  - Push to container registry

- **Kubernetes Deployment**
  - Helm charts for deployment
  - ConfigMaps for configuration
  - Secrets for sensitive data
  - Ingress for routing

---

## 📝 Future Enhancements

### Planned Features
1. **Role-Based Access Control (RBAC)**
   - Admin vs. User roles
   - Permission-based UI rendering
   - Audit logging

2. **Advanced Features**
   - Email notifications (policy expiration)
   - Document upload/storage (policy documents)
   - Policy renewal workflow
   - Premium calculation engine
   - Payment integration

3. **Reporting**
   - PDF export
   - Excel export
   - Advanced analytics
   - Custom date range reports

4. **User Management**
   - User registration
   - Password reset
   - Profile management
   - Multi-factor authentication (MFA)

5. **Internationalization**
   - Multi-language support (i18n)
   - Currency localization
   - Date format localization

---

## 🎓 Learning Outcomes

This project demonstrates proficiency in:

### Backend Development
- RESTful API design
- Spring Boot ecosystem
- JPA/Hibernate ORM
- JWT authentication
- Security best practices

### Frontend Development
- Modern React (Hooks, Context)
- State management
- Responsive design
- Data visualization
- HTTP client configuration

### DevOps
- Docker containerization
- Multi-stage builds
- Docker Compose orchestration
- Environment configuration
- Health monitoring

### Architecture
- Microservices principles
- Separation of concerns
- Layered architecture
- API-first design

---

## 📞 Support & Maintenance

### Logs
```bash
# View all logs
docker-compose logs

# View specific service
docker-compose logs backend
docker-compose logs frontend
docker-compose logs mysql

# Follow logs in real-time
docker-compose logs -f backend
```

### Troubleshooting
- Backend not starting: Check MySQL connection
- Frontend API errors: Verify VITE_API_URL configuration
- Port conflicts: Modify ports in docker-compose.yml
- Database reset: `docker-compose down -v`

---

## 📄 License & Copyright

This is a portfolio project created to demonstrate full-stack development and DevOps capabilities for a Senior DevOps Engineer position.

**Created**: March 2026  
**Status**: Production-ready  
**Purpose**: Portfolio demonstration

---

## ✅ Quality Checklist

- [x] Clean, production-ready code
- [x] No TODO comments or debug logs
- [x] Comprehensive error handling
- [x] Security best practices implemented
- [x] Environment variable configuration
- [x] Docker multi-stage builds
- [x] Health check endpoints
- [x] Responsive UI design
- [x] Realistic seed data
- [x] Professional documentation

---

**Version**: 1.0.0  
**Last Updated**: March 1, 2026
