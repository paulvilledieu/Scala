#!/bin/bash

# Set KAFKA_PATH if not in env
if [ -z "${KAFKA_PATH}" ]; then

    KAFKA_PATH=$(find ~ -type d -name "kafka_2*" | head -n 1)

    if [ -z "${KAFKA_PATH}" ]; then
        echo "Kafka not found"
        return 1
    fi
fi

export KAFKA_PATH

alias sd_consumer="$KAFKA_PATH/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic standard --from-beginning"

alias alert_consumer="$KAFKA_PATH/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic alert --from-beginning"

alias alert_producer="$KAFKA_PATH/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic alert"

alias sd_producer="$KAFKA_PATH/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic standard"
