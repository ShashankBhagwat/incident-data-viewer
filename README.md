🏗️ Architecture – Incident Data Viewer

This project is a serverless web-based internal data viewer built using Spring Boot, AWS Lambda, PostgreSQL RDS, and Flyway, with a Thymeleaf UI exposed via a Lambda Function URL.

The system is designed to be:

✅ Low cost

✅ On-demand (not running 24/7)

✅ Internal-tool friendly

✅ Environment-aware (Local H2 / AWS PostgreSQL)

✅ Schema-migration safe using Flyway

🔹 Component Breakdown
1. Frontend (UI Layer)

Built using Thymeleaf

Served directly from Spring Boot running inside AWS Lambda

Accessed through:

https://<lambda-id>.lambda-url.<region>.on.aws


Features:

Table selection (single, multi-select, ALL)

Dynamic column rendering

Displays multiple tables sequentially

No React, no API Gateway (kept minimal on purpose)

2. Backend (Spring Boot on AWS Lambda)

Runs using:

✅ Java 17

✅ Spring Boot 2.7.18 (Lambda-compatible)

Packaged using:

✅ Maven Shade Plugin (flat JAR for Lambda)

Responsibilities:

Serves UI

Executes JDBC queries

Handles dynamic table selection

Integrates with Flyway

Lambda is cold-started only when the URL is accessed, ensuring:

Zero idle cost

True serverless behavior

3. Database (PostgreSQL RDS)

Managed AWS RDS PostgreSQL

Stores:

employees

products

orders

Also maintains:

flyway_schema_history for migration tracking

Accessed using:

JDBC via HikariCP

4. Database Migration (Flyway)

Flyway runs automatically during application startup.

Migration order:

V0__grant_permissions.sql
V1__create_tables.sql
V2__insert_sample_data.sql


Flyway responsibilities:

Auto-create tables

Auto-insert seed data

Maintain schema version history

Prevent accidental schema drift

🔹 Environment Separation

The same JAR runs in both Local and AWS using Spring Profiles.

Environment	Profile	Database
Local	local	H2 (In-memory)
AWS Lambda	aws	PostgreSQL RDS
Local Configuration
application-local.properties → H2

AWS Configuration
application-aws.properties → PostgreSQL RDS


Activated using:

SPRING_PROFILE=aws


set as a Lambda Environment Variable.

🔹 Data Flow
✅ UI Data Request Flow
User
→ Lambda Function URL
→ Spring Boot Controller
→ DatabaseService
→ JDBC (HikariCP)
→ PostgreSQL RDS
→ Result Set
← Data Mapping
← Service Response
← Thymeleaf Rendering
← HTML UI Response

🔹 Why This Architecture Was Chosen
Requirement	Design Decision
Cheap	Lambda + RDS
No API Gateway	Lambda Function URL
No React	Thymeleaf
Dynamic schema	Flyway
On-demand	Serverless
Easy rollback	Versioned migrations
Local dev support	H2 Profile
🔹 What This Architecture Avoids (Deliberately)

To keep costs and complexity low, the following were intentionally excluded:

❌ API Gateway

❌ Cognito Authentication

❌ CloudFront CDN

❌ EC2 Servers

❌ Kubernetes

❌ CI/CD Pipelines

❌ Service Mesh

❌ Secrets Manager (can be added later)

🔹 Production Hardening (Optional – Future Enhancements)

These can be added later if required:

Lock RDS access to Lambda VPC only

Move DB credentials to AWS Secrets Manager

Add Cognito authentication

Enable audit logging

Add pagination & export

Add role-based data access

Terraform automation

✅ Final Summary

This architecture provides:

✅ Serverless UI + Backend
✅ Version-controlled DB migrations
✅ Environment separation (Local + AWS)
✅ Low cost
✅ Zero always-on servers
✅ Easy to debug
✅ Scales automatically

This is a perfect fit for internal business reporting tools with controlled usage.
