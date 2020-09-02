
create database ticketsale;

use ticketsale;
create table `order`
(
    id              int primary key not null AUTO_INCREMENT,
    rout_number     varchar(20)     not null,
    date_of_fly     DATETIME        not null,
    status_of_order varchar(10)     not null
);
alter table `order`
    add column user_id int not null;

alter table `order`
    add column ticket_id int not null;
