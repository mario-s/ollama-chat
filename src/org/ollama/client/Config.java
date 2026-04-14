package org.ollama.client;

import java.util.List;

record ApiConfig (
    String host,
    long timeout,
    int chatRetries,
    boolean metrics,
    List<String> tools
) {}
record Config (
    String tags,
    ApiConfig apiConfig
) {}
