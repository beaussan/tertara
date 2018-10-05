# Tertara

[![CircleCI](https://circleci.com/gh/beaussart/tertara.svg?style=svg)](https://circleci.com/gh/beaussart/tertara)

# Instalation

```bash
# Install maven deps
mvn dependency

# Run the psql docker
docker-compose -f src/main/docker/postgresql.yml up -d
```

# Run

```bash
nvm spring-boot:run
```

# Package and test

```bash
mvn package
```