USING PERIODIC COMMIT 30000
LOAD CSV FROM 'file:///home/divyanshu/Projects/BTP/ldbc_snb_datagen/social_network_1000/place_0_0.csv' AS line
CREATE (:place { pl_id: toInteger(line[0]), pl_name: line[1], pl_url: line[2], pl_type: line[3]});

USING PERIODIC COMMIT 30000
LOAD CSV FROM 'file:///home/divyanshu/Projects/BTP/ldbc_snb_datagen/social_network_1000/person_0_0.csv' AS line
CREATE (:person { p_id: toInteger(line[0]), firstName: line[1], lastName: line[2], gender: line[3], birthday: toInteger(line[4]), p_creationDate: toInteger(line[5]), p_locationIP: line[6], p_browserUsed: line[7]});

USING PERIODIC COMMIT 30000
LOAD CSV FROM 'file:///home/divyanshu/Projects/BTP/ldbc_snb_datagen/social_network_1000/organisation_0_0.csv' AS line
CREATE (:organization { o_id: toInteger(line[0]), o_type: line[1], pl_name: line[2], o_url: line[3]});

USING PERIODIC COMMIT 30000
LOAD CSV FROM 'file:///home/divyanshu/Projects/BTP/ldbc_snb_datagen/social_network_1000/post_0_0.csv' AS line
CREATE (:post { po_id: toInteger(line[0]), imageFile: line[1], po_creationDate: toInteger(line[2]), po_locationIP: line[3], po_browserUsed: line[4], language: line[5], po_content: line[6], po_length: toInteger(line[7])});

USING PERIODIC COMMIT 30000
LOAD CSV FROM 'file:///home/divyanshu/Projects/BTP/ldbc_snb_datagen/social_network_1000/comment_0_0.csv' AS line
CREATE (:comment { c_id: toInteger(line[0]), c_creationDate: toInteger(line[1]), po_locationIP: line[2], po_browserUsed: line[3], c_content: line[4], c_length: toInteger(line[5])});

USING PERIODIC COMMIT 30000
LOAD CSV FROM 'file:///home/divyanshu/Projects/BTP/ldbc_snb_datagen/social_network_1000/forum_0_0.csv' AS line
CREATE (:forum { f_id: toInteger(line[0]), title: line[1], f_creationDate: toInteger(line[2])});

USING PERIODIC COMMIT 30000
LOAD CSV FROM 'file:///home/divyanshu/Projects/BTP/ldbc_snb_datagen/social_network_1000/tag_0_0.csv' AS line
CREATE (:tag { t_id: toInteger(line[0]), t_name: line[1], t_url: line[2]});

USING PERIODIC COMMIT 30000
LOAD CSV FROM 'file:///home/divyanshu/Projects/BTP/ldbc_snb_datagen/social_network_1000/tagclass_0_0.csv' AS line
CREATE (:tagclass { tc_id: toInteger(line[0]), tc_name: line[1], tc_url: line[2]});

USING PERIODIC COMMIT 30000
LOAD CSV FROM 'file:///home/divyanshu/Projects/BTP/ldbc_snb_datagen/social_network_1000/tagclass_0_0.csv' AS line
