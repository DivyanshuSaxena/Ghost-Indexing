// Q18. How many persons have a given number of posts
/*
  :param {
    date: 20110722000000000,
    lengthThreshold: 20,
    languages: ['ar']
  }
*/
:param max=>20150101010101000;
MATCH (n:super_index) WITH n AS super 
MATCH (super)-[r:super_index_edge]-(n:index) WHERE n.name = 'post_creationDate_index_bPlus_2000' WITH n as root 
MATCH (root)-[r:index_edge]->(n) WHERE NOT (r.min > $max OR r.max < $date) WITH n as leaf_nodes
MATCH (leaf_nodes)-[r:index_data_edge]->(n) WHERE (r.val < $max AND r.val >= $min) WITH n as message
MATCH (person:Person)
OPTIONAL MATCH (person)<-[:HAS_CREATOR]-(message)-[:REPLY_OF*0..]->(post:Post)
WHERE message.content IS NOT NULL
  AND message.length < $lengthThreshold
  AND post.language IN $languages
WITH
  person,
  count(message) AS messageCount
RETURN
  messageCount,
  count(person) AS personCount
ORDER BY
  personCount DESC,
  messageCount DESC
