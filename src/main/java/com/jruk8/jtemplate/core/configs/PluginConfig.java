package com.jruk8.jtemplate.core.configs;

import com.jruk8.jtemplate.core.messages.MessageFormat;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.Comment;
import lombok.Getter;

@Getter
@SuppressWarnings("FieldMayBeFinal")
@Header({
        "################################",
        "#         Plugin Config        #",
        "################################"
})
public class PluginConfig extends OkaeriConfig {

    @Comment({
            "Configures which format to use for messages.",
            "",
            "Supported values: MINIMESSAGE, LEGACY",
            "Default: MINIMESSAGE"
    })
    private MessageFormat messageFormat = MessageFormat.MINIMESSAGE;
}