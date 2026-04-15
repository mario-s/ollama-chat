package org.ollama.client;

record Mcp (
    String jsonPath,
    boolean use
) {}
record ApiConfig (
    String host,
    long timeout,
    int chatRetries,
    boolean metrics,
    Mcp mcp
) {}
record Config (
    String tags,
    ApiConfig apiConfig
) {}
