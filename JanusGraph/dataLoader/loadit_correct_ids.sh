#!/bin/bash

if (( $# != 3 )); then
	echo 'Usage: ./loadit.sh <source> <dest> <path_to_gremlin.sh>\n';
	exit -1;
fi

date;

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

$3/gremlin.sh < schema_correct_ids.gremlin

echo 'Schema creation complete';
echo '========================';
echo 'Reading data and loading it';


echo "
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.graphdb.vertices.*;
import java.util.Date;
import java.text.*;
import org.apache.commons.csv.*
//if (args) {
//    confFile = args[0]
//}
g = JanusGraphFactory.open ('/home/baadalservervm/janus/janusgraph-0.1.1-hadoop2/conf/janusgraph-cassandra-es.properties');
gt = g.traversal();

m2mFlag = 0;

def Date getDate1(String str){
        try
        {
                SimpleDateFormat f = new SimpleDateFormat(\"yyyy-MM-dd'T'HH:mm:ss.SSSZ\");
                Date d = f.parse(str);
                return(d);
        }
        catch(Exception e)
        {
                System.out.println('Exception: '+e);
        }
        return(null);
}

def Date getDate2(String str) {
        try
        {
                SimpleDateFormat f = new SimpleDateFormat('yyyy-MM-dd');
                Date d = f.parse(str);
                return(d);
        }
        catch(Exception e)
        {
                System.out.println('Exception: '+e);
        }
        return(null);
}
def Date getDate3(String str){
        try
        {
                SimpleDateFormat f = new SimpleDateFormat(\"yyyy-MM-dd'T'HH:mm:ss.SSSZ\");
                Date d = f.parse('2010-03-17T10:02:10.447+0000');
                return(d);
        }
        catch(Exception e)
        {
                System.out.println('Exception: '+e);
        }
        return(null);
}
def boolean readBridge (final JanusGraph g, final String bridgeFile,
                                       final String src, final String srcPkProp,
                                       final String tgt, final String tgtPkProp,
                                       final String allowedLabel,
                                       final String delimiterChar,
                                       final String quoteChar,
				       final boolean isEdgeprop,
				       final String edgePropName,
				       final boolean isDate,
                                       final int commitFrequency,
				       final int sortColumn,
				       final int m2m) {
       gt = g.traversal();
       prev_val = -1;
       cache_node = null;
	node_map = new HashMap<Long, CacheVertex>();
       for (final CSVRecord csvrec : CSVFormat.DEFAULT.withDelimiter (delimiterChar as char).withIgnoreEmptyLines().withAllowMissingColumnNames().withQuote (quoteChar as char).parse (new FileReader (bridgeFile)) ) {
               if (csvrec.getRecordNumber() > 0 & csvrec.getRecordNumber() % 30000 == 0) { 
	       	       g.tx().commit(); System.gc();
		       println 'line:'+csvrec.getRecordNumber();
	       }
               try {
			src_instance = null;
			tgt_instance = null;
			if(sortColumn == 0){
                             k = csvrec.get(0).toLong();
                             if(k!=prev_val) {
                                  cache_node = gt.V().has (srcPkProp, k).next()
                                  prev_val = k;
                              }
			      src_instance = cache_node;
			      k2 = csvrec.get(1).toLong();
			      if(m2m == 2) {
			      	if(!node_map.containsKey(k2)) {
				  tgt_instance = gt.V().has (tgtPkProp, k2).next()
				  node_map.put(k2, tgt_instance);  
			      	}
			      	else {
			      	  tgt_instance = node_map.get(k2); 
			      	}
			      }
			      else {
				  tgt_instance = gt.V().has (tgtPkProp, k2).next()
			      }
                         }
                         if(sortColumn == 1){
                              k = csvrec.get(1).toLong();
                              if(k!=prev_val) {
                                   cache_node = gt.V().has (tgtPkProp, k).next()
                                   prev_val = k;
                              }
			      tgt_instance = cache_node;
			      k2 = csvrec.get(0).toLong();
			      if(m2m == 2) {
                                if(!node_map.containsKey(k2)) {
                                  src_instance = gt.V().has (srcPkProp, k2).next()
                                  node_map.put(k2, src_instance);
                                }
                                else {
                                  src_instance = node_map.get(k2);
                                }
                              }
                              else {
                                  src_instance = gt.V().has (srcPkProp, k2).next()
                              }   
                         }
                       if (isEdgeprop) {
                               if(isDate){
				    SimpleDateFormat f = new SimpleDateFormat(\"yyyy-MM-dd'T'HH:mm:ss.SSSZ\");
                		    Date d = f.parse(csvrec.get(2));
				    src_instance.addEdge (allowedLabel, tgt_instance, edgePropName, d);
                               }
			       else{
				   src_instance.addEdge (allowedLabel, tgt_instance, edgePropName, csvrec.get(2).toInteger())
			       }
		       }
		       else{
		 	       src_instance.addEdge (allowedLabel, tgt_instance)
		       }
               }
               catch (Exception e) {
                       println 'ERROR : Error in finding vertices to connect in record ' + csvrec.getRecordNumber() + ' (src=' + srcPkProp + ', tgt=' + tgtPkProp +')'+e;
               }
       }
       node_map.clear();
       g.tx().commit(); System.gc();
       println 'edges are created between '+src+ ' and '+tgt+'.';
}
/*
def boolean createEdge (final TitanGraph g, 
                        final String csvFile,
						 final int csvFilecolumn,
                        final String delimiterChar,
                        final String quoteChar,
                        final String srcProp,
						 final String tgtProp,
						 final String edgeLabel{
	gt = g.traversal();
	__src_list = [];
	__tgt_list = []
	for (final CSVRecord csvrec : CSVFormat.DEFAULT.withDelimiter (delimiterChar as char).withIgnoreEmptyLines().withAllowMissingColumnNames().withQuote (quoteChar as char).parse (new FileReader (csvFile)) ) {
   	try{
			if( csvrec.get(csvFilecolumn).trim()){
	        	int __join_value = csvrec.get(csvFilecolumn).toInteger();
	        	__edge_count =  gt.V().has(srcProp, __join_value).limit(1).out(edgeLabel).count().next();
	        	if(__edge_count==0){
	        		__src_list =  gt.V().has(srcProp, __join_value).toList();
	        		__tgt_list = gt.V().has(tgtProp, __join_value).toList();
	        		for(n1 in __src_list){
	            		for(n2 in __tgt_list){
 	                		n1.addEdge(edgeLabel, n2);
	                	}
	            	}
	            	gr.tx().commit(); System.gc();
	        	}
	    	}
		}
		catch(Exception e){
			println 'ERROR : Error in finding vertices to connect for join property value ' +csvrec.get(csvFilecolumn) +' (src=' + srcProp + ', tgt=' + tgtProp + ', label=' + edgeLabel + ')'
		}
	}
*/
cache_node = null;
prev_val = -1;
pre = System.nanoTime();

for (final CSVRecord csvrec : CSVFormat.DEFAULT.withDelimiter ('|' as char).withIgnoreEmptyLines().withAllowMissingColumnNames().withQuote('¡' as char).parse(new FileReader ('$1/place_0_0.csv'))){
    if (csvrec.getRecordNumber() > 0 & csvrec.getRecordNumber() % 30000 == 0) { g.tx().commit(); System.gc(); }
    v = g.addVertex(T.label,'place');
    v.property('id',csvrec.get(0).toLong())
    if (csvrec.get(1).trim()) {
        v.property('name',csvrec.get(1))
    }
    if (csvrec.get(2).trim()) {
        v.property('url',csvrec.get(2))
    }
    if (csvrec.get(3).trim()) {
        v.property('type',csvrec.get(3))
    }
}

g.tx().commit(); System.gc();

println '###### Completed place #######\n'

for (final CSVRecord csvrec : CSVFormat.DEFAULT.withDelimiter ('|' as char).withIgnoreEmptyLines().withAllowMissingColumnNames().withQuote('¡' as char).parse(new FileReader ('$1/person_0_0.csv'))){
    if (csvrec.getRecordNumber() > 0 & csvrec.getRecordNumber() % 30000 == 0) { g.tx().commit(); System.gc(); }
    v = g.addVertex(T.label,'person');
    v.property('id',csvrec.get(0).toLong())
    if (csvrec.get(1).trim()) {
        v.property('firstName',csvrec.get(1))
    }
    if (csvrec.get(2).trim()) {
        v.property('lastName',csvrec.get(2))
    }
    if (csvrec.get(3).trim()) {
        v.property('gender',csvrec.get(3))
    }
    if (csvrec.get(4).trim()) {
        v.property('birthday',getDate2(csvrec.get(4)))
    }
    if (csvrec.get(5).trim()) {
        v.property('creationDate',getDate3(csvrec.get(5)))
    }
    if (csvrec.get(6).trim()) {
        v.property('locationIP',csvrec.get(6))
    }
    if (csvrec.get(7).trim()) {
        v.property('browserUsed',csvrec.get(7))
    }
/*    if (csvrec.get(8).trim()) {
	k = csvrec.get(8).toLong()
	if(k!=prev_val) {
	    cache_node = gt.V().has('id',k).next();
	    prev_val = k;
	}
	v.addEdge('isLocatedIn',cache_node);
    }
*/
}

g.tx().commit(); System.gc();

println '###### Completed person #######\n'
prev_val = -1;
for (final CSVRecord csvrec : CSVFormat.DEFAULT.withDelimiter ('|' as char).withIgnoreEmptyLines().withAllowMissingColumnNames().withQuote('¡' as char).parse(new FileReader ('$1/organisation_0_0.csv'))){
    if (csvrec.getRecordNumber() > 0 & csvrec.getRecordNumber() % 30000 == 0) { g.tx().commit(); System.gc(); }
    v = g.addVertex(T.label,'organization');
    v.property('id',csvrec.get(0).toLong())
    if (csvrec.get(1).trim()) {
        v.property('type',csvrec.get(1))
    }
    if (csvrec.get(2).trim()) {
        v.property('name',csvrec.get(2))
    }
    if (csvrec.get(3).trim()) {
        v.property('url',csvrec.get(3))
    }
/*    if (csvrec.get(4).trim()) {
        k = csvrec.get(4).toLong()
        if(k!=prev_val) {
            cache_node = gt.V().has('id',k).next();
	    prev_val = k;
        }
        v.addEdge('isLocatedIn',cache_node);
    }
*/
}

g.tx().commit(); System.gc();

println '###### Completed organization #######\n'
prev_val = -1;
for (final CSVRecord csvrec : CSVFormat.DEFAULT.withDelimiter ('|' as char).withIgnoreEmptyLines().withAllowMissingColumnNames().withQuote('¡' as char).parse(new FileReader ('$1/post_0_0.csv'))){
    if (csvrec.getRecordNumber() > 0 & csvrec.getRecordNumber() % 30000 == 0) { g.tx().commit(); System.gc(); }
    v = g.addVertex(T.label,'post');
    v.property('id',csvrec.get(0).toLong())
    if (csvrec.get(1).trim()) {
        v.property('imageFile',csvrec.get(1))
    }
    if (csvrec.get(2).trim()) {
        v.property('creationDate',getDate3(csvrec.get(2)))
    }
    if (csvrec.get(3).trim()) {
        v.property('locationIP',csvrec.get(3))
    }
    if (csvrec.get(4).trim()) {
        v.property('browserUsed',csvrec.get(4))
    }
    if (csvrec.get(5).trim()) {
        v.property('language',csvrec.get(5))
    }
    if (csvrec.get(6).trim()) {
        v.property('content',csvrec.get(6))
    }
    if( csvrec.get(7).trim()) {
        v.property('length',csvrec.get(7).toInteger())
    }
/*    if (csvrec.get(8).trim()) {
        k = csvrec.get(8).toLong()
        if(k!=prev_val) {
            cache_node = gt.V().has('id',k).next();
	    prev_val = k;
        }
        v.addEdge('isLocatedIn',cache_node);
    }
*/    
}

g.tx().commit(); System.gc();

println '###### Completed post #######\n'
prev_val = -1;
for (final CSVRecord csvrec : CSVFormat.DEFAULT.withDelimiter ('|' as char).withIgnoreEmptyLines().withAllowMissingColumnNames().withQuote('¡' as char).parse(new FileReader ('$1/comment_0_0.csv'))){
    if (csvrec.getRecordNumber() > 0 & csvrec.getRecordNumber() % 30000 == 0) { g.tx().commit(); System.gc(); }
    v = g.addVertex(T.label,'comment');
    v.property('id',csvrec.get(0).toLong())
    if (csvrec.get(1).trim()) {
        v.property('creationDate',getDate3(csvrec.get(1)))
    }
    if (csvrec.get(2).trim()) {
        v.property('locationIP',csvrec.get(2))
    }
    if (csvrec.get(3).trim()) {
        v.property('browserUsed',csvrec.get(3))
    }
    if (csvrec.get(4).trim()) {
        v.property('content',csvrec.get(4))
    }
    if( csvrec.get(5).trim()) {
        v.property('length',csvrec.get(5).toInteger())
    }
/*    if (csvrec.get(6).trim()) {
        k = csvrec.get(6).toLong()
        if(k!=prev_val) {
            cache_node = gt.V().has('id',k).next();
            prev_val = k;
        }
        v.addEdge('isLocatedIn',cache_node);
    }
*/
}

g.tx().commit(); System.gc();

println '###### Completed comment #######\n'
prev_val = -1;
for (final CSVRecord csvrec : CSVFormat.DEFAULT.withDelimiter ('|' as char).withIgnoreEmptyLines().withAllowMissingColumnNames().withQuote('¡' as char).parse(new FileReader ('$1/forum_0_0.csv'))){
    if (csvrec.getRecordNumber() > 0 & csvrec.getRecordNumber() % 30000 == 0) { g.tx().commit(); System.gc(); }
    v = g.addVertex(T.label,'forum');
    v.property('id',csvrec.get(0).toLong())
    if (csvrec.get(1).trim()) {
        v.property('title',csvrec.get(1))
    }
    if (csvrec.get(2).trim()) {
        v.property('creationDate',getDate3(csvrec.get(2)))
    }
/*    if (csvrec.get(3).trim()) {
        k = csvrec.get(3).toLong()
        if(k!=prev_val) {
            cache_node = gt.V().has('id',k).next();
            prev_val = k;
        }
        v.addEdge('hasModerator',cache_node);
    }
*/
}

g.tx().commit(); System.gc();

println '###### Completed forum #######\n'
prev_val = -1;

/*i = CSVFormat.DEFAULT.withDelimiter ('|' as char).withIgnoreEmptyLines().withAllowMissingColumnNames().withQuote('¡' as char).parse(new FileReader('$2/person_hasInterest_tag_0_0.csv'));
bridge = i.iterator();
b_record = null;
if(bridge.hasNext()){
    b_record = bridge.next();
}*/
for (final CSVRecord csvrec : CSVFormat.DEFAULT.withDelimiter ('|' as char).withIgnoreEmptyLines().withAllowMissingColumnNames().withQuote('¡' as char).parse(new FileReader ('$1/tag_0_0.csv'))){
    if (csvrec.getRecordNumber() > 0 & csvrec.getRecordNumber() % 30000 == 0) { g.tx().commit(); System.gc(); }
    v = g.addVertex(T.label,'tag');
    pk = csvrec.get(0).toLong();
    v.property('id',pk)
    if (csvrec.get(1).trim()) {
        v.property('name',csvrec.get(1))
    }
    if (csvrec.get(2).trim()) {
        v.property('url',csvrec.get(2))
    }
/*
    while(b_record.get(1).toLong()==pk) {
	person = gt.V().has('id',b_record.get(0).toLong()).next();
        person.addEdge('hasInterest',v);
        if(bridge.hasNext()){
	    b_record = bridge.next();
        }
	else
	    break;
    }
*/
}

g.tx().commit(); System.gc();

println '###### Completed tag #######\n'

/*i = CSVFormat.DEFAULT.withDelimiter ('|' as char).withIgnoreEmptyLines().withAllowMissingColumnNames().withQuote('¡' as char).parse(new FileReader('$2/tag_hasType_tagclass_0_0.csv'));
bridge = i.iterator();
b_record = null;
if(bridge.hasNext()){
    b_record = bridge.next();
}
*/
for (final CSVRecord csvrec : CSVFormat.DEFAULT.withDelimiter ('|' as char).withIgnoreEmptyLines().withAllowMissingColumnNames().withQuote('¡' as char).parse(new FileReader ('$1/tagclass_0_0.csv'))){
    if (csvrec.getRecordNumber() > 0 & csvrec.getRecordNumber() % 30000 == 0) { g.tx().commit(); System.gc(); }
    v = g.addVertex(T.label,'tagclass');
    pk = csvrec.get(0).toLong();
    v.property('id',pk)
    if (csvrec.get(1).trim()) {
        v.property('name',csvrec.get(1))
    }
    if (csvrec.get(2).trim()) {
        v.property('url',csvrec.get(2))
    }

/*    while(b_record.get(1).toLong()==pk) {
        tag = gt.V().has('id',b_record.get(0).toLong()).next();
        tag.addEdge('hasType',v);
        if(bridge.hasNext()){
            b_record = bridge.next();
        }
	else
	    break;
    }
*/
}

g.tx().commit(); System.gc();

println '###### Completed tagclass #######\n'



for (final CSVRecord csvrec : CSVFormat.DEFAULT.withDelimiter ('|' as char).withIgnoreEmptyLines().withAllowMissingColumnNames().withQuote('¡' as char).parse(new FileReader ('$1/person_email_emailaddress_0_0.csv'))){
    if (csvrec.getRecordNumber() > 0 & csvrec.getRecordNumber() % 30000 == 0) { g.tx().commit(); System.gc(); }
    v = gt.V().has('id',csvrec.get(0).toLong()).next();
    try
    {
    	if (csvrec.get(1).trim()) {
        	v.property('emails',csvrec.get(1))
    	}
    }
    catch(Exception e)
    {
	println 'Exception inside email adding function, person node is not found with id:'+csvrec.get(0);
    }
}

g.tx().commit(); System.gc();

println '###### Completed email #######\n'

for (final CSVRecord csvrec : CSVFormat.DEFAULT.withDelimiter ('|' as char).withIgnoreEmptyLines().withAllowMissingColumnNames().withQuote('¡' as char).parse(new FileReader ('$1/person_speaks_language_0_0.csv'))){
    if (csvrec.getRecordNumber() > 0 & csvrec.getRecordNumber() % 30000 == 0) { g.tx().commit(); System.gc(); }
    v = gt.V().has('id',csvrec.get(0).toLong()).next();
    try
    {
	    if (csvrec.get(1).trim()) {
        	v.property('speaksLanguage',csvrec.get(1))
	    }
    }
    catch(Exception e)
    {
        println 'Exception inside language adding function, person node is not found with id:'+csvrec.get(0);
    }
}

g.tx().commit(); System.gc();

println '###### Completed Spoken Languages #######\n'

exec_time = System.nanoTime() - pre;
println 'Nodes Loading time: '+exec_time * 1.0 / 1E6;

pre = System.nanoTime();

readBridge (g, '$2/comment_hasCreator_person_0_0.csv', 'comment', 'id', 'person', 'id', 'hasCreator', '|', '¡', false, '', false, 30000,1,0)
readBridge (g, '$2/comment_isLocatedIn_place_0_0.csv', 'comment', 'id', 'place', 'id', 'isLocatedIn', '|', '¡', false, '', false, 30000,1,0)
readBridge (g, '$2/comment_replyOf_comment_0_0.csv', 'comment', 'id', 'comment', 'id', 'replyOf', '|', '¡', false, '', false, 30000,1,0)
readBridge (g, '$2/comment_replyOf_post_0_0.csv', 'comment', 'id', 'post', 'id', 'replyOf', '|', '¡', false, '', false, 30000,1,0)
readBridge (g, '$2/forum_containerOf_post_0_0.csv', 'forum', 'id', 'post', 'id', 'containerOf', '|', '¡', false, '', false, 30000,0,0)
readBridge (g, '$2/forum_hasMember_person_0_0.csv', 'forum', 'id', 'person', 'id', 'hasMember', '|', '¡', true, 'joinDate', true, 30000,0,1)

readBridge (g, '$2/forum_hasModerator_person_0_0.csv', 'forum', 'id', 'person', 'id', 'hasModerator', '|', '¡', false, '', false, 30000,1,0)

readBridge (g, '$2/forum_hasTag_tag_0_0.csv', 'forum', 'id', 'tag', 'id', 'hasTag', '|', '¡', false, '', false, 30000,0,1)
readBridge (g, '$2/organisation_isLocatedIn_place_0_0.csv', 'organization', 'id', 'place', 'id', 'isLocatedIn', '|', '¡', false, '', false, 30000,1,0)
readBridge (g, '$2/person_hasInterest_tag_0_0.csv', 'person', 'id', 'tag', 'id', 'hasInterest', '|', '¡', false, '', false, 30000,0,1)
readBridge (g, '$2/person_isLocatedIn_place_0_0.csv', 'person', 'id', 'place', 'id', 'isLocatedIn', '|', '¡', false, '', false, 30000,1,0)
readBridge (g, '$2/person_knows_person_0_0.csv', 'person', 'id', 'person', 'id', 'knows', '|', '¡', true, 'creationDate', true, 30000,0,1)
readBridge (g, '$2/person_likes_comment_0_0.csv', 'person', 'id', 'comment', 'id', 'likes', '|', '¡', true, 'creationDate', true, 30000,1,1)
readBridge (g, '$2/person_likes_post_0_0.csv', 'person', 'id', 'post', 'id', 'likes', '|', '¡', true, 'creationDate', true, 30000,1,1)
readBridge (g, '$2/person_studyAt_organisation_0_0.csv', 'person', 'id', 'organization', 'id', 'studyAt', '|', '¡', true, 'classYear', false, 30000,1,1)
readBridge (g, '$2/person_workAt_organisation_0_0.csv', 'person', 'id', 'organization', 'id', 'workAt', '|', '¡', true, 'workFrom', false, 30000,1,1)
readBridge (g, '$2/place_isPartOf_place_0_0.csv', 'place', 'id', 'place', 'id', 'isPartOf', '|', '¡', false, '', false, 30000,1,0)
readBridge (g, '$2/post_hasCreator_person_0_0.csv', 'post', 'id', 'person', 'id', 'hasCreator', '|', '¡', false, '', false, 30000,1,0)
readBridge (g, '$2/post_hasTag_tag_0_0.csv', 'post', 'id', 'tag', 'id', 'hasTag', '|', '¡', false, '', false, 30000,0,1)
readBridge (g, '$2/post_isLocatedIn_place_0_0.csv', 'post', 'id', 'place', 'id', 'isLocatedIn', '|', '¡', false, '', false, 30000,1,0)
readBridge (g, '$2/tag_hasType_tagclass_0_0.csv', 'tag', 'id', 'tagclass', 'id', 'hasType', '|', '¡', false, '', false, 30000,0,1)
readBridge (g, '$2/tagclass_isSubclassOf_tagclass_0_0.csv', 'tagclass', 'id', 'tagclass', 'id', 'isSubClassOf', '|', '¡', false, '', false, 30000,1,1)

exec_time = System.nanoTime() - pre;
println 'Edge Loading time: '+exec_time * 1.0 / 1E6;
g.close();
System.exit(1);"  > reader.gremlin


echo 'loading data';
echo "=============";

$3/gremlin.sh < reader.gremlin

echo 'loading done';
