// Q12. Trending Posts
/*
  :param {
    date: 20110721220000000,
    likeThreshold: 400
  }
*/
MATCH
  (root:index)-[r1:index_edge]->(leaf_node),
  (leaf_node)-[r2:index_data_edge]->(n)
WHERE
  root.name = 'Post_creationDate_BP_2000' AND
  (r2.val >= $date) AND
  (r1.max > $date)
WITH n as message
MATCH
  (message)<-[like:LIKES]-(:Person)
WITH message, count(like) AS likeCount
WHERE likeCount > $likeThreshold
MATCH
  (message)-[:HAS_CREATOR]->(creator:Person)
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