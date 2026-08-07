package com.jruk8.jtemplate.core.commands;

import com.jruk8.jtemplate.core.Bootstrap;
import com.jruk8.jtemplate.core.configs.ConfigRegistrar;
import com.jruk8.jtemplate.core.messages.Messenger;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.paper.util.sender.Source;

public class CommandRegistrar implements Bootstrap {

    private final AnnotationParser<Source> annotationParser;
    private final ConfigRegistrar config;
    private final Messenger messenger;

    public CommandRegistrar(AnnotationParser<Source> annotationParser,
                            ConfigRegistrar config,
                            Messenger messenger) {
        this.annotationParser = annotationParser;
        this.config = config;
        this.messenger = messenger;
    }

    @Override
    public void register() {
        annotationParser.parse(new CoreCommands(config, messenger));
    }
}