FROM amazoncorretto:17 as builder
COPY build.gradle settings.gradle /app/
COPY gradle /app/gradle
RUN cd /app && ./gradlew dependencies
COPY . /app
RUN cd /app && ./gradlew build installDist

FROM amazoncorretto:17
COPY --from=builder /app/build/install/rabbit-backend /app
WORKDIR /app
CMD ["bin/rabbit-backend"]
##TODO env
##TODO port