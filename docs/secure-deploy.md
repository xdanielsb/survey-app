# Secure Production Deployment with Docker Compose (Non-Root User)

## 1. Create a Dedicated Deploy User

On the target server, create a new Unix user and restrict its permissions:

```bash
sudo useradd -m -s /bin/bash -U deploy
sudo usermod -aG docker deploy
```

### Set Up SSH Access

1. As root:

```bash
sudo mkdir -p /home/deploy/.ssh
sudo nano /home/deploy/.ssh/authorized_keys
```

2. Paste your **public SSH key**  

3. Secure the SSH setup:

```bash
sudo chmod 700 /home/deploy/.ssh
sudo chmod 600 /home/deploy/.ssh/authorized_keys
sudo chown -R deploy:deploy /home/deploy/.ssh
```

Then reload SSH:

```bash
sudo systemctl reload sshd
```

---

##  2. Prepare Application Directory

Create and secure the deployment directory:

```bash
sudo mkdir -p /home/deploy/survey-app
sudo chown -R deploy:deploy /home/deploy/survey-app
sudo chmod 750 /home/deploy/survey-app
```

---

Use **ed25519** key for stronger and faster SSH encryption.

After pushing your workflow changes:

- Run a test deployment
- SSH in and verify:

```bash
whoami
docker ps
```
