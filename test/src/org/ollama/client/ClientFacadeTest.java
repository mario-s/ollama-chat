package org.ollama.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class ClientFacadeTest {
    private static final String NAME = "foo";

    @Mock
    private ApiClient apiClient;

    @Mock
    private SiteClient siteClient;

    @Mock
    private Chat mockChat;

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

    @Test
    @DisplayName("It should set the selected model and create chat on first call")
    void setChatModel() {
        classUnderTest.setChatModel(NAME);

        verify(apiClient).createChat(NAME);
    }

    @Test
    @DisplayName("It should update model on existing chat when model changes")
    void setChatModel_ExistingChat() {
        when(apiClient.createChat(NAME)).thenReturn(mockChat);

        classUnderTest.setChatModel(NAME);
        classUnderTest.setChatModel("newModel");

        verify(mockChat).setModel("newModel");
    }

    @Test
    @DisplayName("It should throw exception when setting null model")
    void setChatModel_Null() {
        assertThrows(IllegalArgumentException.class,
            () -> classUnderTest.setChatModel(null));
    }

    @Test
    @DisplayName("It should throw exception when setting blank model")
    void setChatModel_Blank() {
        assertThrows(IllegalArgumentException.class,
            () -> classUnderTest.setChatModel(""));
    }

    @Test
    @DisplayName("It should throw exception when chatting without selected model")
    void chat_NoModel() {
        assertThrows(IllegalStateException.class,
            () -> classUnderTest.chat("question"));
    }

    @Test
    @DisplayName("It should use selected model for chat")
    void chat_SelectedModel() {
        when(apiClient.createChat(NAME)).thenReturn(mockChat);
        when(mockChat.chat("question")).thenReturn("answer");

        classUnderTest.setChatModel(NAME);
        String result = classUnderTest.chat("question");

        assertEquals("answer", result);
        verify(mockChat).chat("question");
    }
}
