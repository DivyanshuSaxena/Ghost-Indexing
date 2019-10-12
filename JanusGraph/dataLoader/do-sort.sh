#!/bin/bash
date;
sort -t"|" -k2 -n ../social_network/place_isPartOf_place_0_0.csv > place_isPartOf_place_0_0.csv  
sort -t"|" -k1 -n ../social_network/person_workAt_organisation_0_0.csv > person_workAt_organisation_0_0.csv 
sort -t"|" -k1 -n ../social_network/person_studyAt_organisation_0_0.csv > person_studyAt_organisation_0_0.csv 
sort -t"|" -k1 -n ../social_network/person_knows_person_0_0.csv > person_knows_person_0_0.csv
sort -t"|" -k2 -n ../social_network/comment_hasCreator_person_0_0.csv > comment_hasCreator_person_0_0.csv 
sort -t"|" -k1 -n ../social_network/person_likes_comment_0_0.csv > person_likes_comment_0_0.csv 
sort -t"|" -k1 -n ../social_network/person_likes_post_0_0.csv > person_likes_post_0_0.csv 
sort -t"|" -k2 -n ../social_network/post_hasCreator_person_0_0.csv > post_hasCreator_person_0_0.csv 
sort -t"|" -k2 -n ../social_network/comment_replyOf_comment_0_0.csv > comment_replyOf_comment_0_0.csv 
sort -t"|" -k2 -n ../social_network/comment_replyOf_post_0_0.csv > comment_replyOf_post_0_0.csv 
sort -t"|" -k1 -n ../social_network/comment_hasTag_tag_0_0.csv > comment_hasTag_tag_0_0.csv 
sort -t"|" -k1 -n ../social_network/post_hasTag_tag_0_0.csv > post_hasTag_tag_0_0.csv 
sort -t"|" -k2 -n ../social_network/forum_containerOf_post_0_0.csv > forum_containerOf_post_0_0.csv 
sort -t"|" -k1 -n ../social_network/forum_hasTag_tag_0_0.csv > forum_hasTag_tag_0_0.csv 
sort -t"|" -k2 -n ../social_network/forum_hasMember_person_0_0.csv > forum_hasMember_person_0_0.csv 
sort -t"|" -k1 -n ../social_network/person_hasInterest_tag_0_0.csv > person_hasInterest_tag_0_0.csv 
sort -t"|" -k1 -n ../social_network/tag_hasType_tagclass_0_0.csv > tag_hasType_tagclass_0_0.csv 
sort -t"|" -k1 -n ../social_network/tagclass_isSubclassOf_tagclass_0_0.csv > tagclass_isSubclassOf_tagclass_0_0.csv 
sort -t"|" -k2 -n ../social_network/person_isLocatedIn_place_0_0.csv > person_isLocatedIn_place_0_0.csv
sort -t"|" -k2 -n ../social_network/comment_isLocatedIn_place_0_0.csv > comment_isLocatedIn_place_0_0.csv
sort -t"|" -k2 -n ../social_network/post_isLocatedIn_place_0_0.csv > post_isLocatedIn_place_0_0.csv
sort -t"|" -k2 -n ../social_network/organisation_isLocatedIn_place_0_0.csv > organisation_isLocatedIn_place_0_0.csv
sort -t"|" -k2 -n ../social_network/forum_hasModerator_person_0_0.csv > forum_hasModerator_person_0_0.csv

sort -t"|" -k2 -n ../social_network/comment_isLocatedIn_place_0_0.csv > comment_isLocatedIn_place_0_0.csv
sort -t"|" -k2 -n ../social_network/forum_hasModerator_person_0_0.csv > forum_hasModerator_person_0_0.csv
sort -t"|" -k2 -n ../social_network/organisation_isLocatedIn_place_0_0.csv > organisation_isLocatedIn_place_0_0.csv
sort -t"|" -k1 -n ../social_network/person_hasInterest_tag_0_0.csv > person_hasInterest_tag_0_0.csv
sort -t"|" -k2 -n ../social_network/person_isLocatedIn_place_0_0.csv > person_isLocatedIn_place_0_0.csv
sort -t"|" -k2 -n ../social_network/post_isLocatedIn_place_0_0.csv > post_isLocatedIn_place_0_0.csv 
sort -t"|" -k1 -n ../social_network/tag_hasType_tagclass_0_0.csv > tag_hasType_tagclass_0_0.csv 

date;
