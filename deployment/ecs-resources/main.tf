####################################################
#        Auth API ECS Module Implementation        #
####################################################
module "auth_api_ecs_module" {
  source = "../../api-aws-tf-modules/module.ecs-service"


  component_name = var.component_name
  owner_team = var.owner_team
  environment = var.environment
  default_region = var.default_region

  default_target_group_port = var.default_target_group_port
  ecs_task_mode = var.ecs_task_mode
  service_desired_count = var.service_desired_count
  service_launch_type = var.service_launch_type
}