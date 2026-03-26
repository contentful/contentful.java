ARG JAVA_VERSION=8
FROM eclipse-temurin:${JAVA_VERSION}-jdk-jammy

RUN groupadd --gid 1000 vscode \
    && useradd --uid 1000 --gid 1000 --create-home --shell /bin/bash vscode \
    && apt-get update \
    && apt-get install -y --no-install-recommends curl git openssh-client \
    && rm -rf /var/lib/apt/lists/*
