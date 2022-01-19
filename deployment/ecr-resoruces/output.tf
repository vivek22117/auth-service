output "ecr_repository_name" {
  value       = module.vpc-es-cluster.ecr_repository_name
  description = "ECR Registry name"
}

output "ecr_registry_id" {
  value       = module.vpc-es-cluster.ecr_registry_id
  description = "ECR Registry ID"
}

output "ecr_registry_url" {
  value       = module.vpc-es-cluster.ecr_registry_url
  description = "ECR Registry URL"
}
