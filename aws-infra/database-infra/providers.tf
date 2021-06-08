provider "aws" {
  region  = var.default_region // Interpolation Syntax
  profile = var.profile

  version = ">=2.22" // AWS plugin version
}

provider "template" {
  version = "2.1.2"
}

provider "null" {
  version = "2.1.2"
}

provider "random" {
  version = "2.1.2"
}

provider "archive" {
  version = "1.2.2"
}

###########################################################
# Terraform configuration block is used to define backend #
# Interpolation sytanx is not allowed in Backend          #
###########################################################
terraform {
  required_version = ">= 0.12" // Terraform version

  backend "s3" {
    profile        = "admin"
    bucket         = "doubledigit-tfstate-dev-us-east-1"
    dynamodb_table = "doubledigit-tfstate-dev-us-east-1"
    key            = "state/dev/rds/auth-service/terraform.tfstate"
    region         = "us-east-1"
    encrypt        = "true"
  }
}
