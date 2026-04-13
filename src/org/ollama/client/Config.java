package org.ollama.client;

record Api (
    String host,
    long timeout,
    int chatRetries,
    boolean metrics
) {}
record Config (
    String tags,
    Api api
) {}
