## HOW TO BUILD INDEXES?

* If this is for the first time you are hearing about our Ghost Indices, you will have to create the sceham for them first (our bulk loader does that automatically - so if you used it you should skip this step) - from indexCreationScripts/mgmtIndex.gremlin
* Run the following command:

`./unifyIndex.sh post post_creationDate_index_bPlus_100 100 3 /home/prakhar/social_network.og/post_0_0.csv D BP po_id`

or simply ./unifyIndex.sh to know the arguments to pass

