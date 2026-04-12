package org.ollama.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import io.github.ollama4j.models.response.Model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A client that interacts with the Ollama web site.
 */
public class SiteClient {

    private static final Logger LOG = LoggerFactory.getLogger(SiteClient.class);

    private final String tagsUrl;
    private final HttpClient client;

    public SiteClient(Config config) {
        this(config.tags());
    }

    public SiteClient(String tagsUrl) {
        this.tagsUrl = tagsUrl;
        client = HttpClient.newHttpClient();
    }

    /**
     * This method returns a collection avialable models, that are hosted on
     * Ollama and can be downloaded. It is not interacting with the Ollama
     * service running a model.<br/>
     * Note that it does not load models which do not have a version.
     * A version comes after a ':' in the name. Some tags do not have this.
     *
     * @return A collection of available models.
     */
    public List<Model> getRemoteModels() {
        List<Model> models = Collections.emptyList();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tagsUrl))
                .GET()
                .build();

        try {
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
            record Response(List<Model> models) {}
            Response result = mapper.readValue(response.body(), Response.class);
            models = result.models();
        } catch (IOException | InterruptedException e) {
            LOG.warn(e.getMessage(), e);
            throw new IllegalStateException(e);
        }

        return removeMissingVersions(models);
    }

    /**
     * Filters out those models, which do not have a version number in th name.
     * @param models
     * @return Collection without elments that do't have a version
     */
    private List<Model> removeMissingVersions(List<Model> models) {
        return models.stream()
        .filter(m -> m.getName().contains(":"))
        .toList();
    }
}
