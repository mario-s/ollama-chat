package org.ollama.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ollama.client.ApiClient;
import org.ollama.client.SiteClient;

import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
class ClientFacadeTest {
    private static final String NAME = "foo";

    @Mock
    private Frame frame;

    @Mock
    private ApiClient apiClient;

    @Mock
    private SiteClient siteClient;

    private ClientFacade classUnderTest;

    @BeforeEach
    void setUp() {
        classUnderTest = new ClientFacade(frame, apiClient, siteClient);
    }

    @Test
    @DisplayName("It should use the ApiClient to create a chat")
    void createChat() {
        classUnderTest.createChat(NAME);

        verify(apiClient).createChat(NAME);
    }

    @Test
    @DisplayName("It should use the ApiClient to pull a model")
    void pullModelSync() throws Exception {
        classUnderTest.pullModelSync(NAME);

        verify(apiClient).pullModel(NAME);
    }
}
