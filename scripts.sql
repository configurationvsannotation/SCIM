drop table user_groups;
drop table groups;
drop table users;

create table users (
  id INT NOT NULL AUTO_INCREMENT,
  id_str VARCHAR(100) NOT NULL,
  userName VARCHAR(100) NOT NULL,
  firstName VARCHAR(100) NOT NULL,
  lastName VARCHAR(100) NOT NULL,
  active BIT NOT NULL DEFAULT 1,
  primary key(id),
  constraint userName_unique UNIQUE (userName),
  constraint id_unique UNIQUE (id_str)
);

create table groups (
  id INT NOT NULL AUTO_INCREMENT,
  id_str VARCHAR(100) NOT NULL,
  displayName VARCHAR(100) NOT NULL,
  constraint id_unique UNIQUE (id_str),
  primary key(id)
);

create table user_groups (
  user_id INT NOT NULL,
  group_id INT NOT NULL,
  constraint fk_user foreign key (user_id) references users(id),
  constraint fk_group foreign key (group_id) references groups(id),
  constraint userGroup_unique Unique(user_id,group_id)
);

insert into users(id_str,userName,firstName,lastName,active)values('efd2b16d-68e1-4dff-9c28-8e006a2c579a','tsage','Tommy','Sage',1);
insert into users(id_str,userName,firstName,lastName,active)values('6f6d16aa-3203-4532-8bfb-3d69cf8176c4','esage','Emma','Sage',1);
insert into users(id_str,userName,firstName,lastName,active)values('85ccd046-e032-4b82-8056-85b2c052be33','csage','Clara','Sage',1);
insert into users(id_str,userName,firstName,lastName,active)values('46d7481a-fb48-4654-885a-dada44c58abb','msage','Mark','Sage',1);
insert into users(id_str,userName,firstName,lastName,active)values('88dbac2b-1818-4962-be62-487afbff327f','aquayle','Alice','Quayle',1);
insert into users(id_str,userName,firstName,lastName,active)values('2f1c2d1f-8d7e-4361-8134-af011d25686c','tquayle','Tom','Quayle',1);
insert into users(id_str,userName,firstName,lastName,active)values('5621e846-2f0c-4b11-b960-93ccec567b49','dquayle','Dirk','Quayle',1);
insert into users(id_str,userName,firstName,lastName,active)values('f5755049-49a3-4b18-9c97-d4ef6cbb5bbd','iquayle','Iris','Quayle',1);
insert into users(id_str,userName,firstName,lastName,active)values('763e35c9-2a72-4357-8b4a-13e940a333f2','jquayle','Julia','Quayle',1);
insert into users(id_str,userName,firstName,lastName,active)values('5f421034-6eb2-45e5-88fc-6737de3026d9','mquayle','Maddie','Quayle',1);

insert into groups(id_str, displayName) VALUES ('7fa32265-5016-41ef-bc30-486aba9065e6','Family');
insert into groups(id_str, displayName) VALUES ('5f421034-6eb2-45e5-88fc-6737de3026d9','Thrillseekers');

insert into user_groups(user_id, group_id)VALUES (1,1);
insert into user_groups(user_id, group_id)VALUES (2,1);
insert into user_groups(user_id, group_id)VALUES (3,1);
insert into user_groups(user_id, group_id)VALUES (4,1);
insert into user_groups(user_id, group_id)VALUES (5,1);
insert into user_groups(user_id, group_id)VALUES (6,1);
insert into user_groups(user_id, group_id)VALUES (7,1);
insert into user_groups(user_id, group_id)VALUES (8,1);
insert into user_groups(user_id, group_id)VALUES (9,1);
insert into user_groups(user_id, group_id)VALUES (10,1);

insert into user_groups(user_id, group_id)VALUES (1,2);
insert into user_groups(user_id, group_id)VALUES (4,2);
