package org.cancer_models.entity2ontology;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

// A custom factory to allow Picocli to use Spring for creating command instances
@Component
class SpringCommandFactory implements CommandLine.IFactory {
    private final ApplicationContext context;

    @Autowired
    public SpringCommandFactory(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public <K> K create(Class<K> cls) throws Exception {
        return context.getBean(cls);
    }
}
