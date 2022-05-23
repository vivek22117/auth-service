owner_team     = "DD-Team"
default_region = "us-east-1"

component_name = "auth-api"
ecs_task_mode  = "bridge"
ecs_dns_name   = "auth-api.cloud-interview.in"

service_desired_count = 2
service_launch_type   = "EC2"

default_target_group_port = 9004

service_discovery = {
  "routing_policy" = "MULTIVALUE"
  "ttl"            = 60
  "type"           = "A"
}

service_discovery_health_check_custom_config = {
  "failure_threshold" = null
}
