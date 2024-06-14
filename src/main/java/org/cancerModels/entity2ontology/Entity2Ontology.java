package org.cancerModels.entity2ontology;

import org.cancerModels.entity2ontology.map.command.MapCommand;
import org.cancerModels.entity2ontology.map.model.MappingRequest;
import org.cancerModels.entity2ontology.map.model.SourceEntity;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CommandLine.Command(name = "myapp", subcommands = {MapCommand.class})
public class Entity2Ontology implements Runnable {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new Entity2Ontology()).execute(args);
        System.out.println("Exit code : " + exitCode);
        System.exit(exitCode);


        Map<String, String> data = new HashMap<>();
        data.put("field1", "hello");
        data.put("field2", "world");
        SourceEntity entity1 = new SourceEntity("key1", data);
        SourceEntity entity2 = new SourceEntity("key2", null);
        List<SourceEntity> entityList = new ArrayList<>();
        entityList.add(entity1);
        entityList.add(entity2);
        MappingRequest mappingRequest = new MappingRequest(
            10,
            entityList);
        System.out.println(mappingRequest);
    }

    @Override
    public void run() {
        System.out.println("Use a subcommand: index or map");
    }
}