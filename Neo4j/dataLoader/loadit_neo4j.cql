USING PERIODIC COMMIT 30000
LOAD CSV FROM 'file:///home/divyanshu/Projects/BTP/ldbc_snb_datagen/social_network_1000/place_0_0.csv' AS line
CREATE (:place { pl_id: toInteger(line[0]), pl_name: line[1], pl_url: line[2], pl_type: line[3]});

USING PERIODIC COMMIT 30000
LOAD CSV FROM 'file:///home/divyanshu/Projects/BTP/ldbc_snb_datagen/social_network_1000/person_0_0.csv' AS line
CREATE (:person { p_id: toInteger(line[0]), firstName: line[1], lastName: line[2], gender: line[3], birthday: line[4]});