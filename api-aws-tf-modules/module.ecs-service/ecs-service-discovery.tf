locals {
  dns_routing_policy = lookup(var.service_discovery, "routing_policy", "MULTIVALUE")
  dns_ttl            = lookup(var.service_discovery, "ttl", 60)
  dns_type           = lookup(var.service_discovery, "type", "SRV")
  namespace_name     = lookup(var.service_discovery, "name", var.component_name)
}

resource "aws_service_discovery_private_dns_namespace" "auth_api_pvt_dns_ns" {
  name        = "doubledigit-auth-service"
  description = "Domain for all the services"
  vpc         = data.terraform_remote_state.vpc.outputs.vpc_id
}

resource "aws_service_discovery_service" "auth_api_sd" {
  name = local.namespace_name

  dns_config {
    namespace_id = aws_service_discovery_private_dns_namespace.auth_api_pvt_dns_ns.id

    dns_records {
      ttl  = local.dns_ttl
      type = local.dns_type
    }

    routing_policy = local.dns_routing_policy
  }

  dynamic "health_check_custom_config" {
    for_each = [var.service_discovery_health_check_custom_config]
    content {
      failure_threshold = lookup(health_check_custom_config.value, "failure_threshold", null)
    }
  }
}

