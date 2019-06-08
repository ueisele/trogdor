FROM adoptopenjdk:11-jdk-hotspot

ENV GRADLE_VERSION 5.4.1
ARG GRADLE_DOWNLOAD_SHA256=7bdbad1e4f54f13c8a78abc00c26d44dd8709d4aedb704d913fb1bb78ac025dc
ARG GRADLE_DOWNLOAD_URL="https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"

# Set the default shell to bash instead of sh
ENV SHELL /bin/bash

# Allow for a consistant java home location for settings - image is changing over time
RUN if [ ! -d "/docker-java-home" ]; then ln -s "${JAVA_HOME}" /docker-java-home; fi

# Verify git, needed tools installed
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update \
    && apt-get -y install --no-install-recommends \
        apt-utils \
        fontconfig \
        unzip \
        bzr \
        curl \
        wget \
        openssh-client \
        git \
        procps 2>&1 \
    && apt-get autoremove -y \
    && apt-get clean -y \
    && rm -rf /var/lib/apt/lists/*
ENV DEBIAN_FRONTEND=dialog

# Create dev user
ARG USER_NAME="dev"
ARG USER_ID=1000
ARG GROUP_ID=1000
RUN set -o errexit -o nounset \
    && echo "Adding ${USER_NAME} user and group" \
    && groupadd --system --gid ${GROUP_ID} ${USER_NAME} \
    && useradd --system --gid ${USER_NAME} --uid ${USER_ID} --shell /bin/bash --create-home ${USER_NAME} \
    && mkdir -p /home/${USER_NAME}/.local/bin \
    && chown --recursive ${USER_NAME}:${USER_NAME} /home/${USER_NAME}
WORKDIR /home/${USER_NAME}

# Activate dev user
USER ${USER_NAME}
ENV HOME /home/${USER_NAME}
ENV PATH="/home/${USER_NAME}/.local/bin:${PATH}"

# Install Gradle
ENV GRADLE_HOME /home/${USER_NAME}/.gradle
RUN set -o errexit -o nounset \
    && echo "Downloading Gradle" \
    && wget --no-verbose --output-document=gradle.zip "${GRADLE_DOWNLOAD_URL}" \
    \
    && echo "Checking download hash" \
    && echo "${GRADLE_DOWNLOAD_SHA256} *gradle.zip" | sha256sum --check - \
    \
    && echo "Installing Gradle" \
    && unzip gradle.zip \
    && rm gradle.zip \
    && mv "gradle-${GRADLE_VERSION}" "${GRADLE_HOME}/" \
    && ln --symbolic "${GRADLE_HOME}/bin/gradle" /home/${USER_NAME}/.local/bin/gradle \
    && mkdir -p "${GRADLE_HOME}/caches" && mkdir -p "${GRADLE_HOME}/wrapper" \
    \
    && echo "Configuring Gradle" \
    && echo "org.gradle.jvmargs=-Dfile.encoding=UTF-8" >> "${GRADLE_HOME}/gradle.properties" \
    \
    && echo "Testing Gradle installation" \
    && gradle --version