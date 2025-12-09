output "lambda_url" {
  value = aws_lambda_function_url.ui_url.function_url
}

output "db_endpoint" {
  value = aws_lightsail_database.incident_db.master_endpoint_address
}
