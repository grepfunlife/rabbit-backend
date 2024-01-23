FROM amazoncorretto:17 as builder
COPY . /app
WORKDIR /app
RUN ./gradlew build installDist

FROM amazoncorretto:17
COPY --from=builder /app/build/install/rabbit-backend /app
WORKDIR /app
CMD ["bin/rabbit-backend"]

##TODO env
##TODO port