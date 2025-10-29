FROM eclipse-temurin:21-jdk-jammy as builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B # Baixa as dependências
COPY src ./src
RUN ./mvnw package -DskipTests # Compila e empacota o JAR, pulando os testes

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar 
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]