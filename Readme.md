# BankPro - Banking Application

A Spring Boot-based banking application with Account management REST API, containerized with Docker and deployed using CI/CD pipeline.

## Features

- RESTful API for Account management (CRUD operations)
- OpenAPI/Swagger documentation
- Docker containerization
- Jenkins CI/CD pipeline
- Ansible multi-cloud deployment (AWS, Azure)
- In-memory data storage

## Tech Stack

- **Framework**: Spring Boot 3.1.5
- **Java**: 17
- **Build Tool**: Maven
- **Containerization**: Docker
- **CI/CD**: Jenkins
- **Deployment**: Ansible
- **Documentation**: OpenAPI/Swagger

## Prerequisites

- Java 17+
- Maven 3.9+
- Docker
- Jenkins (for CI/CD)
- Ansible (for deployment)

## Building the Application

```bash
mvn clean package
```

## Running Locally

```bash
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## API Endpoints

- `GET /api/accounts` - List all accounts
- `GET /api/accounts/{id}` - Get account by ID
- `POST /api/accounts` - Create new account
- `PUT /api/accounts/{id}` - Update account
- `DELETE /api/accounts/{id}` - Delete account

## API Documentation

Once the application is running, access Swagger UI at:

- `http://localhost:8080/swagger-ui.html`

## Docker

### Build Image

```bash
docker build -t bankpro-core:latest .
```

### Run Container

```bash
docker run -p 8080:8080 bankpro-core:latest
```

## CI/CD Pipeline

The Jenkins pipeline includes:

1. Code checkout
2. Build & test
3. Publish test results
4. Build Docker image
5. Push to DockerHub
6. Deploy via Ansible

## Deployment

### Using Ansible

1. Configure inventory in `ansible/inventory/hosts`
2. Run deployment:

```bash
ansible-playbook -i ansible/inventory/hosts ansible/deploy.yml
```

### Environment Variables

- `JAVA_OPTS`: JVM options (default: `-Xms256m -Xmx512m`)
- `published_port`: Container port (default: `8080`)
- `host_port`: Host port mapping (default: `8080`)

## Project Structure

```
bank-pro/
├── src/
│   ├── main/java/
│   │   ├── controller/     # REST controllers
│   │   ├── model/          # Domain models
│   │   ├── repository/     # Data repositories
│   │   └── service/        # Business logic
│   └── test/              # Test files
├── ansible/               # Ansible playbooks
├── Dockerfile            # Docker configuration
├── Jenkinsfile          # CI/CD pipeline
└── pom.xml              # Maven configuration
```

## Testing

Run tests:

```bash
mvn test
```
