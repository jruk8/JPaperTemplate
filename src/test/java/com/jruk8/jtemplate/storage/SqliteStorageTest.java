package com.jruk8.jtemplate.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqliteStorageTest {

    private static final String STAT_KEY = "authors_command_uses";

    private final UUID firstPlayer = UUID.randomUUID();
    private final UUID secondPlayer = UUID.randomUUID();

    private SqliteStorage storage;

    @BeforeEach
    void setUp() {
        this.storage = new SqliteStorage("jdbc:sqlite::memory:", Logger.getLogger("sqlite-test"));
        assertTrue(this.storage.init());
    }

    @AfterEach
    void tearDown() {
        if (this.storage != null) {
            this.storage.close();
        }
    }

    @Nested
    @DisplayName("init()")
    class InitTests {

        @Test
        @DisplayName("should initialize an in-memory database")
        void shouldInitializeMemoryDatabase() {
            assertTrue(storage.init());
        }
    }

    @Nested
    @DisplayName("reads")
    class ReadTests {

        @Test
        @DisplayName("should return zero for missing player stat")
        void shouldReturnZeroForMissingPlayerStat() {
            assertEquals(0, storage.playerTotal(STAT_KEY, firstPlayer));
        }

        @Test
        @DisplayName("should return zero for missing global stat")
        void shouldReturnZeroForMissingGlobalStat() {
            assertEquals(0, storage.globalTotal(STAT_KEY));
        }

        @Test
        @DisplayName("should isolate counters between players")
        void shouldIsolatePlayers() {
            storage.increment(STAT_KEY, firstPlayer);

            assertEquals(1, storage.playerTotal(STAT_KEY, firstPlayer));
            assertEquals(0, storage.playerTotal(STAT_KEY, secondPlayer));
        }
    }

    @Nested
    @DisplayName("increments")
    class IncrementTests {

        @Test
        @DisplayName("should increment the player counter only")
        void shouldIncrementPlayerCounter() {
            storage.increment(STAT_KEY, firstPlayer);

            assertEquals(1, storage.playerTotal(STAT_KEY, firstPlayer));
            assertEquals(0, storage.globalTotal(STAT_KEY));
        }

        @Test
        @DisplayName("should increment the global counter only")
        void shouldIncrementGlobalCounter() {
            storage.incrementGlobal(STAT_KEY);

            assertEquals(1, storage.globalTotal(STAT_KEY));
            assertEquals(0, storage.playerTotal(STAT_KEY, firstPlayer));
        }

        @Test
        @DisplayName("should accumulate multiple increments")
        void shouldAccumulateIncrements() {
            storage.increment(STAT_KEY, firstPlayer);
            storage.increment(STAT_KEY, firstPlayer);
            storage.increment(STAT_KEY, firstPlayer);

            assertEquals(3, storage.playerTotal(STAT_KEY, firstPlayer));
        }

        @Test
        @DisplayName("should aggregate global counter across players")
        void shouldAggregateGlobally() {
            storage.increment(STAT_KEY, firstPlayer);
            storage.incrementGlobal(STAT_KEY);
            storage.increment(STAT_KEY, firstPlayer);
            storage.incrementGlobal(STAT_KEY);
            storage.increment(STAT_KEY, secondPlayer);
            storage.incrementGlobal(STAT_KEY);

            assertEquals(3, storage.globalTotal(STAT_KEY));
        }
    }

    @Nested
    @DisplayName("arbitrary values")
    class ValueTests {

        @Test
        @DisplayName("should store and overwrite a player value")
        void shouldStorePlayerValue() {
            storage.setPlayerValue(STAT_KEY, firstPlayer, 7);

            assertEquals(7, storage.playerTotal(STAT_KEY, firstPlayer));

            storage.setPlayerValue(STAT_KEY, firstPlayer, 3);

            assertEquals(3, storage.playerTotal(STAT_KEY, firstPlayer));
        }

        @Test
        @DisplayName("should read a player value without a default")
        void shouldReadPlayerValue() {
            assertTrue(storage.playerValue(STAT_KEY, firstPlayer).isEmpty());

            storage.setPlayerValue(STAT_KEY, firstPlayer, 5);

            assertEquals(5, storage.playerValue(STAT_KEY, firstPlayer).orElseThrow());
        }
    }

    @Nested
    @DisplayName("persistence")
    class PersistenceTests {

        @Test
        @DisplayName("should persist values across reopen")
        void shouldPersistAcrossReopen(@TempDir Path directory) {
            File dbFile = directory.resolve("persisted.db").toFile();

            var first = new SqliteStorage(dbFile, Logger.getLogger("first"));
            assertTrue(first.init());
            first.increment(STAT_KEY, firstPlayer);
            first.incrementGlobal(STAT_KEY);
            first.close();

            var second = new SqliteStorage(dbFile, Logger.getLogger("second"));
            assertTrue(second.init());
            assertEquals(1, second.playerTotal(STAT_KEY, firstPlayer));
            assertEquals(1, second.globalTotal(STAT_KEY));
            second.close();

            storage = null;
        }
    }
}