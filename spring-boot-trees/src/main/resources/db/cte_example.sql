-- All
with recursive category_tree (id, name, parent_id, level, path) as
                   (select id,
                           name,
                           parent_id,
                           0,
                           concat('/', name, '/')
                    from category
                    where parent_id is null -- anchor

                    union all

                    select c.id,
                           c.name,
                           c.parent_id,
                           ct.level + 1,
                           concat(ct.path, c.name, '/')
                    from category c
                             join category_tree ct
                                  on c.parent_id = ct.id)
select *
from category_tree
order by path;

-- Subtree
with recursive category_tree (id, name, parent_id, level, path) as
                   (select id,
                           name,
                           parent_id,
                           0,
                           concat('/', name, '/')
                    from category
                    where parent_id is null
                      and name = 'Label' -- anchor

                    union all

                    select c.id,
                           c.name,
                           c.parent_id,
                           ct.level + 1,
                           concat(ct.path, c.name, '/')
                    from category c
                             join category_tree ct
                                  on c.parent_id = ct.id)
select *
from category_tree
order by path;
