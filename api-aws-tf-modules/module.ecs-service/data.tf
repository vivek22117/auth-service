###################################################
# Fetch remote state for S3 deployment bucket     #
###################################################
data "terraform_remote_state" "vpc" {
  backend = "s3"

  config = {
    bucket = "${var.environment}-tfstate-${data.aws_caller_identity.current.account_id}-${var.default_region}"
    key    = "state/${var.environment}/vpc/terraform.tfstate"
    region = var.default_region
  }
}

data "terraform_remote_state" "ecs_cluster" {
  backend = "s3"

  config = {
    bucket = "${var.environment}-tfstate-${data.aws_caller_identity.current.account_id}-${var.default_region}"
    key    = "state/${var.environment}/ec2-ecs-cluster/terraform.tfstate"
    region = var.default_region
  }
}

data "terraform_remote_state" "auth_api_ecr_state" {
  backend = "s3"

  config = {
    bucket = "${var.environment}-tfstate-${data.aws_caller_identity.current.account_id}-${var.default_region}"
    key    = "state/${var.environment}/ecr-repo/auth-api/terraform.tfstate"
    region = var.default_region
  }
}

data "template_file" "ecs_service_policy_template" {
  template = file("${path.module}/policy-doc/ecs-service-policy.json")
}

data "template_file" "ecs_task_policy_template" {
  template = file("${path.module}/policy-doc/ecs-task-policy.json")
}

data "template_file" "auth_service_task" {
  template = file("${path.module}/tasks/auth-service-api-task.json")

  vars = {
    auth_api_image = data.terraform_remote_state.auth_api_ecr_state.outputs.ecr_registry_url
    log_group      = data.terraform_remote_state.ecs_cluster.outputs.ecs-cluster-log-group
    aws_region     = var.default_region
  }
}

# used for accessing Account ID and ARN
data "aws_caller_identity" "current" {}
