###########################################################
#           ECS Service Role & Policy                     #
###########################################################
resource "aws_iam_role" "ecs_service_role" {

  name               = "AuthAPIECSServiceRole"
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "ecs.amazonaws.com"
      },
      "Effect": "Allow"
    }
  ]
}
EOF
}

resource "aws_iam_policy" "ecs_service_policy" {
  name        = "AuthAPIECSServiceRolePolicy"
  description = "Policy to access ECR, EC2 and Logs"
  path        = "/"
  policy      = data.template_file.ecs_service_policy_template.rendered
}

resource "aws_iam_role_policy_attachment" "ecs_service_role_att" {
  policy_arn = aws_iam_policy.ecs_service_policy.arn
  role       = aws_iam_role.ecs_service_role.name
}


###########################################################
#           ECS Task Role & Policy                        #
###########################################################
resource "aws_iam_role" "ecs_task_execution_role" {

  name               = "AuthAPIECSTaskExecutionRole"
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "ecs-tasks.amazonaws.com"
      },
      "Effect": "Allow"
    }
  ]
}
EOF
}

resource "aws_iam_policy" "ecs_task_execution_role_policy" {
  name        = "AuthAPIECSTaskExecutionRolePolicy"
  description = "Policy to access ECR and Logs"
  path        = "/"
  policy      = data.template_file.ecs_task_policy_template.rendered
}

resource "aws_iam_role_policy_attachment" "ecs_task_policy_role_att" {
  policy_arn = aws_iam_policy.ecs_task_execution_role_policy.arn
  role       = aws_iam_role.ecs_task_execution_role.name
}