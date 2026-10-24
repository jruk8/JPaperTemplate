package com.jruk8.jtemplate.core.commands.user.help;

import java.util.List;
import java.util.Locale;

/**
 * One row of the {@code /jtemplate help} listing: the syntax players type, a short description,
 * and the permission that gates its visibility.
 *
 * @param syntax the command syntax, e.g. {@code "/jtemplate authors"}
 * @param description the short help text for the command
 * @param permission the permission a sender must hold to see this entry
 */
public record HelpEntry(String syntax, String description, String permission) {

    /**
     * Filters entries by an optional search query, matching case-insensitively against syntax and description.
     * A null or blank query matches every entry.
     *
     * @param entries the entries to filter
     * @param query the search query, may be null or blank
     * @return the matching entries, in their original order
     */
    public static List<HelpEntry> matching(List<HelpEntry> entries, String query) {
        if (query == null || query.isBlank()) {
            return List.copyOf(entries);
        }
        String needle = query.toLowerCase(Locale.ROOT);
        return entries.stream()
                .filter(entry -> entry.syntax().toLowerCase(Locale.ROOT).contains(needle)
                        || entry.description().toLowerCase(Locale.ROOT).contains(needle))
                .toList();
    }
}
