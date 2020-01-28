// Q1. Posting summary
/*
  :param { date: 20110721220000000 }
*/
:param min=>19890101010101000;
MATCH (n:super_index) WITH n AS super 
MATCH (super)-[r:super_index_edge]-(n:index) WHERE n.name = 'post_creationDate_index_bPlus_2000' WITH n as root 
MATCH (root)-[r:index_edge]->(n) WHERE NOT (r.min > $date OR r.max < $min) WITH n as leaf_nodes
MATCH (leaf_nodes)-[r:index_data_edge]->(n) WHERE (r.val < $date AND r.val >= $min) WITH n as message
WITH count(message) AS totalMessageCountInt
WITH toFloat(totalMessageCountInt) AS totalMessageCount
MATCH (message)
WHERE message.content IS NOT NULL
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
