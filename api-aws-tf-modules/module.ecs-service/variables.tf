#####=================Global Variables==============#####
variable "default_region" {
  type        = string
  description = "AWS region to deploy our resources"
}

variable "environment" {
  type        = string
  description = "Environment to be configured 'dev', 'qa', 'prod'"
}

variable "owner_team" {
  type        = string
  description = "Name of owner team"
}

variable "component_name" {
  type        = string
  description = "Component name for resources"
}


#################################
# ECS Service Variables         #
#################################
variable "service_launch_type" {
  type        = string
  description = "The launch type, can be EC2 or FARGATE"
}

variable "service_desired_count" {
  type        = number
  description = "The number of instances of the task definition to place and keep running."
}

variable "ecs_task_mode" {
  type        = string
  description = "ECS task network mode"
}

variable "default_target_group_port" {
  type        = number
  description = "Target group port for ECS Cluster"
}

variable "ecs_dns_name" {
  type        = string
  description = "dns name for config server elb"
}


####################################
# Local variables                  #
####################################
locals {
  common_tags = {
    owner       = "Vivek"
    team        = "DD-Team"
    environment = var.environment
  }
}

#####============================ECS Service Discovery Variables================================#####
variable "service_discovery" {
  description = "A service discovery block"
  type        = map(string)
  default     = {}
}

variable "service_discovery_health_check_custom_config" {
  description = "A service discovery health check custom config block"
  type        = map(string)
  default     = {}
}
