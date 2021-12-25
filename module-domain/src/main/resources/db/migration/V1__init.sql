create table bookmark (
                          bookmark_id bigint not null auto_increment,
                          create_date datetime(6),
                          last_modified_date datetime(6),
                          food_id bigint,
                          user_id bigint,
                          primary key (bookmark_id)
) engine=InnoDB;

create table category (
                          category_id bigint not null auto_increment,
                          create_date datetime(6),
                          last_modified_date datetime(6),
                          name varchar(255),
                          parent_category_id bigint,
                          primary key (category_id)
) engine=InnoDB;

create table favorite (
                          favorite_id bigint not null auto_increment,
                          create_date datetime(6),
                          last_modified_date datetime(6),
                          food_id bigint,
                          user_id bigint,
                          primary key (favorite_id)
) engine=InnoDB;

create table flavor (
                        flavor_id bigint not null auto_increment,
                        create_date datetime(6),
                        last_modified_date datetime(6),
                        flavor_type varchar(255),
                        primary key (flavor_id)
) engine=InnoDB;

create table food (
                      food_id bigint not null auto_increment,
                      create_date datetime(6),
                      last_modified_date datetime(6),
                      food_status varchar(255) not null,
                      food_title varchar(255) not null,
                      number_of_likes bigint not null,
                      price integer not null,
                      report_point integer not null,
                      report_status varchar(255) not null,
                      review_msg varchar(255),
                      writer_nickname varchar(50),
                      category_id bigint,
                      writer_id bigint,
                      primary key (food_id)
) engine=InnoDB;

create table food_flavor (
                             food_flavor_id bigint not null auto_increment,
                             create_date datetime(6),
                             last_modified_date datetime(6),
                             flavor_id bigint not null,
                             food_id bigint not null,
                             primary key (food_flavor_id)
) engine=InnoDB;

create table food_tag (
                          food_tag_id bigint not null auto_increment,
                          create_date datetime(6),
                          last_modified_date datetime(6),
                          ingredient_type varchar(20) not null,
                          food_id bigint not null,
                          tag_id bigint not null,
                          primary key (food_tag_id)
) engine=InnoDB;

create table image (
                       image_id bigint not null auto_increment,
                       create_date datetime(6),
                       last_modified_date datetime(6),
                       real_filename varchar(255) not null,
                       store_filename varchar(255) not null,
                       food_id bigint,
                       primary key (image_id)
) engine=InnoDB;

create table likes (
                       like_id bigint not null auto_increment,
                       create_date datetime(6),
                       last_modified_date datetime(6),
                       food_id bigint,
                       user_id bigint,
                       primary key (like_id)
) engine=InnoDB;

create table tag (
                     tag_id bigint not null auto_increment,
                     create_date datetime(6),
                     last_modified_date datetime(6),
                     name varchar(255) not null,
                     primary key (tag_id)
) engine=InnoDB;

create table user (
                      user_id bigint not null auto_increment,
                      create_date datetime(6),
                      last_modified_date datetime(6),
                      user_grade varchar(255),
                      user_grade_point integer,
                      nickname varchar(20),
                      name varchar(80) not null,
                      oauth_id varchar(200) not null,
                      oauth_type varchar(30) not null,
                      user_report_point integer not null,
                      user_report_status varchar(255) not null,
                      primary key (user_id)
) engine=InnoDB;

create table user_flavor (
                             user_flavor_id bigint not null auto_increment,
                             flavor_id bigint,
                             user_id bigint,
                             primary key (user_flavor_id)
) engine=InnoDB;

alter table bookmark
    add constraint unique_key_user_food unique (food_id, user_id);

alter table category
    add constraint category_name_parents_id unique (name, parent_category_id);

alter table favorite
    add constraint unique_key_user_food unique (food_id, user_id);

alter table flavor
    add constraint unique_key_flavorType unique (flavor_type);

alter table image
    add constraint unique_key_storeFilename unique (store_filename);

alter table likes
    add constraint unique_key_user_food unique (user_id, food_id);

alter table tag
    add constraint unique_key_name unique (name);

alter table user
    add constraint unique_key_oauthId_oauthType unique (oauth_id, oauth_type);

alter table user
    add constraint unique_key_nickname unique (nickname);