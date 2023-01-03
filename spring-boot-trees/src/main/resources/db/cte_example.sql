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


select category0_.name as col_0_0_, count(category1_.name) - 1 as col_1_0_
from category category0_
         cross join category category1_
where category0_.lft between category1_.lft and category1_.rgt
group by category0_.name, category0_.lft
order by category0_.lft;


explain SELECT node.name AS name, (COUNT(parent.name) - (sub_tree.depth + 1)) AS depth
FROM category AS node,
     category AS parent,
     category AS sub_parent,
     (SELECT node.name, (COUNT(parent.name) - 1) AS depth
      FROM category AS node,
           category AS parent
      WHERE node.lft BETWEEN parent.lft AND parent.rgt
        AND node.name = 'Label'
      GROUP BY node.name, node.lft
      ORDER BY node.lft) AS sub_tree
WHERE node.lft BETWEEN parent.lft AND parent.rgt
  AND node.lft BETWEEN sub_parent.lft AND sub_parent.rgt
  AND sub_parent.name = sub_tree.name
GROUP BY node.name, node.lft, depth
ORDER BY node.lft;

explain  with  sub_tree as (SELECT node.name, (COUNT(parent.name) - 1) AS depth
                  FROM category AS node,
                       category AS parent
                  WHERE node.lft BETWEEN parent.lft AND parent.rgt
                    AND node.name = 'Label'
                  GROUP BY node.name, node.lft
                  ORDER BY node.lft)
SELECT node.name AS name, (COUNT(parent.name) - (sub_tree.depth + 1)) AS depth
FROM category AS node,
     category AS parent,
     category AS sub_parent,
     sub_tree
WHERE node.lft BETWEEN parent.lft AND parent.rgt
  AND node.lft BETWEEN sub_parent.lft AND sub_parent.rgt
  AND sub_parent.name = sub_tree.name
GROUP BY node.name, node.lft, depth
ORDER BY node.lft;