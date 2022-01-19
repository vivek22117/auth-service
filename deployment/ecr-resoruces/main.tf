####################################################
#        Auth API Module Implementation            #
####################################################
module "vpc-es-cluster" {
  source = "../../api-aws-tf-modules/module.ecr-infra"

  default_region = ""
  enabled = false
  environment = ""
  isMonitoring = false
  max_image_count = 0
  owner = ""
  repo_name = ""
  team = ""
}