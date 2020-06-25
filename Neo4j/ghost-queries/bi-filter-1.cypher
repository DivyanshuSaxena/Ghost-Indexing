// Q1. Posting sumary
// Filter query
/*
  :param { date: 20110721220000000 }
*/
MATCH (message:Post)
WHERE message.creationDate < $date
WITH count(message) AS totalMessageCountInt // this should be a subquery once Cypher supports it
WITH toFloat(totalMessageCountInt) AS totalMessageCount
MATCH (message:Post)
WHERE message.creationDate < $date
  AND message.content IS NOT NULL
RETURN COUNT(message)