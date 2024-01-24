FROM amazoncorretto:17 as builder
WORKDIR /app
COPY gradlew /app/
RUN chmod +x /app/gradlew
COPY build.gradle.kts settings.gradle.kts /app/
COPY gradle /app/gradle
ARG KTOR_VERSION=2.3.7
COPY . /app
RUN /app/gradlew build installDist -PktorVersion=$KTOR_VERSION

FROM amazoncorretto:17
COPY --from=builder /app/build/install/rabbit-backend /app
WORKDIR /app
CMD ["bin/rabbit-backend"]
##TODO env
##TODO port