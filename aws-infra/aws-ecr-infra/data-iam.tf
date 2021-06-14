data "aws_iam_policy_document" "ecr_access_policy" {
  depends_on = [data.aws_caller_identity.current]

  count = var.enabled ? 1 : 0

  statement {
    sid    = "AllowPushPull"
    effect = "Allow"

    principals {
      type = "AWS"

      identifiers = ["*"]
    }

    actions = [
      "ecr:GetDownloadUrlForLayer",
      "ecr:PutImage",
      "ecr:InitiateLayerUpload",
      "ecr:UploadLayerPart",
      "ecr:CompleteLayerUpload",
      "ecr:DescribeRepositories",
      "ecr:GetRepositoryPolicy",
      "ecr:ListImages",
      "ecr:DeleteRepository",
      "ecr:BatchDeleteImage",
      "ecr:SetRepositoryPolicy",
      "ecr:DeleteRepositoryPolicy",
      "ecr:GetAuthorizationToken",
      "ecr:BatchCheckLayerAvailability",
      "ecr:BatchGetImage"
    ]
  }
}

data "aws_caller_identity" "current" {}

locals {
  common_arns = {
    user_arn = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:role/JenkinsSlavesRole"
    role_arn = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:user/doubledigit"
  }
}