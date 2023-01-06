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

update product set name='update' where id='f5104052-2ee2-49bb-a436-e5eab15771c7';

delete from product where id='f5104052-2ee2-49bb-a436-e5eab15771c7';