package com.jruk8.jtemplate.core.commands.user;

import com.jruk8.jtemplate.core.commands.user.help.HelpEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HelpEntryTest {

    private static final List<HelpEntry> ENTRIES = List.of(
            new HelpEntry("/jtemplate help [query]", "Shows this help menu.", "jtemplate.user.help"),
            new HelpEntry("/jtemplate authors", "Shows the authors of the plugin.", "jtemplate.user.authors"),
            new HelpEntry("/jtemplate reload", "Reloads the plugin configuration.", "jtemplate.admin.reload")
    );

    @Nested
    @DisplayName("matching() without a query")
    class BlankQueryTests {

        @Test
        @DisplayName("should return every entry when the query is null")
        void shouldReturnAllWhenQueryIsNull() {
            assertEquals(ENTRIES, HelpEntry.matching(ENTRIES, null));
        }

        @Test
        @DisplayName("should return every entry when the query is blank")
        void shouldReturnAllWhenQueryIsBlank() {
            assertEquals(ENTRIES, HelpEntry.matching(ENTRIES, "   "));
        }
    }

    @Nested
    @DisplayName("matching() with a query")
    class QueryTests {

        @Test
        @DisplayName("should match syntax case-insensitively")
        void shouldMatchSyntaxCaseInsensitively() {
            var matches = HelpEntry.matching(ENTRIES, "AUTHORS");

            assertEquals(1, matches.size());
            assertEquals("/jtemplate authors", matches.getFirst().syntax());
        }

        @Test
        @DisplayName("should match descriptions")
        void shouldMatchDescriptions() {
            var matches = HelpEntry.matching(ENTRIES, "reloads");

            assertEquals(1, matches.size());
            assertEquals("/jtemplate reload", matches.getFirst().syntax());
        }

        @Test
        @DisplayName("should return no entries when nothing matches")
        void shouldReturnEmptyWhenNothingMatches() {
            assertTrue(HelpEntry.matching(ENTRIES, "nothing-like-this").isEmpty());
        }
    }
}
