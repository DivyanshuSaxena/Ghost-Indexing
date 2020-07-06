#!/bin/bash

if (( $# != 5 )); then
    echo 'Usage: ./loadit.sh <source> <dest> <path_to_gremlin.sh> <dataset_size> <whether_loading_in_distributed_setting>\n';
    exit -1;
fi

date;

echo "$1 $2"
mkdir -p $2
sort -t'|' -k2 -n $1/place_isPartOf_place_0_0.csv > $2/place_isPartOf_place_0_0.csv  
sort -t'|' -k1 -n $1/person_workAt_organisation_0_0.csv > $2/person_workAt_organisation_0_0.csv 
sort -t'|' -k1 -n $1/person_studyAt_organisation_0_0.csv > $2/person_studyAt_organisation_0_0.csv 
sort -t'|' -k1 -n $1/person_knows_person_0_0.csv > $2/person_knows_person_0_0.csv
sort -t'|' -k2 -n $1/comment_hasCreator_person_0_0.csv > $2/comment_hasCreator_person_0_0.csv 
sort -t'|' -k1 -n $1/person_likes_comment_0_0.csv > $2/person_likes_comment_0_0.csv 
sort -t'|' -k1 -n $1/person_likes_post_0_0.csv > $2/person_likes_post_0_0.csv 
sort -t'|' -k2 -n $1/post_hasCreator_person_0_0.csv > $2/post_hasCreator_person_0_0.csv 
sort -t'|' -k2 -n $1/comment_replyOf_comment_0_0.csv > $2/comment_replyOf_comment_0_0.csv 
sort -t'|' -k2 -n $1/comment_replyOf_post_0_0.csv > $2/comment_replyOf_post_0_0.csv 
sort -t'|' -k1 -n $1/comment_hasTag_tag_0_0.csv > $2/comment_hasTag_tag_0_0.csv 
sort -t'|' -k1 -n $1/post_hasTag_tag_0_0.csv > $2/post_hasTag_tag_0_0.csv 
sort -t'|' -k2 -n $1/forum_containerOf_post_0_0.csv > $2/forum_containerOf_post_0_0.csv 
sort -t'|' -k1 -n $1/forum_hasTag_tag_0_0.csv > $2/forum_hasTag_tag_0_0.csv 
sort -t'|' -k2 -n $1/forum_hasMember_person_0_0.csv > $2/forum_hasMember_person_0_0.csv 
sort -t'|' -k1 -n $1/person_hasInterest_tag_0_0.csv > $2/person_hasInterest_tag_0_0.csv 
sort -t'|' -k1 -n $1/tag_hasType_tagclass_0_0.csv > $2/tag_hasType_tagclass_0_0.csv 
sort -t'|' -k1 -n $1/tagclass_isSubclassOf_tagclass_0_0.csv > $2/tagclass_isSubclassOf_tagclass_0_0.csv 
sort -t'|' -k2 -n $1/person_isLocatedIn_place_0_0.csv > $2/person_isLocatedIn_place_0_0.csv
sort -t'|' -k2 -n $1/comment_isLocatedIn_place_0_0.csv > $2/comment_isLocatedIn_place_0_0.csv
sort -t'|' -k2 -n $1/post_isLocatedIn_place_0_0.csv > $2/post_isLocatedIn_place_0_0.csv
sort -t'|' -k2 -n $1/organisation_isLocatedIn_place_0_0.csv > $2/organisation_isLocatedIn_place_0_0.csv
sort -t'|' -k2 -n $1/forum_hasModerator_person_0_0.csv > $2/forum_hasModerator_person_0_0.csv

sort -t'|' -k2 -n $1/comment_isLocatedIn_place_0_0.csv > $2/comment_isLocatedIn_place_0_0.csv
sort -t'|' -k2 -n $1/forum_hasModerator_person_0_0.csv > $2/forum_hasModerator_person_0_0.csv
sort -t'|' -k2 -n $1/organisation_isLocatedIn_place_0_0.csv > $2/organisation_isLocatedIn_place_0_0.csv
sort -t'|' -k1 -n $1/person_hasInterest_tag_0_0.csv > $2/person_hasInterest_tag_0_0.csv
sort -t'|' -k2 -n $1/person_isLocatedIn_place_0_0.csv > $2/person_isLocatedIn_place_0_0.csv
sort -t'|' -k2 -n $1/post_isLocatedIn_place_0_0.csv > $2/post_isLocatedIn_place_0_0.csv 
sort -t'|' -k1 -n $1/tag_hasType_tagclass_0_0.csv > $2/tag_hasType_tagclass_0_0.csv

date;
echo 'Sorting done';
echo '============';
echo 'Schema creation commands generation'

PWD=$(pwd)
CONFIG="g = JanusGraphFactory.open('${PWD}/../GhostIndex/conf/standalone/janusgraph/janusgraph-cassandra-es.$4.properties');"
if [[ $5 -gt 0 ]]
then
    echo "DISTRIBUTED SETTING"
    CONFIG="g = JanusGraphFactory.build().set('storage.backend', 'cassandrathrift').set('storage.hostname', '10.17.5.53').set('index.search.backend', 'elasticsearch').set('index.search.hostname', '10.17.5.53:9210').open();"
fi

sed -i "s|@CONFIG|${CONFIG}|" schema_mixed_indices.gremlin
echo ":load ${PWD}/schema_mixed_indices.gremlin" | $3/gremlin.sh

echo 'Schema creation complete';
echo '========================';
echo 'Reading data and loading it';

echo ${CONFIG}
echo ${PWD}
sed -i "s|@CONFIG|${CONFIG}|" loader.gremlin
sed -i "s|@DIR|$1|" loader.gremlin
sed -i "s|@SDIR|$2|" loader.gremlin

echo ":load ${PWD}/loader.gremlin" | $3/gremlin.sh

echo 'loading done';

### CLEAN UP
sed -i "s|${CONFIG}|@CONFIG|" schema_mixed_indices.gremlin
sed -i "s|${CONFIG}|@CONFIG|" loader.gremlin
sed -i "s|$1|@DIR|" loader.gremlin
sed -i "s|$2|@SDIR|" loader.gremlin

echo "Cleanup done"
