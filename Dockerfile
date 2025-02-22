FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/target/bot.jar /app/app.jar
RUN mkdir -p /app/config && \
    echo "bot.token=${BOT_TOKEN}\nbot.prefix=!\nbot.activity=with music\nspotify.client.id=${SPOTIFY_CLIENT_ID}\nspotify.client.secret=${SPOTIFY_CLIENT_SECRET}\nspotify.redirect.uri=${SPOTIFY_REDIRECT_URI}" > /app/config/config.properties

ENV BOT_TOKEN=${BOT_TOKEN} \
    SPOTIFY_CLIENT_ID=${SPOTIFY_CLIENT_ID} \
    SPOTIFY_CLIENT_SECRET=${SPOTIFY_CLIENT_SECRET} \
    SPOTIFY_REDIRECT_URI=${SPOTIFY_REDIRECT_URI} \
    PORT=8080 \
    CONFIG_PATH=/app/config

EXPOSE 8080
CMD ["java", "-Dconfig.path=/app/config", "-jar", "/app/app.jar"]
