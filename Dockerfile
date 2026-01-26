# Etap 1: Budowanie aplikacji
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Kopiowanie pom.xml i pobieranie zależności
COPY pom.xml .
RUN mvn dependency:go-offline

# Kopiowanie źródeł i budowanie paczki
COPY src ./src
RUN mvn clean package -DskipTests

# Etap 2: Obraz uruchomieniowy
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Kopiowanie zbudowanego jara z pierwszego etapu
COPY --from=build /app/target/*.jar app.jar

# Katalog na przesyłane pliki (plakaty)
RUN mkdir -p uploads/posters

# Uruchomienie aplikacji
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
