/**
+++++++++++++++++++++++++++++++++Our filter algorithm+++++++++++++++++++++++++++++++
*/

:param min: "2012-02-06T04:26:47.076+0000"
:param max: "2012-06-20T08:47:50.992+0000"

MATCH (n:super_index) WITH n AS super 
MATCH (super)-[r:super_index_edge]-(n:index) WHERE n.name = 'post_creationDate_index_bPlus_2000' WITH n as root 
MATCH (root)-[r:index_edge]->(n) WHERE NOT (r.min > $max OR r.max < $min) WITH n as leaf_nodes
MATCH (leaf_nodes)-[r:index_data_edge]->(n) WHERE (r.val <= $max AND r.val >= $min) RETURN n

/**
+++++++++++++++++++++++++++++Same filter in simple cypher+++++++++++++++++++++++++++
*/

20120206042647076
20120620084750992

198901010101010000
201107212200000000

MATCH (p:Post) WHERE p.creationDate >= 198901010101010000 AND p.creationDate <= 201107212200000000 RETURN p
MATCH (p:Post) WHERE p.creationDate <= 201107212200000000 RETURN count(p)

/**
+++++++++++++++++++++++++++++++Query 1 after our filter+++++++++++++++++++++++++++++
*/

:param max: "2011-07-21T22:00:00.000+0000"
:param min: "1989-01-01T01:01:01.000+0000"

MATCH (n:super_index) WITH n AS super 
MATCH (super)-[r:super_index_edge]-(n:index) WHERE n.name = 'post_creationDate_index_bPlus_2000' WITH n as root 
MATCH (root)-[r:index_edge]->(n) WHERE NOT (r.min > $max OR r.max < $min) WITH n as leaf_nodes
MATCH (leaf_nodes)-[r:index_data_edge]->(n) WHERE (r.val < $max AND r.val >= $min) WITH n as message
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

+++++++++++++++++++++++++++++++++Plain Query 1++++++++++++++++++++++++++++++++++++++++++

:param date: 20110721220000000

MATCH (message:Message)
WHERE message.creationDate < $date
WITH count(message) AS totalMessageCountInt // this should be a subquery once Cypher supports it
WITH toFloat(totalMessageCountInt) AS totalMessageCount
MATCH (message:Message)
WHERE message.creationDate < $date
  AND message.content IS NOT NULL
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

++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
