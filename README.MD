# Spring Boot GitHub API

## Links

- Local: http://localhost:8080/swagger-ui/index.html

## Getting Started
### Localhost
1. Need to set the `app.github.api_token` in
   the [application.properties](src%2Fmain%2Fresources%2Fapplication.properties). You can generate your GitHub token 
by following [this guide](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)
2. Run SpringBoot application with Maven: `./mvnw spring-boot:run`
3. Check the API documentation: http://localhost:8080/swagger-ui/index.html

### Docker
1. Build docker image: `docker build -t springboot-github-api .`
2. Create container:\
```docker run -e APP_GITHUB_API_TOKEN=<Your Github Token> -p 8081:8080 springboot-github-api```
3. Check the API documentation: http://localhost:8081/swagger-ui/index.html