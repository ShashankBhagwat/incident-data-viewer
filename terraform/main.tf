terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# ------------------------
# IAM Role for Lambda
# ------------------------
resource "aws_iam_role" "lambda_exec" {
  name = "lambda-incident-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Principal = { Service = "lambda.amazonaws.com" }
      Action    = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_role_policy_attachment" "lambda_basic" {
  role       = aws_iam_role.lambda_exec.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

# ------------------------
# Lightsail MySQL Database (RELATIONAL)
# ------------------------
resource "aws_lightsail_database" "incident_db" {
  relational_database_name = "incident-db"
  availability_zone        = "ap-south-1a"

  blueprint_id = "mysql_8_0"
  bundle_id    = "micro_1_0"

  master_username = var.db_username
  master_password = var.db_password

  master_database_name = "incidentdb"
}

# ------------------------
# Lambda Function
# ------------------------
resource "aws_lambda_function" "incident_lambda" {
  function_name = "incident-data-viewer"

  runtime = "java17"
  handler = "com.example.demo.UnifiedLambdaHandler::handleRequest"

  role    = aws_iam_role.lambda_exec.arn

  filename         = var.lambda_jar_path
  source_code_hash = filebase64sha256(var.lambda_jar_path)

  timeout      = 30
  memory_size = 1024

  depends_on = [
    aws_lightsail_database.incident_db
  ]

  environment {
    variables = {
      SPRING_DATASOURCE_URL      = "jdbc:mysql://${aws_lightsail_database.incident_db.master_endpoint_address}:${aws_lightsail_database.incident_db.master_endpoint_port}/incidentdb"
      SPRING_DATASOURCE_USERNAME = var.db_username
      SPRING_DATASOURCE_PASSWORD = var.db_password
    }
  }
}

# ------------------------
# Lambda Function URL (Public UI)
# ------------------------
resource "aws_lambda_function_url" "ui_url" {
  function_name      = aws_lambda_function.incident_lambda.function_name
  authorization_type = "NONE"
}

resource "aws_iam_user_policy" "allow_lightsail" {
  name = "allow-lightsail"
  user = "kk_labs_user_720707"

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "lightsail:*"
        ],
        Resource = "*"
      }
    ]
  })
}

