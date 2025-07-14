# Imagen base con JDK 17 para Spring Boot
FROM eclipse-temurin:17-jdk-alpine

# Directorio de trabajo
WORKDIR /app

# Copiar archivo de configuración y build
COPY build.gradle /app/build.gradle
COPY application.yml /app/src/main/resources/application.yml

# Instalar Gradle y dependencias necesarias
RUN apk add --no-cache curl \
    && curl -s "https://get.sdkman.io" | bash \
    && source "$HOME/.sdkman/bin/sdkman-init.sh" \
    && sdk install gradle 8.7 \
    && gradle build -x test --no-daemon

# Copiar código fuente completo
COPY . /app

# Generar el archivo JAR del proyecto
RUN ./gradlew clean build -x test --no-daemon

# Exponer el puerto configurado en application.yml
EXPOSE 8081

# Ejecutar la aplicación
ENTRYPOINT ["java","-jar","/app/build/libs/servicioCatalogo-gradle-0.0.1-SNAPSHOT.jar"]
