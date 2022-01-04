insert into category(category_id, create_date, last_modified_date, name, parent_category_id)
values (1, now(), now(), "음식", null);
insert into category(category_id, create_date, last_modified_date, name, parent_category_id)
values (2, now(), now(), "음료", null);

insert into category(create_date, last_modified_date, name, parent_category_id)
values (now(), now(), "샌드위치", 1);
insert into category(create_date, last_modified_date, name, parent_category_id)
values (now(), now(), "마라탕", 1);
insert into category(create_date, last_modified_date, name, parent_category_id)
values (now(), now(), "떡볶이", 1);
insert into category(create_date, last_modified_date, name, parent_category_id)
values (now(), now(), "샐러드", 1);
insert into category(create_date, last_modified_date, name, parent_category_id)
values (now(), now(), "라면", 1);

insert into category(create_date, last_modified_date, name, parent_category_id)
values (now(), now(), "스타벅스", 2);
insert into category(create_date, last_modified_date, name, parent_category_id)
values (now(), now(), "아마스빈", 2);
insert into category(create_date, last_modified_date, name, parent_category_id)
values (now(), now(), "공차", 2);
insert into category(create_date, last_modified_date, name, parent_category_id)
values (now(), now(), "이디야", 2);
insert into category(create_date, last_modified_date, name, parent_category_id)
values (now(), now(), "칵테일", 2);
insert into category(create_date, last_modified_date, name, parent_category_id)
values (now(), now(), "기타", 2);