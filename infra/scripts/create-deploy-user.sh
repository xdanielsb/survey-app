#!/bin/bash

# Check if a username is provided
if [ -z "$1" ]; then
  echo "Usage: $0 <username>"
  exit 1
fi

USERNAME="$1"
USER_HOME="/home/$USERNAME"

# Check if user already exists
if id "$USERNAME" &>/dev/null; then
  echo "User '$USERNAME' already exists."
  exit 1
fi

# Create the user with home directory and bash shell
sudo useradd -m -s /bin/bash -U "$USERNAME"

# Add user to docker group (if docker group exists)
if getent group docker > /dev/null; then
  sudo usermod -aG docker "$USERNAME"
else
  echo "Warning: 'docker' group does not exist. Skipping Docker group assignment."
fi

# Create .ssh directory
sudo mkdir -p "$USER_HOME/.ssh"

# Copy authorized_keys from current user (if exists)
if [ -f "$HOME/.ssh/authorized_keys" ]; then
  sudo cp "$HOME/.ssh/authorized_keys" "$USER_HOME/.ssh/authorized_keys"
else
  echo "No authorized_keys found in current user's ~/.ssh directory. Creating an empty one."
  sudo touch "$USER_HOME/.ssh/authorized_keys"
fi

# Set permissions
sudo chmod 700 "$USER_HOME/.ssh"
sudo chmod 600 "$USER_HOME/.ssh/authorized_keys"

# Set correct ownership
sudo chown -R "$USERNAME:$USERNAME" "$USER_HOME/.ssh"

echo "User '$USERNAME' created successfully with SSH access and optional Docker permissions."
