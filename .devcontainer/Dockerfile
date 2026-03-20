ARG JAVA_VERSION=8
FROM eclipse-temurin:${JAVA_VERSION}-jdk-jammy

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl git openssh-client \
    && rm -rf /var/lib/apt/lists/*
