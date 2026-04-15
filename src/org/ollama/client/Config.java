package org.ollama.client;

import java.util.Optional;

import static java.util.Optional.ofNullable;

record Mcp (
    String jsonPath,
    boolean use
) {
    Optional<String> getJsonPath() {
        return ofNullable(jsonPath);
    }
}
record ApiConfig (
    String host,
    long timeout,
    int chatRetries,
    boolean metrics,
    Mcp mcp
) {
    Optional<Mcp> getMcp() {
        return ofNullable(mcp);
    }
}
record Config (
    String tags,
    ApiConfig apiConfig
) {}
