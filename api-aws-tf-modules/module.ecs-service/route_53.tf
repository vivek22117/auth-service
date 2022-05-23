resource "aws_route53_record" "ecs_cluster_record" {
  count = var.ecs_dns_name != "" ? 1 : 0

  zone_id = "Z029807318ZYBD0ARNFLS"
  name    = var.ecs_dns_name
  type    = "A"

  alias {
    name                   = data.terraform_remote_state.vpc.outputs.ecs-cluster-lb-domain
    zone_id                = data.terraform_remote_state.vpc.outputs.ecs-cluster-lb-zoneId
    evaluate_target_health = false
  }
}
