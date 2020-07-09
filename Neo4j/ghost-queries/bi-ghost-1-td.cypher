// Q1. Posting summary
/*
  :param { date: 20110721220000000 }
*/
MATCH
  p=(root:index)-[:index_edge*1..]->(leaf_node)
WHERE
  root.name = 'Post_creationDate_BP_200'
UNWIND relationships(p) as r1
WITH r1, leaf_node
MATCH
  (leaf_node)-[r2:index_data_edge]->(n)
WHERE
  (r1.min < $date) AND (r2.val < $date) AND
  n.content IS NOT NULL
WITH n AS message
WITH
  message,
  message.creationDate/10000000000000 AS year
WITH
  year,
  CASE
    WHEN message.length <  40 THEN 0
    WHEN message.length <  80 THEN 1
    WHEN message.length < 160 THEN 2
    ELSE                           3
  END AS lengthCategory,
  count(message) AS messageCount
RETURN
  year,
  lengthCategory,
  messageCount
ORDER BY
  year DESC,
  lengthCategory ASC