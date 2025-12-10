📌 Project Overview

This project is a production-ready web application built with:

Spring Boot (Java 17)

Thymeleaf for server-side UI rendering

PostgreSQL (AWS RDS) for production database

H2 for local development

Docker + ECS Fargate for containerized deployment

Application Load Balancer (ALB) for public traffic routing

The application allows users to:

Dynamically list all database tables

Select multiple tables using a dropdown with checkboxes

Fetch and render data from selected tables dynamically

Operate fully stateless (no session usage)

🏗️ Final Architecture

User Browser
↓
Application Load Balancer (ALB)
↓
ECS Fargate Service
↓
Spring Boot + Thymeleaf (Port 8080)
↓
PostgreSQL RDS

⚙️ Technology Stack

| Layer         | Technology                      |
| ------------- | ------------------------------- |
| Language      | Java 17                         |
| Framework     | Spring Boot 2.7.18              |
| UI            | Thymeleaf                       |
| Database      | PostgreSQL (RDS), H2 (Local)    |
| Container     | Docker                          |
| Orchestration | ECS Fargate                     |
| Load Balancer | Application Load Balancer (ALB) |
| Build Tool    | Maven                           |

🚀 Local Development

mvn clean package

docker build -t incident-data-viewer:local .

docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=local incident-data-viewer:local

http://localhost:8080/

http://localhost:8080/ping

![img.png](img.png)

![img_1.png](img_1.png)

![img_2.png](img_2.png)

☁️ ECS Deployment – Required Environment Variables

| Variable                     | Purpose      |
| ---------------------------- | ------------ |
| `SPRING_PROFILES_ACTIVE`     | aws          |
| `SPRING_DATASOURCE_URL`      | RDS JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | RDS Username |
| `SPRING_DATASOURCE_PASSWORD` | RDS Password |
