package com.jruk8.jtemplate.core.messages;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import lombok.Getter;

@Getter
@SuppressWarnings("FieldMayBeFinal")
@Header({
        "################################",
        "#            Messages          #",
        "################################"
})
public class MessageConfig extends OkaeriConfig {

    @Comment("Prefix appended before messages (use <prefix> in other messages)")
    private String prefix = "<gradient:#5e42f4:#b742f4>[JTemplate]</gradient> <gray>»</gray> ";

    @Comment("Message sent when a player lacks permission")
    private String noPermission = "<prefix><red>You don't have permission to do that!</red>";

    @Comment("Message sent on plugin reload")
    private String reloadSuccess = "<prefix><green>Configuration successfully reloaded in <time>ms!</green>";
}