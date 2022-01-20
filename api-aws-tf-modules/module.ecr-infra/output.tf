output "ecr_repository_name" {
  value       = aws_ecr_repository.auth_api_ecr.*.name
  description = "ECR Registry name"
}

output "ecr_registry_id" {
  value       = join("", aws_ecr_repository.auth_api_ecr.*.registry_id)
  description = "ECR Registry ID"
}

output "ecr_registry_url" {
  value       = join("", aws_ecr_repository.auth_api_ecr.*.repository_url)
  description = "ECR Registry URL"
}

output "account_id" {
  value = data.aws_caller_identity.current.account_id
}
