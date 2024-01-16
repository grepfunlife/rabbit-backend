FROM amazoncorretto:17
ADD . /app
RUN cd /app/ && ./gradlew build installDist
#TODO multi-stage build
#TODO env
#TODO port
CMD "/app/build/install/rabbit-backend/bin/rabbit-backend"