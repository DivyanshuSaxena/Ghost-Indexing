## Steps to run this:

* change the conf file location in `loadit.sh` and `schema_mixed_indices.gremlin`
* Run the following command:
time ./loadit.sh ../social\_network ../sorted /home/prakhar/janus/delete/janusgraph-0.1.1-hadoop2/bin



======================================================================================
## Which files are what?


### USE THIS
`time ./loadit.sh <PATH-TO-DATA-WITH-HEADER-REMOVED> <PATH TO STORE THE SORTED FILES CONTAINING DATA> <PATH TO JANUSGRAPH BIN FOLDER>`

Eg.

`time ./loadit.sh ../social\_network ../sorted /home/prakhar/janus/delete/janusgraph-0.1.1-hadoop2/bin`

schema\_mixed\_indices.gremlin: This has the properties names like t\_id and p\_id instead of calling all of them `id`
loadit.sh: is the corresponding data loader file



#### Below files are not to be used in the general case of loading

schema\_correct\_indices.gremlin: This file creates schema properties by calling ids of all different kinds of object as `id` itself and thus creating need that the elastic search index created supports non-unique ids etc.
load\_correct\_indices.sh : is the corresponding loader file

