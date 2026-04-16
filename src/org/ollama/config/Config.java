package org.ollama.config;

import java.util.Optional;
import static java.util.Optional.ofNullable;

public record Config (
    String tags,
    String searchUrl,
    ApiConfig apiConfig
) {
    public Optional<String> getSearchUrl() {
        return ofNullable(searchUrl);
    }

    public record ApiConfig (
        String host,
        long timeout,
        int chatRetries,
        boolean metrics,
        Mcp mcp
    ) {
        public Optional<Mcp> getMcp() {
            return ofNullable(mcp);
        }

        public record Mcp (
            String jsonPath,
            boolean use
        ) {
            public Optional<String> getJsonPath() {
                return ofNullable(jsonPath);
            }
        }
    }
}
