# Entity2Ontology - Mapping entities to Ontology terms

`Entity2Ontology` is a Java tool that maps entities to specific ontology terms, useful for cases where non-textual 
information with attributes must be mapped accurately to a controlled vocabulary.

Consider for example the use case in [cancermodels.org](https://www.cancermodels.org/), where the diagnosis
in a cancer model needs to be linked to a relevant ontology term (like those from [NCIt](https://www.ebi.ac.uk/ols4/ontologies/ncit)), 
but additional information, like tumour type or collection site, is also available, and could be used to find a more 
precise ontology term.

As not all the attributes in the entity to map are equally relevant, `Entity2Ontology` allows you to define different 
weights for attributes, enabling you to control the mapping process with precision.

The application is written in Java 22, as a Maven project. The jar file, *entity2Ontology-[version].jar*, can be 
accessed through the command line or used as a dependency in your project.


## Overview of the project

The `Entity2Ontology` mapping process consists of two steps: first, creating an index of ontology terms and, second, 
mapping entities to those terms.

### Indexing process
The objective of the indexing process is to allow searching for similar strings when we want to map an entity.
The goal is to match ontology terms, so we need to index a set of ontology terms.
However, we can also index *rules*, which are previous associations of entity-ontology term, so we still end up matching  
to an ontology term indirectly.

The indexing process, if successful, creates a [Lucene](https://lucene.apache.org/core/) index, which contains a set
of documents representing the ontology terms, our target.

### Mapping process
Once the ontologies and rules are indexed, we can start mapping entities to ontologies. In this step, the input of the 
process is a list of entitles to map, and the expected output is a list of suggested matches per entity.

### Rules
Rules represent previous entity-ontology associations and act as a knowledge base that `Entity2Ontology` can reference 
first when mapping. These are useful for leveraging existing mappings to improve new entity matches.

Example of a rule:
```json
[
  {
    "mappingKey": "085afcd822008bbff3d4ba2f4",
    "entityType": "diagnosis",
    "mappingValues": {
      "OriginTissue": "digestive/gastrointestinal",
      "TumorType": "metastatic",
      "SampleDiagnosis": "adenocarcinoma - stomach invasive poorly differentiated",
      "DataSource": "pdmr"
    },
    "mappedTermUrl": "http://purl.obolibrary.org/obo/NCIT_C4004",
    "mappedTermLabel": "GASTRIC ADENOCARCINOMA",
    "status": "Mapped",
    "mappingType": "Manual"
  }
]
```
**Note**: Your rules can look different, but they must contain the fields and values of the mapped entity, as well as the label
and URL of the ontology term. Currently only JSON files are supported, but the name of the attributes is configurable.

## Commands

### Index command
This command will produce a Lucene Index based in the information provided in the file `requestFile`.

Create the index once and reuse it for multiple mappings until updates are needed.

The index is created once, but can be used multiple times for mapping entities. The index
only needs to be created again if you need to update the documents it 
contains (for instance, if you get new ontology terms to index).

```
Usage: Entity2Ontology index [-hV] --request=<requestFile>
Indexes data into a Lucene index.
  -h, --help      Show this help message and exit.
      --request=<requestFile>
                  Indexing request JSON file.
  -V, --version   Print version information and exit.
```
#### Options
##### `--request`
This option allows passing a configuration JSON file with all the parameters needed to define an index, like its
location (a path), and the location of the data to index. An example of such configuration is shown later.

### Map command
This is where the actual mapping happens. It takes the user input and tries to match the entities to a document in the 
Lucene index, which will contain the rules and ontologies indexed in the previous step.

This command allows the user to map a list of entities into ontology terms using a specific index.
``` 
Usage: Entity2Ontology map [-hV] --output=<outputFile> --request=<requestFile>
Performs mapping using a mapping request JSON.
  -h, --help      Show this help message and exit.
      --output=<outputFile>
                  Output file to write the mapping results.
      --request=<requestFile>
                  Mapping request JSON file.
  -V, --version   Print version information and exit.
```
#### Options
##### `--request`
This option passing a configuration JSON file with the list of entities to map, the location of the index to use, and
other parameters to tune the mapping process to the specific needs.

## License
This project is licensed under the Apache License 2.0.