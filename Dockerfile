# Etapa 1: Build com Maven
FROM maven:3.8.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Cache de dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia código e faz o build
COPY src ./src
RUN mvn clean package -DskipTests -DfinalName=app

# Etapa 2: Execução
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app


# Verifica se o JAR existe
RUN ls -l /app

# Cria usuário seguro (obrigatório no Fly.io)
RUN addgroup -S javauser && adduser -S -G javauser javauser \
    && chown -R javauser:javauser /app
USER javauser

# Copia o JAR com nome fixo
COPY --from=build /app/target/app.jar .

# Configurações essenciais
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]