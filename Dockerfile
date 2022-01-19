FROM openjdk:11

VOLUME /candlestick
WORKDIR /candlestick

COPY . /candlestick

RUN ./gradlew build

CMD ./gradlew run