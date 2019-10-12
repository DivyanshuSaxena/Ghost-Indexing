
# Creating an Index

## Generating the csv files for index creation

Run the executable 

$ IndexHandler/bTreeCreate

### Usage Details
IndexHandler/bTreeCreate &lt;label-of-vertex> &lt;index-name> &lt;min-no-of-child-in-bTree> &lt;column-no.-in-csv-to-index-on> &lt;path-to-input-csv> &lt;index-key-datatype(Date(D)/String(S)/Int(I))>

## Running gremlin script to create index

- Use the script **Scripts/IndexCreation/read_frm_file.gr**
- If this is the first index you are creating, you also need to execute **Scripts/IndexCreation/mgmtIndex.gr**
- Remember to have the right paths of the csv files generated in the above step.
- Also the data-type of the key of the index needs to be taken care of in this script. For example: Date type needs df.parse()
