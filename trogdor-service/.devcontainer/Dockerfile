FROM adoptopenjdk:11-jdk-hotspot

ENV GRADLE_VERSION 5.4.1
ARG GRADLE_DOWNLOAD_SHA256=7bdbad1e4f54f13c8a78abc00c26d44dd8709d4aedb704d913fb1bb78ac025dc
ARG GRADLE_DOWNLOAD_URL="https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"

# Allow for a consistant java home location for settings - image is changing over time
RUN if [ ! -d "/docker-java-home" ]; then ln -s "${JAVA_HOME}" /docker-java-home; fi

# Verify git, needed tools installed
# Unminimize makes man pages available (see https://github.com/tianon/docker-brew-ubuntu-core/issues/122)
ENV DEBIAN_FRONTEND=noninteractive
RUN yes | unminimize \
    && apt-get update \
    && apt-get -y install --no-install-recommends \
        man-db \
        zsh \
        sudo \
        apt-utils \
        fontconfig \
        vim \
        unzip \
        bzr \
        curl \
        wget \
        openssh-client \
        ssh-askpass \
        git \
        procps 2>&1 \
    && apt-get autoremove -y \
    && apt-get clean -y \
    && rm -rf /var/lib/apt/lists/*
ENV DEBIAN_FRONTEND=dialog

# Set the default shell to zsh instead of sh
ENV SHELL /usr/bin/zsh

# Create dev user
ARG USER_NAME="dev"
ARG USER_ID=1000
ARG GROUP_ID=1000
RUN set -o errexit -o nounset \
    && echo "Adding ${USER_NAME} user and group" \
    && groupadd --system --gid ${GROUP_ID} ${USER_NAME} \
    && useradd --system --gid ${USER_NAME} --uid ${USER_ID} --shell /bin/bash --create-home ${USER_NAME} \
    && mkdir -p /home/${USER_NAME}/.local/bin \
    && mkdir -p /home/${USER_NAME}/.ssh \
    && chown --recursive ${USER_NAME}:${USER_NAME} /home/${USER_NAME}
WORKDIR /home/${USER_NAME}

# Add sudo support
RUN echo ${USER_NAME} ALL=\(root\) NOPASSWD:ALL > /etc/sudoers.d/${USER_NAME} \
    && chmod 0440 /etc/sudoers.d/${USER_NAME}

# Install and configure gpg2
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update \
    && apt-get -y install --no-install-recommends gnupg2 2>&1 \
    && apt-get autoremove -y \
    && apt-get clean -y \
    && rm -rf /var/lib/apt/lists/*
ENV DEBIAN_FRONTEND=dialog
RUN set -o errexit -o nounset \
    && echo "Adding /run/user/${USER_ID}/" \
    && mkdir -p /run/user/${USER_ID} \
    && chown --recursive ${USER_NAME}:${USER_NAME} /run/user/${USER_ID} && chmod 700 /run/user/${USER_ID} \
    && echo "Adding /home/${USER_NAME}/.gnupg/" \
    && mkdir -p /home/${USER_NAME}/.gnupg \
    && chown --recursive ${USER_NAME}:${USER_NAME} /home/${USER_NAME}/.gnupg && chmod 700 /home/${USER_NAME}/.gnupg

# Activate dev user
USER ${USER_NAME}
ENV HOME /home/${USER_NAME}
ENV PATH="/home/${USER_NAME}/.local/bin:${PATH}"

# Install and configure Oh-My-Zsh
ARG ZSH_THEME=blinks
RUN wget https://github.com/robbyrussell/oh-my-zsh/raw/master/tools/install.sh -O - | zsh \
    && sed -r -i "s/(ZSH_THEME=\")([^\"]+)(\")/\1${ZSH_THEME}\3/" /home/${USER_NAME}/.zshrc

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