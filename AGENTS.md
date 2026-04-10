# OpenCode Agent Guide for ollama-chat

This repository uses the Mill build tool for project management, building, testing, and running.

## Build & Test Workflow
- **Command:** `./mill test --testFrameworkArgs "--display-mode=tree"`
- **Description:** Runs all unit and integration tests using JUnit 6.
- **Prerequisites:** Requires Java 25.

## Core Developer Commands
- **Build:** `./mill build`
- **Run:** `./mill run`

## System/Architecture Notes
- **Language/Toolchain:** Requires Java 25.
- **Build Config:** The main `build.mill.yaml` dictates the build process, including dependencies like `ollama4j` and Jackson for JSON serialization.
- **Initialization:** Check `README.md` for general setup, noting the requirement for an accessible Ollama instance.