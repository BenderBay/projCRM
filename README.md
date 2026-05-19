

# How to get API docs:

mvn clean package -Pdocker
docker compose up --build
docker compose down

OR

./mvnw clean package -Pdocker

# Check following URLs in your browser
http://localhost:8080/swagger-ui.html
http://localhost:8080/v3/api-docs
http://localhost:8080/v3/api-docs.yaml

# Prerequirements
- Docker env
