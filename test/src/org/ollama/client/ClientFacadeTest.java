package org.ollama.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
class ClientFacadeTest {
    private static final String NAME = "foo";

    @Mock
    private ApiClient apiClient;

    @Mock
    private SiteClient siteClient;

    private ClientFacade classUnderTest;

    @BeforeEach
    void setUp() {
        classUnderTest = new ClientFacade(apiClient, siteClient);
    }

    @Test
    @DisplayName("It should use the ApiClient to create a chat")
    void createChat() {
        classUnderTest.getChat(NAME);

        verify(apiClient).createChat(NAME);
    }

    @Test
    @DisplayName("It should use the ApiClient to pull a model")
    void pullModel() throws Exception {
        classUnderTest.pullModel(NAME);

        verify(apiClient).pullModel(NAME);
    }

    @Test
    @DisplayName("It should use the ApiClient to load local models")
    void getLocalModels() throws Exception {
        classUnderTest.getLocalModels();

        verify(apiClient).getModels();
    }

    @Test
    @DisplayName("It should use the SiteClient to load models from ollama.com")
    void getRemoteModels() throws Exception {
        classUnderTest.getRemoteModels();

        verify(siteClient).getModels();
    }
}
