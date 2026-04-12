package org.ollama.client;

import java.util.List;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import io.github.ollama4j.models.response.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class SiteClientTest {

    private static final String URL = "https://ollama.com/api/tags";

    private static final String BODY = """
    {
        "models": [
            {
                "name": "gpt-oss:120b",
                "model": "gpt-oss:120b",
                "modified_at": "2025-08-05T00:00:00Z",
                "size": 65290180781,
                "digest": "d98fe6ba01e6",
                "details": {}
            },
            {
                "name": "gpt-oss:20b",
                "model": "gpt-oss:20b",
                "modified_at": "2025-08-05T00:00:00Z",
                "size": 13780162412,
                "digest": "05afbac4bad6",
                "details": {}
            },
            {
                "name": "minimax-m2.1",
                "model": "minimax-m2.1",
                "modified_at": "2025-12-20T00:00:00Z",
                "size": 230000000000,
                "digest": "a7c3e1f98b2d",
                "details": {}
            }
        ]
    }""";

    @Mock
    private HttpClient client;

    @Mock
    private HttpResponse<String> response;

    private SiteClient classUnderTest;

    @BeforeEach
    void setUp() {
        classUnderTest = new SiteClient(URL, client);
    }

    @Test
    @DisplayName("It should return a ordered list of models")
    void getModels() throws Exception {
        when(client.send(any(HttpRequest.class), any())).thenReturn((HttpResponse) response);

        when(response.body()).thenReturn(BODY);

        List<Model> models = classUnderTest.getModels();

        assertEquals(models.size(), 2);
    }
}
