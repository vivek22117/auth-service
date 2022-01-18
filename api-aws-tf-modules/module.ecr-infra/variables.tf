######################################################################
# Global variables for VPC, Subnet, Routes and Bastion Host          #
######################################################################
variable "default_region" {
  type        = string
  description = "AWS region to deploy resources"
}


#################################
# ECR Variables                 #
#################################
variable "enabled" {
  type        = bool
  description = "True will allow to create ECR"
}

variable "repo_name" {
  type        = string
  description = "ECR repository name"
}

variable "max_image_count" {
  type        = number
  description = "Total number of images allowed in ECR"
}


######################################################
# Local variables defined                            #
######################################################
variable "team" {
  type        = string
  description = "Owner team for this application infrastructure"
}

variable "owner" {
  type        = string
  description = "Owner of the product"
}

variable "environment" {
  type        = string
  description = "Environment to be configured 'dev', 'qa', 'prod'"
}

variable "isMonitoring" {
  type        = bool
  description = "Monitoring is enabled or disabled for the resources creating"
}

locals {
  common_tags = {
    owner       = var.owner
    team        = var.team
    environment = var.environment
    monitoring  = var.isMonitoring
    Project     = "Auth-API"
  }
}