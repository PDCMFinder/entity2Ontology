# Entity2Ontology - Mapping entities to Ontology terms

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=PDCMFinder_entity2Ontology&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=PDCMFinder_entity2Ontology)

`Entity2Ontology` is a Java tool that maps entities to specific ontology terms, useful for cases where non-textual 
information with attributes must be mapped accurately to a controlled vocabulary.

Consider for example the use case in [cancermodels.org](https://www.cancermodels.org/), where the diagnosis
in a cancer model needs to be linked to a relevant ontology term (like those from 
[NCIt](https://www.ebi.ac.uk/ols4/ontologies/ncit)), but additional information, like tumour type or collection site, 
is also available, and could be used to find a more 
precise ontology term.

As not all the attributes in the entity to map are equally relevant, `Entity2Ontology` allows you to define different 
weights for attributes, enabling you to control the mapping process with precision.

The application is written in Java 21, as a Maven project. The jar file, *entity2Ontology-[version].jar*, can be 
accessed through the command line or used as a dependency in your project.

---
## Table of Contents
* [Overview](#Overview)
* [Technologies](#technologies)
* [Usage](#usage)
* [Configuration](#configuration)
  * [Mapping Configuration file](#mapping-configuration-file)
  * [Indexing Request file](#indexing-request-file)
  * [Mapping Request file](#mapping-request-file)
* [Installation](#installation)
---

## Overview

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

### Entity types
`Entity2Ontology` allows you to map different types of entities, that is, entities representing different concepts,
having different attributes.

For instance, this is the structure of 2 different entities, `diagnosis` and `treatment`:
- entity type: diagnosis
    - OriginTissue
    - SampleDiagnosis
    - TumorType
    - DataSource
- entity type: treatment
    - TreatmentName

---

## Technologies
* Java 21
* JUnit 5
* Apache Lucene 9.11.1
---

## Current Version

The latest version of `Entity2Ontology` is **1.0-SNAPSHOT**.

You can pull the Docker image using:

```bash
docker pull pdxfinder/entity2ontology:1.0
```
For the latest version
```bash
docker pull pdxfinder/entity2ontology:latest
```


## Usage

### Index command
This command will produce a Lucene Index based in the information provided in the file `requestFile`.

Create the index once and reuse it for multiple mappings until updates are needed.

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
location (a path), and the location of the data to index. See [Indexing request file](#indexing_request_file).

#### Command Usage Example
This is an example
``` 
java -cp "entity2Ontology-1.0-SNAPSHOT.jar:lib/*" org.cancer_models.entity2ontology.Entity2Ontology index --request indexingRequest.json
```
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
other parameters to tune the mapping process to the specific needs. See [configuration file](#mapping_request_file).

##### `--output`
The path of the file with the output of the mapping process. The output is a JSON file with contains some information
about the mapping process. It also contains an array `mappingsResults` with every entity that was processed, and a list
of suggestions for each one. 

A suggestion is the match that the mapping process found. The suggestions per entity are 
sorted by score. The `score` value is a number from 0 to 100 indicating how similar the suggestion was respect to the 
entity.

#### Command Usage Example
``` 
java -cp "entity2Ontology-1.0-SNAPSHOT.jar:lib/*" org.cancer_models.entity2ontology.Entity2Ontology map --request mappingRequest.json --output myOutput.json
```

---
## Configuration
There are 3 JSON files used to configure the indexing and mapping tasks:
- **Mapping Configuration file**: The configuration of entities and fields relevant for your use cases.
  Once defined it should rarely change.
- **Indexing Request File**: Used to request the creation of an index. Only used when an index needs to be created or updated.
- **Mapping Request File**: Used to request the mapping of a list of entities.


<a id="mapping_configuration_file"></a>
### Mapping Configuration file
The mapping configuration is a JSON file where users can define the fields to use in the mapping process, as well as
their relevance. They also contain sections to define ontology templates, where users can customize how the fields
in the entities are going to be combined to find appropriate matches.

This file is set in the property `mappingConfigurationFile` in [mapping request file](#mapping_request_file).

Example:

<details>
<summary>Click to see an example of a mapping configuration file</summary>

```json
{
  "name": "pdcm configuration",
  "configurations": [
    {
      "entityType": "diagnosis",
      "fields": [
        {
          "name": "SampleDiagnosis",
          "weight": 1
        },
        {
          "name": "OriginTissue",
          "weight": 0.5
        },
        {
          "name": "TumorType",
          "weight": 0.5
        }
      ],
      "ontologyTemplates": [
        "${TumorType} ${SampleDiagnosis} in the ${OriginTissue}",
        "${TumorType} ${OriginTissue} ${SampleDiagnosis}",
        "${TumorType} ${SampleDiagnosis}",
        "${OriginTissue} ${SampleDiagnosis}"
      ]
    },
    {
      "entityType": "treatment",
      "fields": [
        {
          "name": "TreatmentName",
          "weight": 1
        }
      ]
    }
  ]
}

```
</details>

#### Properties
- `name`: An arbitrary name for this configuration.
- `configurations`: An array containing a configuration per entity type.
    - `entityType`: A string with the type of entity to configure.
    - `fields`: Array of fields and weights.
        - `name`: Name of the field or attribute.
        - `weight`: Positive number indicating how relevant this field is relative to other fields for this entity type.
    - `ontologyTemplates`: Array of strings representing templates. Format: `"${field_a} ${field_b}`. They represent
      combinations of the fields to find suitable matches against a label in an ontology term.


---
<a id="indexing_request_file"></a>
### Indexing Request File
The `index` command requires an *Index Request File* whose structure is as follows:

- `indexPath`: A directory where you have writing access, and you want the index to be created.
- `ruleLocations`: Indicates where the JSON files with the rules are.
  - `filePath`: Path to the JSON file with the rules related to this entity type.
  - `name`: Identifier for this set of rules.
  - `ignore`: Whether this set of rules should be ignored in the indexing process.
  - `fieldsConversion`: Some fields are mandatory for a rule, this section allows to define their equivalences in the
                        rules file in use.
    - `id`: A string uniquely identifying an entry in the rules file.
    - `entityType`: Each rule must define the entity type it applies to.
    - `data`: A map holding the attributes (fields) and values for the entity.
    - `label`: The label of the mapped ontology term.
    - `url`: The URL of the mapped ontology term.
- `ontologyLocations`: Indicates the ontology (currently only from OLS) from which the terms
                       will be downloaded.
  - `ontoId`: ID of the ontology in [OLS](https://www.ebi.ac.uk/ols4/ontologies).
  - `name`: Name to identify this set of ontologies.
  - `branches`: List of root terms to download.
  - `ignore`: Whether this set of ontologies should be ignored in the indexing process.

<details>
<summary>Click to see an example of an index request file</summary>

```json
{
  "indexPath": "IndexPath1",
  "ruleLocations": [
    {
      "filePath": "/path/file/treatments.json",
      "name": "treatment",
      "ignore": false,
      "fieldsConversion": {
        "id": "mappingKey",
        "entityType": "entityType",
        "data": "mappingValues",
        "label": "mappedTermLabel",
        "url": "mappedTermUrl"
      }
    }
  ],
  "ontologyLocations": [
    {
      "ontoId": "ncit",
      "name": "ncit ontology diagnosis",
      "branches": [
        "NCIT_C9305",
        "NCIT_C3262"
      ],
      "ignore": false
    }
  ]
}
```
</details>

<a id="mapping_request_file"></a>
### Mapping Request File
The `map` command requires a *Mapping Request File* which contains a set of entities to map, as well as the index to use.
This is the structure of the file:

- `maxNumSuggestions`: The maximum number of suggestions per entity.
- `indexPath`: The path on the Lucene index.
- `mappingConfigurationFile`: Path to the [configuration file](#mapping_configuration_file) defining the fields and 
                              weights.
- `entities`: An array with the entities to map.
  - `id`: A string uniquely identifying the entity.
  - `type`: The entity type.
  - `data`: Map with the attributes (fields) and values of the entity being mapped.

<details>
<summary>Click to see an example of a mapping request file</summary>

```json
{
  "maxNumSuggestions": 5,
  "indexPath": "/path/to/index",
  "mappingConfigurationFile": "/Users/.../pdcmMappingConfiguration.json",
  "entities": [
    {
      "id": "key_1",
      "type": "diagnosis",
      "data" : {
        "OriginTissue" : "bladder",
        "TumorType" : "recurrent",
        "SampleDiagnosis" : "t2 transitional cell carcinoma",
        "DataSource" : "jax"
      }
    }
  ]
}
```
</details>

---

## Installation
To use the Entity2Ontology application, follow these steps to clone the repository, build the project, and execute the 
generated JAR file.

### Prerequisites
Ensure you have the following installed on your system:

- [Java Development Kit (JDK) 21+](https://www.oracle.com/java/technologies/downloads/#java21)
- [Apache Maven](https://maven.apache.org/install.html)

###  Steps to Install and Build

**1. Clone the Repository**
Open a terminal and clone the repository using Git:

```bash
git clone https://github.com/PDCMFinder/entity2Ontology.git
cd entity2Ontology
```

**2. Build the Project**
Use Maven to compile the project and package it into a JAR file:

```bash
mvn clean package
```

This command will compile the source code, run tests, and package the application into a JAR file.


**3. Locate the JAR File**
After the build completes successfully, the JAR file will be located in the target/ directory:

```bash
target/entity2Ontology-1.0-SNAPSHOT.jar
```

### Running the Application
You can run the application using the java command. For example, to display help information:

```bash
java -cp "target/entity2Ontology-1.0-SNAPSHOT.jar:target/lib/*" org.cancer_models.entity2ontology.Entity2Ontology --help
```

This command executes the Entity2Ontology application with the specified classpath, including the generated JAR file 
and its dependencies.

### Additional Notes
- Replace `entity2Ontology-1.0-SNAPSHOT.jar` with the correct file name if the version differs.
- If you encounter any issues, ensure that all dependencies are correctly downloaded by Maven.
---

## License
This project is licensed under the Apache License 2.0.

## Acknowledgements
`Entity2Ontology` is supported by NCI U24CA253539 and the European Molecular Biology Laboratory.