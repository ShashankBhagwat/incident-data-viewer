variable "aws_region" {
  type    = string
  default = "ap-south-1"
}

variable "lambda_jar_path" {
  type        = string
  description = "Path to the Spring Boot Lambda JAR"
}

variable "db_username" {
  type = string
}

variable "db_password" {
  type = string
}
