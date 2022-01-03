alter table food add index idx_foods_search_order_by_ids(category_id, report_status, food_status, food_id);
alter table food add index idx_foods_search_order_by_likes(category_id, report_status, food_status, number_of_likes);
alter table food add index idx_foods_search_order_by_price(category_id, report_status, food_status, price);