drop table test;

create table test
(
    id int not null default unordered_unique_rowid(),
    a  varchar(64),
    b  varchar(64),
    c  int,

    primary key (id),

    family f1 (id,a,b),
    family f2 (c)
);

delete from test where true;
insert into test (id, a, b, c) values (1, 'A', 'B', 0);
insert into test (id, a, b, c) values (2, 'A', 'B', 0);
insert into test (id, a, b, c) values (3, 'A', 'B', 0);

begin; -- T1
begin; -- T2

select id,c from test where id = 1; -- T1
select id,b from test where id = 1; -- T2

update test set c=12 where id = 1; -- T1
update test set b='TRANSIT' where id = 1; --T2

commit; -- T1
commit; -- T2

-- ##############

begin; --T1
begin; --T2
select id,order_status from purchase_order1 where id=1; --T1
-- 1,PAID
select id,total_price from purchase_order1 where id=1; --T2
-- 1,10.00
update purchase_order1 set order_status = 'CONFIRMED' where id = 1; -- T1
-- 1
update purchase_order1 set total_price = total_price + 5 where id = 1; -- T2
-- block
commit; -- T1
-- T2 ERROR: restart transaction: TransactionRetryWithProtoRefreshError: WriteTooOldError:

-- ##############
begin; --T1
begin; --T2
select id,order_status from purchase_order2 where id=1; --T1
-- 1,PAID
select id,total_price from purchase_order2 where id=1; --T2
-- 1,10.00
update purchase_order2 set order_status = 'CONFIRMED' where id = 1; -- T1
-- 1
update purchase_order2 set total_price = total_price + 5 where id = 1; -- T2
-- 1
commit; -- T1
commit; -- T2

