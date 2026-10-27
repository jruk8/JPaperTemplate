package com.jruk8.jtemplate.placeholder;

import com.jruk8.jtemplate.storage.SqliteStorage;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Domain service for the command usage statistics behind the
 * {@code jtemplate} placeholders. Keeps the DB stat key in one place and
 * exposes the read/write operations the expansion and commands need.
 */
public final class CommandUsageTracker {

    /**
     * Stat key for total tracked command executions per player.
     */
    public static final String USES_KEY = "jtemplate_uses";

    private final Supplier<SqliteStorage> storage;

    /**
     * @param storage resolves the live storage; reloads swap the instance,
     * so holders must not keep a direct reference
     */
    public CommandUsageTracker(Supplier<SqliteStorage> storage) {
        this.storage = storage;
    }

    /**
     * Records one execution of a tracked command, per player and globally.
     *
     * @param player the player that executed the command
     */
    public void recordUse(UUID player) {
        this.storage.get().increment(USES_KEY, player);
        this.storage.get().incrementGlobal(USES_KEY);
    }

    public int playerUses(UUID player) {
        return this.storage.get().playerTotal(USES_KEY, player);
    }

    public int globalUses() {
        return this.storage.get().globalTotal(USES_KEY);
    }
}
