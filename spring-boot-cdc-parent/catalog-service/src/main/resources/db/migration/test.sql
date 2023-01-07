insert into product
(
    name,
    description,
    price,
    currency,
    sku,
    inventory,
    created_by,
    last_modified_by
)
values ('new', 'desc', 10.50,'SEK',gen_random_uuid()::string,300,'user:x','user:x') returning id;

update product set name='hello!' where id='faf8e74d-f1af-4b48-84a2-57c7f3910391';

delete from product where id='faf8e74d-f1af-4b48-84a2-57c7f3910391';