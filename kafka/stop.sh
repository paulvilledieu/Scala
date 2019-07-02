#!/bin/bash

if [ -z "${KAFKA_PATH}" ]; then

    KAFKA_PATH=$(find ~ -type d -name "kafka_2*" | head -n 1)

    if [ -z "${KAFKA_PATH}" ]; then
        echo "Kafka not found"
        exit 1
    fi
fi

"$KAFKA_PATH/bin/kafka-server-stop.sh"
"$KAFKA_PATH/bin/zookeeper-server-stop.sh"
