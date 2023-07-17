create table users (
    user_id uuid primary key,
    github_username varchar(255) not null,
    github_access_token varchar(255) not null
);

create table repos (
    repo_id uuid primary key,
    user_id uuid references users(user_id),
    repo_name varchar(255) not null,
    clone_url varchar(255) not null,
    ssh_url varchar(255) not null
);

create table deployments (
    deployment_id uuid primary key,
    user_id uuid references users(user_id),
    repo_id uuid references repos(repo_id),
    ec2_instance_id varchar(255),
    asg_name varchar(255),
    elb_name varchar(255),
    security_group_id varchar(255) not null,
    ec2_public_ip varchar(255),
    elb_public_ip varchar(255),
    status varchar(50) not null check (status in ('Deployed', 'Deploying...', 'Not Deployed'))
);

