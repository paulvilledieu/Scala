#!/bin/sh

server="localhost:9092"

# Set KAFKA_PATH if not in env
if [ -z "${KAFKA_PATH}" ]; then

    KAFKA_PATH=$(find ~ -type d -name "kafka_2*" | head -n 1)

    if [ -z "${KAFKA_PATH}" ]; then
        echo "Kafka not found"
        exit 1
    fi
fi

echo "Kafka from: ${KAFKA_PATH}"

echo "Starting zookeeper"

"$KAFKA_PATH/bin/zookeeper-server-start.sh" config/zookeeper.properties &

sleep 2

echo "Starting kafka servers"

for config in $(ls config/server*.properties)
do
    "$KAFKA_PATH/bin/kafka-server-start.sh" "$config" &
done

sleep 2

while IFS=, read -r name replication partitions
do
    if [ "$partitions" = "partitions" ]; then
        continue # skip headers
    fi
    "$KAFKA_PATH/bin/kafka-topics.sh" --create \
        --bootstrap-server "$server" \
        --topic "$name" \
        --partitions "$partitions" \
        --replication-factor "$replication"
done < config/topics.csv

echo "Topic List:"
"$KAFKA_PATH/bin/kafka-topics.sh" --list --bootstrap-server "$server"
