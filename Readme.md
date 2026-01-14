# BankPro - Banking Application

A Spring Boot-based banking application with Account management REST API, containerized with Docker and deployed using CI/CD pipeline with multi-cloud support.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Project Structure](#project-structure)
- [API Documentation](#api-documentation)
- [Building & Running](#building--running)
- [Docker](#docker)
- [CI/CD Pipeline](#cicd-pipeline)
- [Deployment](#deployment)
- [Testing](#testing)
- [Configuration](#configuration)
- [Troubleshooting](#troubleshooting)

## Overview

BankPro is a microservices-ready banking application that provides RESTful APIs for account management. The application demonstrates modern DevOps practices including:

- **Containerization**: Docker-based deployment
- **CI/CD**: Automated build, test, and deployment via Jenkins
- **Infrastructure as Code**: Ansible playbooks for multi-cloud deployment
- **API Documentation**: OpenAPI/Swagger integration
- **In-Memory Storage**: ConcurrentHashMap-based repository for development

## Features

- ✅ RESTful API for Account management (CRUD operations)
- ✅ OpenAPI/Swagger documentation
- ✅ Docker containerization with multi-stage builds
- ✅ Jenkins CI/CD pipeline with automated testing
- ✅ Ansible multi-cloud deployment (AWS, Azure)
- ✅ In-memory data storage with thread-safe operations
- ✅ Pre-seeded test data
- ✅ Comprehensive test coverage

## Architecture

The application follows a layered architecture:

```
┌─────────────────────────────────────┐
│      AccountController (REST)       │
│         /api/accounts               │
└─────────────────┬───────────────────┘
                  │
┌─────────────────▼───────────────────┐
│      AccountService (Business)      │
└─────────────────┬───────────────────┘
                  │
┌─────────────────▼───────────────────┐
│  InMemoryAccountRepository (Data)  │
│     ConcurrentHashMap<String,       │
│              Account>               │
└─────────────────────────────────────┘
```

### Components

- **Controller Layer**: Handles HTTP requests/responses (`AccountController`)
- **Service Layer**: Business logic (`AccountService`)
- **Repository Layer**: Data access (`InMemoryAccountRepository`)
- **Model**: Domain entities (`Account`)

## Tech Stack

- **Framework**: Spring Boot 3.1.5
- **Java**: 17 (LTS)
- **Build Tool**: Maven 3.9+
- **Containerization**: Docker
- **CI/CD**: Jenkins
- **Deployment**: Ansible
- **Documentation**: OpenAPI/Swagger (springdoc-openapi 2.1.0)
- **Testing**: JUnit, Spring Boot Test

## Prerequisites

### Required Software

- **Java 17+** (JDK)
- **Maven 3.9+**
- **Docker** (for containerization)
- **Git** (for version control)

### For CI/CD

- **Jenkins** (for CI/CD pipeline)
- **Ansible** (for deployment automation)

### For Deployment

- **AWS EC2** or **Azure VM** instance
- **SSH access** to deployment targets
- **DockerHub account** (for image registry)

## Installation & Setup

### Local Development Setup

1. **Clone the repository:**

```bash
git clone <repository-url>
cd bank-pro
```

2. **Build the application:**

```bash
mvn clean install
```

3. **Run the application:**

```bash
mvn spring-boot:run
```

4. **Access the application:**

- API Base URL: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API Docs (JSON): `http://localhost:8080/v3/api-docs`

### Setup: Ansible & Jenkins on Local Machine → Remote EC2

#### Step 1: Install Ansible on Local Machine

**For WSL (Ubuntu/Debian):**

```bash
sudo apt update
sudo apt install -y ansible
ansible --version
```

**For Windows:**

- Install via WSL or use Ansible in a Docker container

#### Step 2: Install Jenkins on Local Machine

**For WSL:**

```bash
# Install Java
sudo apt install openjdk-17-jdk

# Add Jenkins repository
curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | sudo tee /usr/share/keyrings/jenkins-keyring.asc > /dev/null
echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian-stable binary/ | sudo tee /etc/apt/sources.list.d/jenkins.list > /dev/null

# Install Jenkins
sudo apt update
sudo apt install jenkins
sudo systemctl start jenkins
sudo systemctl enable jenkins
```

Access Jenkins at `http://localhost:8080` (get initial password from `/var/lib/jenkins/secrets/initialAdminPassword`)

#### Step 3: Generate SSH Key Pair for EC2 Access

```bash
# Generate SSH key pair (if not exists)
ssh-keygen -t rsa -b 4096 -f ~/.ssh/id_rsa_ansible -N ""

# This creates:
# ~/.ssh/id_rsa_ansible (private key - keep secure)
# ~/.ssh/id_rsa_ansible.pub (public key - copy to EC2)
```

#### Step 4: Copy Public Key to EC2 Instance

**Option A: Using ssh-copy-id (if you have password access initially)**

```bash
ssh-copy-id -i ~/.ssh/id_rsa_ansible.pub ubuntu@ec2-100-31-126-34.compute-1.amazonaws.com
```

**Option B: Manual copy (if using EC2 key pair)**

```bash
# 1. Copy public key content
cat ~/.ssh/id_rsa_ansible.pub

# 2. SSH into EC2 using your EC2 key pair
ssh -i /path/to/ec2-key.pem ubuntu@ec2-100-31-126-34.compute-1.amazonaws.com

# 3. On EC2, add public key to authorized_keys
echo "YOUR_PUBLIC_KEY_CONTENT" >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys
chmod 700 ~/.ssh
```

#### Step 5: Configure Ansible Inventory

Edit `ansible/inventory/hosts`:

```ini
[aws]
ec2-100-31-126-34.compute-1.amazonaws.com ansible_user=ubuntu

[all:vars]
ansible_ssh_private_key_file=~/.ssh/id_rsa_ansible
ansible_ssh_common_args='-o StrictHostKeyChecking=no'
```

**Note:** For WSL, use `~/.ssh/id_rsa_ansible`. For Jenkins running as root, use `/root/.ssh/id_rsa_ansible`.

#### Step 6: Test Ansible Connection

```bash
# Test SSH connection first
ssh -i ~/.ssh/id_rsa_ansible ubuntu@ec2-100-31-126-34.compute-1.amazonaws.com

# Test Ansible ping
ansible all -i ansible/inventory/hosts -m ping

# Expected output:
# ec2-100-31-126-34.compute-1.amazonaws.com | SUCCESS => {
#     "changed": false,
#     "ping": "pong"
# }
```

#### Step 7: Run Ansible Playbook

```bash
# From project root
ansible-playbook -i ansible/inventory/hosts ansible/deploy.yml
```

#### Step 8: Configure Jenkins for CI/CD

1. **Install Required Plugins:**

   - Manage Jenkins → Plugins → Install:
     - Ansible Plugin
     - Docker Pipeline Plugin
     - JUnit Plugin

2. **Add Credentials in Jenkins:**

   **DockerHub Credentials:**

   - Manage Jenkins → Credentials → Add Credentials
   - Kind: Username with password
   - ID: `dockerhub-creds`
   - Username: Your DockerHub username
   - Password: Your DockerHub password/token

   **SSH Credentials:**

   - Manage Jenkins → Credentials → Add Credentials
   - Kind: SSH Username with private key
   - ID: `ansible-ssh-key`
   - Username: `ubuntu`
   - Private Key: Copy content of `~/.ssh/id_rsa_ansible`

   **Ansible User:**

   - Manage Jenkins → Credentials → Add Credentials
   - Kind: Secret text
   - ID: `ansible-user`
   - Secret: `ubuntu`

   **Ansible Host:**

   - Manage Jenkins → Credentials → Add Credentials
   - Kind: Secret text
   - ID: `ansible-host`
   - Secret: `ec2-100-31-126-34.compute-1.amazonaws.com`

3. **Create Jenkins Pipeline:**
   - New Item → Pipeline
   - Name: `bankpro-pipeline`
   - Pipeline Definition: Pipeline script from SCM
   - SCM: Git
   - Repository URL: Your repository URL
   - Script Path: `Jenkinsfile`

## Project Structure

```
bank-pro/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── BankproApplication.java      # Main Spring Boot application
│   │   │   ├── controller/
│   │   │   │   └── AccountController.java  # REST API endpoints
│   │   │   ├── model/
│   │   │   │   └── Account.java             # Domain model
│   │   │   ├── repository/
│   │   │   │   └── InMemoryAccountRepository.java  # Data layer
│   │   │   └── service/
│   │   │       └── AccountService.java      # Business logic
│   │   └── resources/
│   │       └── application.properties       # Application configuration
│   └── test/
│       └── java/
│           ├── controller/
│           │   └── AccountControllerTest.java
│           └── integration/
│               └── AccountIntegrationTest.java
├── ansible/
│   ├── deploy.yml                          # Ansible deployment playbook
│   └── inventory/
│       └── hosts                           # Ansible inventory
├── Dockerfile                              # Docker build configuration
├── Jenkinsfile                             # Jenkins CI/CD pipeline
├── pom.xml                                 # Maven project configuration
├── .dockerignore                          # Docker ignore patterns
├── .gitignore                             # Git ignore patterns
└── Readme.md                              # This file
```

## API Documentation

### Base URL

```
http://localhost:8080/api/accounts
```

### Endpoints

#### 1. List All Accounts

```http
GET /api/accounts
```

**Response:** `200 OK`

```json
[
  {
    "id": "A100",
    "owner": "Alice",
    "balance": 1200.5
  },
  {
    "id": "A200",
    "owner": "Bob",
    "balance": 380.0
  }
]
```

#### 2. Get Account by ID

```http
GET /api/accounts/{id}
```

**Parameters:**

- `id` (path): Account ID

**Response:** `200 OK`

```json
{
  "id": "A100",
  "owner": "Alice",
  "balance": 1200.5
}
```

**Error Response:** `404 Not Found` (if account doesn't exist)

#### 3. Create New Account

```http
POST /api/accounts
Content-Type: application/json
```

**Request Body:**

```json
{
  "owner": "Charlie",
  "balance": 500.0
}
```

**Response:** `201 Created`

```json
{
  "id": "generated-uuid",
  "owner": "Charlie",
  "balance": 500.0
}
```

#### 4. Update Account

```http
PUT /api/accounts/{id}
Content-Type: application/json
```

**Parameters:**

- `id` (path): Account ID

**Request Body:**

```json
{
  "owner": "Charlie Updated",
  "balance": 750.0
}
```

**Response:** `200 OK`

```json
{
  "id": "A100",
  "owner": "Charlie Updated",
  "balance": 750.0
}
```

**Error Response:** `404 Not Found` (if account doesn't exist)

#### 5. Delete Account

```http
DELETE /api/accounts/{id}
```

**Parameters:**

- `id` (path): Account ID

**Response:** `204 No Content`

### Interactive API Documentation

Access Swagger UI for interactive API testing:

- **URL:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

## Building & Running

### Build Application

```bash
# Clean and build
mvn clean package

# Build without tests
mvn clean package -DskipTests
```

### Run Locally

```bash
# Using Maven
mvn spring-boot:run

# Using JAR file
java -jar target/bankpro-core-0.0.1-SNAPSHOT.jar
```

### Run with Custom Port

```bash
java -jar -Dserver.port=9090 target/bankpro-core-0.0.1-SNAPSHOT.jar
```

## Docker

### Build Docker Image

```bash
docker build -t bankpro-core:latest .
```

### Run Container

```bash
# Basic run
docker run -p 8080:8080 bankpro-core:latest

# With custom JVM options
docker run -p 8080:8080 -e JAVA_OPTS="-Xms512m -Xmx1024m" bankpro-core:latest

# Run in detached mode
docker run -d -p 8080:8080 --name bankpro bankpro-core:latest
```

### Docker Commands

```bash
# View running containers
docker ps

# View logs
docker logs bankpro

# Stop container
docker stop bankpro

# Remove container
docker rm bankpro

# Remove image
docker rmi bankpro-core:latest
```

### Multi-Stage Build

The Dockerfile uses a multi-stage build:

1. **Builder stage**: Maven-based build environment
2. **Runtime stage**: Lightweight JDK-only image

This reduces final image size and improves security.

## CI/CD Pipeline

The Jenkins pipeline (`Jenkinsfile`) automates the following stages:

### Pipeline Stages

1. **Checkout**: Clone source code from SCM
2. **Build & Test**: Compile and run unit tests
3. **Debug**: Display workspace and test reports (for troubleshooting)
4. **Publish Test Results**: Publish JUnit test results
5. **Build Docker Image**: Create Docker image with build number tag
6. **Push Docker Image**: Push to DockerHub registry
7. **Deploy via Ansible**: Deploy to remote hosts using Ansible

### Pipeline Configuration

**Environment Variables:**

- `IMAGE_NAME`: `prem6016/bankpro-core`
- `IMAGE_TAG`: `${BUILD_NUMBER}`
- `IMAGE_FULL`: `${IMAGE_NAME}:${IMAGE_TAG}`

**Required Jenkins Credentials:**

- `dockerhub-creds`: DockerHub username/password
- `ansible-ssh-key`: SSH private key for deployment
- `ansible-user`: SSH username
- `ansible-host`: Target hostname

### Triggering Pipeline

- **Manual**: Click "Build Now" in Jenkins
- **SCM Polling**: Configure in Jenkins job settings
- **Webhook**: Configure Git webhook to trigger on push

## Deployment

### Ansible Deployment

The Ansible playbook (`ansible/deploy.yml`) performs:

1. **Docker Installation**: Installs Docker on target hosts
2. **Image Pull**: Pulls Docker image from registry
3. **Container Management**: Stops existing container and starts new one
4. **Port Mapping**: Maps host port to container port (default: 8080)

### Deploy Manually

```bash
# From project root
ansible-playbook -i ansible/inventory/hosts ansible/deploy.yml \
  --extra-vars "docker_image=prem6016/bankpro-core:latest"
```

### Deploy via Jenkins

The Jenkins pipeline automatically deploys after successful build and image push.

### Multi-Cloud Support

The inventory file supports multiple cloud providers:

```ini
[aws]
ec2-100-31-126-34.compute-1.amazonaws.com ansible_user=ubuntu

[azure]
vm-westus.cloudapp.net ansible_user=azureuser
```

Deploy to specific group:

```bash
ansible-playbook -i ansible/inventory/hosts ansible/deploy.yml --limit aws
```

## Testing

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=AccountControllerTest
```

### Run Integration Tests

```bash
mvn verify
```

### Test Coverage

```bash
# Install jacoco plugin in pom.xml first
mvn clean test jacoco:report
```

### Manual API Testing

**Using cURL:**

```bash
# List all accounts
curl http://localhost:8080/api/accounts

# Get account by ID
curl http://localhost:8080/api/accounts/A100

# Create account
curl -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -d '{"owner":"Test User","balance":1000.00}'

# Update account
curl -X PUT http://localhost:8080/api/accounts/A100 \
  -H "Content-Type: application/json" \
  -d '{"owner":"Updated User","balance":1500.00}'

# Delete account
curl -X DELETE http://localhost:8080/api/accounts/A100
```

## Configuration

### Application Properties

Default configuration (Spring Boot defaults):

- **Server Port**: `8080`
- **Context Path**: `/`
- **JVM Options**: `-Xms256m -Xmx512m`

### Environment Variables

| Variable         | Description       | Default             |
| ---------------- | ----------------- | ------------------- |
| `JAVA_OPTS`      | JVM options       | `-Xms256m -Xmx512m` |
| `SERVER_PORT`    | Application port  | `8080`              |
| `published_port` | Container port    | `8080`              |
| `host_port`      | Host port mapping | `8080`              |

### Docker Configuration

**Container Name**: `bankproAp` (configurable in `ansible/deploy.yml`)

**Restart Policy**: `always` (container auto-restarts on failure)

## Troubleshooting

### Common Issues

#### 1. Port Already in Use

**Error:** `Port 8080 is already in use`

**Solution:**

```bash
# Find process using port 8080
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# Kill process or change port
java -jar -Dserver.port=9090 target/bankpro-core-0.0.1-SNAPSHOT.jar
```

#### 2. Docker Build Fails

**Error:** `Cannot connect to Docker daemon`

**Solution:**

```bash
# Start Docker service
sudo systemctl start docker  # Linux
# Or start Docker Desktop on Windows/Mac
```

#### 3. Ansible Connection Issues

**Error:** `SSH Permission Denied`

**Solutions:**

- Verify key permissions: `chmod 600 ~/.ssh/id_rsa_ansible`
- Check public key is in EC2 `~/.ssh/authorized_keys`
- Verify EC2 security group allows SSH (port 22) from your IP
- Test SSH manually: `ssh -i ~/.ssh/id_rsa_ansible ubuntu@EC2_HOST`

#### 4. Jenkins Pipeline Fails

**Error:** `Credentials not found`

**Solution:**

- Verify all required credentials are configured in Jenkins
- Check credential IDs match those in `Jenkinsfile`

**Error:** `Ansible not found`

**Solution:**

- Install Ansible on Jenkins server
- Or use Ansible plugin in Jenkins

#### 5. Application Not Starting

**Error:** `Application failed to start`

**Solutions:**

- Check Java version: `java -version` (should be 17+)
- Verify Maven dependencies: `mvn dependency:resolve`
- Check application logs for detailed error messages

#### 6. Container Exits Immediately

**Error:** `Container exits with code 1`

**Solution:**

```bash
# Check container logs
docker logs bankproAp

# Run container interactively
docker run -it -p 8080:8080 bankpro-core:latest
```

### Debugging Tips

1. **Enable Debug Logging:**

   ```bash
   java -jar -Dlogging.level.root=DEBUG target/bankpro-core-0.0.1-SNAPSHOT.jar
   ```

2. **Test Ansible Connection:**

   ```bash
   ansible all -i ansible/inventory/hosts -m ping -v
   ```

3. **Verify Docker Image:**

   ```bash
   docker images | grep bankpro-core
   docker inspect bankpro-core:latest
   ```

4. **Check Jenkins Workspace:**
   - Navigate to Jenkins job → Workspace
   - Verify files are checked out correctly

## License

This project is part of a DevOps capstone project.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## Support

For issues and questions:

- Check the [Troubleshooting](#troubleshooting) section
- Review application logs
- Check Jenkins build logs
- Verify Ansible playbook execution

---

**Last Updated:** 2024
**Version:** 0.0.1-SNAPSHOT
