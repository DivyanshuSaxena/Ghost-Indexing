// Q1. Posting summary
/*
  :param { date: 20110721220000000 }
*/
:param min=>19890101010101000;
MATCH
  (root:index)-[r1:index_edge]->(leaf_node),
  (leaf_node)-[r2:index_data_edge]->(n)
WHERE
  root.name = 'Post_creationDate_BP_2000' AND
  (r1.min < $date AND r1.max > $min) AND
  (r2.val < $date AND r2.val >= $min) AND
  n.content IS NOT NULL
WITH n AS message, toFloat(count(n)) AS totalMessageCount
WITH
  totalMessageCount,
  message,
  message.creationDate/10000000000000 AS year
WITH
  totalMessageCount,
  year,
  message:Comment AS isComment,
  CASE
    WHEN message.length <  40 THEN 0
    WHEN message.length <  80 THEN 1
    WHEN message.length < 160 THEN 2
    ELSE                           3
  END AS lengthCategory,
  count(message) AS messageCount,
  floor(avg(message.length)) AS averageMessageLength,
  sum(message.length) AS sumMessageLength
RETURN
  year,
  isComment,
  lengthCategory,
  messageCount,
  averageMessageLength,
  sumMessageLength,
  messageCount / totalMessageCount AS percentageOfMessages,
  totalMessageCount
ORDER BY
  year DESC,
  isComment ASC,
  lengthCategory ASC