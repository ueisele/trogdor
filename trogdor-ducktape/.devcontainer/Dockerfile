FROM continuumio/miniconda3

# Verify git, needed tools installed
# Unminimize makes man pages available (see https://github.com/tianon/docker-brew-ubuntu-core/issues/122)
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update \
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
RUN ln -sf /bin/zsh /bin/sh

# Set entrypoint
COPY .devcontainer/entrypoint.sh /usr/local/bin/entrypoint.sh
ENTRYPOINT [ "/usr/bin/tini", "--", "/usr/local/bin/entrypoint.sh" ]
CMD [ "/bin/zsh" ]

# Create dev user
ARG USER_NAME="dev"
ARG USER_ID=1000
ARG GROUP_ID=1000
RUN set -o errexit -o nounset \
    && echo "Adding ${USER_NAME} user and group" \
    && groupadd --system --gid ${GROUP_ID} ${USER_NAME} \
    && useradd --system --gid ${USER_NAME} --uid ${USER_ID} --shell /bin/bash --create-home ${USER_NAME} \
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

# Install and configure Oh-My-Zsh
ARG ZSH_THEME=blinks
RUN wget https://github.com/robbyrussell/oh-my-zsh/raw/master/tools/install.sh -O - | zsh \
    && sed -r -i "s/(ZSH_THEME=\")([^\"]+)(\")/\1${ZSH_THEME}\3/" /home/${USER_NAME}/.zshrc

# Add git provider to known hosts
ARG GIT_PROVIDER=github.com
RUN ssh-keyscan -H ${GIT_PROVIDER} >> /home/${USER_NAME}/.ssh/known_hosts

# Install Python dependencies from environment.yml if it exists
COPY environment.yml* /home/${USER_NAME}
#RUN if [ -f "environment.yml" ]; then conda env update base -f environment.yml && rm environment.yml*; fi
RUN if [ -f "environment.yml" ]; then conda env create -f environment.yml && rm environment.yml*; fi
# Activate environment
ARG CONDA_ENV=trogdor-ducktape
RUN for file in /home/${USER_NAME}/.zshrc /home/${USER_NAME}/.bashrc; do \
    echo -e "\n# Activate conda environment\n. /opt/conda/etc/profile.d/conda.sh\nconda activate ${CONDA_ENV}" >> $file \
    ;done
