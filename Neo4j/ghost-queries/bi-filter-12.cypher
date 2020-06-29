// Q12. Trending Posts
// Filter query
/*
  :param {
    date: 20110721220000000,
    likeThreshold: 400
  }
*/
MATCH
  (message:Post)-[:HAS_CREATOR]->(creator:Person),
  (message)<-[like:LIKES]-(:Person)
WHERE message.creationDate > $date
WITH message, creator, count(like) AS likeCount
WHERE likeCount > $likeThreshold
RETURN COUNT(message)