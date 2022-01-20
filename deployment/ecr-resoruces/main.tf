####################################################
#        Auth API Module Implementation            #
####################################################
module "vpc-es-cluster" {
  source = "../../api-aws-tf-modules/module.ecr-infra"

  default_region = var.default_region
  team = var.team
  environment = var.environment
  isMonitoring = var.isMonitoring
  owner = var.owner

  enabled = var.enabled

  max_image_count = var.max_image_count
  repo_name = var.repo_name
}