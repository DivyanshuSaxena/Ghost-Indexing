// Q4. Popular topics in a country
/*
  :param {
    tagClass: 'MusicalArtist',
    country: 'Burma'
  }
*/
MATCH
  (root:index)-[r1:index_edge]->(leaf_node),
  (leaf_node)-[r2:index_data_edge]->(n)
WHERE
  root.name = 'TagClass_name_B_20' AND
  (r1.min < $tagClass AND r1.max > $tagClass) AND
  (r2.val = $tagClass)
WITH n AS tagClass
MATCH
  (tagClass)<-[:HAS_TYPE]-(:Tag)<-[:HAS_TAG]-
  (post:Post)<-[:CONTAINER_OF]-(forum:Forum)-[:HAS_MODERATOR]->
  (person:Person)-[:IS_LOCATED_IN]->(:City)-[:IS_PART_OF]->(:Country {name: $country})
RETURN
  forum.id,
  forum.title,
  forum.creationDate,
  person.id,
  count(DISTINCT post) AS postCount
ORDER BY
  postCount DESC,
  forum.id ASC
LIMIT 20
