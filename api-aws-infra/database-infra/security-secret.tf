#################################################
#           Database Security Group             #
#################################################
resource "aws_security_group" "auth_service_db_sg" {
  name = var.sg_name

  description = "Allow traffic for auth-service security group"
  vpc_id      = data.terraform_remote_state.vpc.outputs.vpc_id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = [data.terraform_remote_state.vpc.outputs.bastion_sg]
  }

  ingress {
    from_port       = 22
    to_port         = 22
    protocol        = "tcp"
    security_groups = [data.terraform_remote_state.vpc.outputs.bastion_sg]
  }

  tags = local.common_tags
}


resource "aws_secretsmanager_secret" "auth_service_secrets" {
  name        = "auth-service/client/credentials/${var.secret_version}"
  description = "Auth-Service DB credentials"
}

resource "aws_secretsmanager_secret_version" "auth_service_cred" {
  secret_id     = aws_secretsmanager_secret.auth_service_secrets.id
  secret_string = jsonencode({ "username" = var.username, "password" = var.password })
}