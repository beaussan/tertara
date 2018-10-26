# Tertara

[![CircleCI](https://circleci.com/gh/beaussart/tertara.svg?style=svg)](https://circleci.com/gh/beaussart/tertara)
[![codecov](https://codecov.io/gh/beaussart/tertara/branch/master/graph/badge.svg)](https://codecov.io/gh/beaussart/tertara)

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

# For running test and sonar

To start the sonar docker :

```bash
docker-compose -f src/main/docker/sonar.yml up -d
```

To run test and a sonar report :

```bash
mvn clean test sonar:sonar -Dsonar.host.url=http://localhost:9001  
```

