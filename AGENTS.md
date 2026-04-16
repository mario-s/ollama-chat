# AGENTS.md

This file provides guidance to agents when working with code in this repository.

## Non-Obvious Project Specifics

### Config Loading Priority (ConfigLoader.java)
- Config files checked in order: `~/.ollama-chat/ollama.yaml` → `./ollama.yaml` → defaults
- MCP tools loaded from `jsonPath` in config only if `mcp.use: true`
- Tools must exist at path or silently fail with warning (no error thrown)

### Test Execution
- Single test: `./mill test --testFrameworkArgs "--select-class=org.ollama.client.ApiClientTest"`
- Test module in `test/` directory extends `build.JavaTests` and `TestModule.Junit6`
- Uses JUnit 6.0.2 (not Jupiter 5.x) with Mockito 5.23.0

### Chat State Management
- `Chat` class maintains conversation history via `chatResult.getChatHistory()`
- History automatically included in subsequent requests (stateful within Chat instance)
- Each Chat instance is model-specific; switching models requires new Chat instance

### UI Threading
- All Ollama API calls wrapped in `SwingWorker` with 60-second timeout
- Timeout cancels worker but doesn't throw exception to UI
- Glass pane (`WaitPanel`) used for blocking UI during operations

### Model List Sorting
- `ApiClient.getModels()` returns models sorted case-insensitively by name
- Not sorted by Ollama API; sorting done client-side