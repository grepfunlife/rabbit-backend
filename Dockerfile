FROM amazoncorretto:17 as builder
WORKDIR /app
COPY gradlew /app/
RUN chmod +x /app/gradlew
COPY build.gradle.kts settings.gradle.kts gradle.properties /app/
COPY gradle /app/gradle
RUN ./gradlew dependencies
COPY . /app
RUN /app/gradlew build installDist

FROM amazoncorretto:17
COPY --from=builder /app/build/install/rabbit-backend /app
WORKDIR /app
CMD ["bin/rabbit-backend"]
##TODO env
##TODO port