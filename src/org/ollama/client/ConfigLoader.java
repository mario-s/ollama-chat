package org.ollama.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ConfigLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigLoader.class);

    private static final String APP_DIR = ".ollama-chat";
    private static final String FILE_NAME = "ollama.yaml";

    private final ObjectMapper mapper;

    ConfigLoader() {
        this.mapper = new ObjectMapper(new YAMLFactory());
    }

    Config load() {
        String home = System.getProperty("user.home");
        Path userHomeConfig = Path.of(home, APP_DIR, FILE_NAME);
        if (Files.exists(userHomeConfig)) {
            LOG.trace("using config in user's home");
            return loadFromFile(userHomeConfig);
        }

        Path localConfig = Path.of(FILE_NAME);
        if (Files.exists(localConfig)) {
            LOG.trace("using local config");
            return loadFromFile(localConfig);
        }

        LOG.trace("using default config");
        return defaultConfig();
    }

    private Config loadFromFile(Path path) {
        try {
            return mapper.readValue(path.toFile(), Config.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Config defaultConfig() {
        return new Config(
            "http://locahost:11434",
            "https://ollama.com/api/tags");
    }
}
