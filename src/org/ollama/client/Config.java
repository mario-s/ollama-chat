package org.ollama.client;

record ApiConfig (
    String host,
    long timeout,
    int chatRetries,
    boolean metrics
) {}
record Config (
    String tags,
    ApiConfig apiConfig
) {}
