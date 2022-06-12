# auth-service

* https://aws.amazon.com/premiumsupport/knowledge-center/ecs-pull-container-api-error-ecr/#:~:text=You%20can%20receive%20this%20error,to%20pull%20or%20push%20images


### DB Setup for Bastion Host
* https://techviewleo.com/install-postgresql-13-on-amazon-linux/
    - `sudo amazon-linux-extras install epel`
    - `sudo tee /etc/yum.repos.d/pgdg.repo<<EOF
       [pgdg13]
       name=PostgreSQL 13 for RHEL/CentOS 7 - x86_64
       baseurl=http://download.postgresql.org/pub/repos/yum/13/redhat/rhel-7-x86_64
       enabled=1
       gpgcheck=0
       EOF`
    - `sudo yum install postgresql13 postgresql13-server`
    - `sudo /usr/pgsql-13/bin/postgresql-13-setup initdb`
    - `sudo systemctl enable --now postgresql-13`
    - `systemctl status postgresql-13`
    
* To login and basic commands
    - `psql --host=auth-service-cluster.cluster-cfzxwuqlh5qa.us-east-1.rds.amazonaws.com --port=5432 --username=doubledigit --password --dbname=auth_service`
    - `\l`
    - `\dt`
    - `INSERT INTO role (id, name, description) VALUES (1, 'ADMIN', 'Administration');`
