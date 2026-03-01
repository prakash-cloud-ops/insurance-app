# Insurance Policy Management System

A production-ready full-stack insurance policy management application built with React, Spring Boot, and MySQL. This system provides comprehensive policy management capabilities with a modern, responsive UI and secure JWT-based authentication.

## 🚀 Tech Stack

### Frontend
- **React 18** with Vite for fast development
- **TailwindCSS** for modern, responsive UI
- **Recharts** for data visualization
- **Axios** for API communication
- **React Router** for navigation
- **React Hot Toast** for notifications

### Backend
- **Spring Boot 3.2.0** with Java 17
- **Spring Security** with JWT authentication
- **Spring Data JPA** for database operations
- **MySQL 8** database
- **Maven** for dependency management
- **Actuator** for health checks

### DevOps
- **Docker** with multi-stage builds
- **Docker Compose** for local orchestration
- **Nginx** for frontend serving and API proxying

## 📋 Features

### Authentication
- JWT-based authentication
- Secure token storage in localStorage
- Automatic token refresh and logout on expiration

### Dashboard
- Overview statistics (Total, Active, Expired, Pending policies)
- Interactive bar chart showing policies by type
- Pie chart displaying policy status distribution
- Real-time data updates

### Policy Management
- Create, Read, Update, Delete (CRUD) operations
- Advanced filtering by status and type
- Search by holder name
- Pagination support
- Soft delete (marks policy as CANCELLED)

### Policy Features
- Unique policy number generation (POL-YYYY-XXXX format)
- Support for multiple policy types: HEALTH, LIFE, VEHICLE, PROPERTY
- Policy status tracking: ACTIVE, EXPIRED, PENDING, CANCELLED
- Premium amount tracking
- Date range validation
- Automatic timestamp tracking (createdAt, updatedAt)

## 🏗️ Project Structure

```
insurance-app/
├── backend/
│   ├── src/main/java/com/insurance/
│   │   ├── config/              # Configuration classes
│   │   ├── controller/          # REST controllers
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── model/               # JPA entities
│   │   ├── repository/          # Data repositories
│   │   ├── security/            # JWT security configuration
│   │   ├── service/             # Business logic
│   │   └── InsuranceApplication.java
│   ├── src/main/resources/
│   │   └── application.yml      # Application configuration
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── api/                 # API service layer
│   │   ├── components/          # Reusable components
│   │   ├── pages/               # Page components
│   │   ├── App.jsx              # Main app component
│   │   └── main.jsx             # Entry point
│   ├── Dockerfile
│   ├── nginx.conf              # Nginx configuration
│   ├── package.json
│   └── vite.config.js
│
├── docker-compose.yml
└── README.md
```

## 🔧 Environment Variables

### Backend

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_HOST` | localhost | MySQL database host |
| `DB_PORT` | 3306 | MySQL database port |
| `DB_NAME` | insurancedb | Database name |
| `DB_USER` | root | Database username |
| `DB_PASSWORD` | rootpassword | Database password |
| `SERVER_PORT` | 8080 | Backend server port |
| `JWT_SECRET` | (default) | JWT signing secret key |
| `JWT_EXPIRATION` | 86400000 | JWT expiration time (ms) |

### Frontend

| Variable | Default | Description |
|----------|---------|-------------|
| `VITE_API_URL` | http://localhost:8080 | Backend API URL |

## 🚀 Getting Started

### Prerequisites

- Docker 20.10+
- Docker Compose 1.29+
- (Optional) Node.js 20+ and Maven 3.9+ for local development

### Running with Docker Compose

1. **Clone the repository**
   ```bash
   cd insurance-app
   ```

2. **Start all services**
   ```bash
   docker-compose up -d
   ```

3. **Wait for services to be healthy** (approximately 60 seconds)
   ```bash
   docker-compose ps
   ```

4. **Access the application**
   - **Frontend:** http://localhost:3000
   - **Backend API:** http://localhost:8080
   - **Health Check:** http://localhost:8080/api/actuator/health

5. **Login credentials**
   - **Username:** admin
   - **Password:** admin123

### Stopping the Application

```bash
docker-compose down
```

To remove volumes (database data):
```bash
docker-compose down -v
```

## 🧪 Development

### Backend Development

```bash
cd backend
mvn spring-boot:run
```

### Frontend Development

```bash
cd frontend
npm install
npm run dev
```

## 📡 API Endpoints

### Authentication

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | /api/auth/login | User login | No |

**Login Request:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Login Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "role": "ADMIN"
}
```

### Policies

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | /api/policies | List all policies (paginated) | Yes |
| GET | /api/policies/{id} | Get policy by ID | Yes |
| POST | /api/policies | Create new policy | Yes |
| PUT | /api/policies/{id} | Update policy | Yes |
| DELETE | /api/policies/{id} | Cancel policy (soft delete) | Yes |
| GET | /api/policies/stats | Get policy statistics | Yes |

### Query Parameters for GET /api/policies

| Parameter | Type | Description |
|-----------|------|-------------|
| page | int | Page number (0-based) |
| size | int | Page size (default: 10) |
| sortBy | string | Sort field (default: id) |
| sortDir | string | Sort direction (asc/desc) |
| status | enum | Filter by status |
| type | enum | Filter by type |
| search | string | Search by holder name |

**Example:**
```
GET /api/policies?page=0&size=10&status=ACTIVE&type=HEALTH&search=John
```

### Policy Request Body

```json
{
  "holderName": "John Smith",
  "type": "HEALTH",
  "status": "ACTIVE",
  "premiumAmount": 1500.00,
  "startDate": "2026-01-01",
  "endDate": "2027-01-01"
}
```

### Health Check

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | /api/actuator/health | Application health status | No |

## 🐳 Docker Configuration

### Backend Dockerfile

- **Multi-stage build** for optimized image size
- **Builder stage:** Maven build with dependency caching
- **Runtime stage:** Lightweight JRE Alpine image
- **Non-root user** for security
- **Health check** configured
- **Exposed port:** 8080

### Frontend Dockerfile

- **Multi-stage build** for optimized image size
- **Builder stage:** Node.js build with npm ci
- **Runtime stage:** Nginx Alpine image
- **Non-root user** for security
- **Nginx** configured for React Router
- **API proxy** to backend service
- **Health check** configured
- **Exposed port:** 80

## 🔒 Security Features

- JWT token-based authentication
- Password encryption using BCrypt
- CORS configuration for cross-origin requests
- Protected API endpoints
- Request/response interceptors for token management
- Automatic logout on token expiration
- Non-root Docker containers
- Environment variable configuration

## 📊 Seed Data

The application automatically seeds:
- 1 admin user (username: admin, password: admin123)
- 20 sample policies with randomized data

## 🎨 UI Features

- **Dark professional theme** (Slate color palette)
- **Responsive design** (mobile-friendly)
- **Status badges** with color coding
- **Interactive charts** (Bar and Pie)
- **Toast notifications** for user feedback
- **Loading states** for better UX
- **Form validation** with error messages
- **Sidebar navigation** with icons
- **Protected routes** with authentication check

## 🛠️ Troubleshooting

### Backend not connecting to MySQL

```bash
docker-compose logs backend
docker-compose logs mysql
```

Check that MySQL is healthy before backend starts.

### Frontend API calls failing

Check that `VITE_API_URL` is correctly set to the backend URL.

### Port conflicts

If ports 3000, 8080, or 3307 are in use, modify `docker-compose.yml`:

```yaml
ports:
  - "3001:80"     # Frontend
  - "8081:8080"   # Backend
  - "3308:3306"   # MySQL
```

### Database persistence

Data is stored in Docker volume `mysql_data`. To reset:

```bash
docker-compose down -v
docker-compose up -d
```

## 📈 Future Enhancements

- Role-based access control (RBAC)
- Email notifications for policy expiration
- Document upload for policies
- Policy renewal workflow
- Advanced reporting and analytics
- Export to PDF/Excel
- Audit logging
- Multi-language support
- Kubernetes deployment manifests
- CI/CD pipeline configuration

## 📝 License

This project is created as a portfolio demonstration for a Senior DevOps Engineer position.

## 👤 Author

Portfolio project showcasing full-stack development and DevOps practices.

## 🙏 Acknowledgments

- Spring Boot team for excellent framework
- React team for excellent library
- TailwindCSS for utility-first CSS
- Recharts for data visualization components
