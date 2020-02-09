// Q12. Trending Posts
/*
  :param {
    date: 20110721220000000,
    likeThreshold: 400
  }
*/
:param max=>20150101010101000;
MATCH (n:super_index) WITH n AS super 
MATCH (super)-[r:super_index_edge]-(n:index) WHERE n.name = 'Post_creationDate_BP_2000' WITH n as root 
MATCH (root)-[r:index_edge]->(n) WHERE NOT (r.min > $max OR r.max < $date) WITH n as leaf_nodes
MATCH (leaf_nodes)-[r:index_data_edge]->(n) WHERE (r.val < $max AND r.val >= $date) WITH n as message
WITH message
MATCH
  (message:Post)-[:HAS_CREATOR]->(creator:Person),
  (message)<-[like:LIKES]-(:Person)
WITH message, creator, count(like) AS likeCount
WHERE likeCount > $likeThreshold
RETURN
  message.id,
  message.creationDate,
  creator.firstName,
  creator.lastName,
  likeCount
ORDER BY
  likeCount DESC,
  message.id ASC
LIMIT 100
