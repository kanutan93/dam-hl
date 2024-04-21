import json
import logging
import os

from kafka import KafkaConsumer, KafkaProducer
from torchvision.io import read_image
from torchvision.models import resnet50, ResNet50_Weights

logging.basicConfig(level = logging.INFO)

KAFKA_BOOTSTRAP_SERVERS = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:29091")
KAFKA_CONSUMER_GROUP_ID = os.getenv("KAFKA_CONSUMER_GROUP_ID", "image-processing-service")
LISTEN_ADDING_NEW_IMAGE_TOPIC = os.getenv("LISTEN_ADDING_NEW_IMAGE_TOPIC", "adding-new-image-topic")
PRODUCE_SAVING_NEW_IMAGE_CATEGORY_TOPIC = os.getenv("PRODUCE_SAVING_NEW_IMAGE_CATEGORY_TOPIC", "saving-new-image-category-topic")
IMAGE_DIRECTORY = os.getenv("IMAGE_DIRECTORY","/tmp/images")

consumer = KafkaConsumer(
    bootstrap_servers=[KAFKA_BOOTSTRAP_SERVERS],
    group_id=KAFKA_CONSUMER_GROUP_ID,
    auto_offset_reset='latest'
)
producer = KafkaProducer(
    bootstrap_servers = [KAFKA_BOOTSTRAP_SERVERS],
    batch_size=0,
    value_serializer=lambda m: json.dumps(m).encode('utf-8')
)
weights = ResNet50_Weights.DEFAULT
model = resnet50(weights=weights)
model.eval()

def process_image(record_value):
    image_payload = json.loads(record_value)
    id = image_payload['id']
    filename = image_payload['filename']
    if id:
        image_with_category_payload = {}
        try:
            logging.info(f"Trying to handle new ImagePayload with id: {id} and {filename}")
            img = read_image(f"{IMAGE_DIRECTORY}/{filename}")

            # Step 1: Initialize the inference transforms
            preprocess = weights.transforms()

            # Step 2: Apply inference preprocessing transforms
            batch = preprocess(img).unsqueeze(0)

            # Step 3: Use the model and print the predicted category
            prediction = model(batch).squeeze(0).softmax(0)
            class_id = prediction.argmax().item()
            score = prediction[class_id].item()
            category_name = weights.meta["categories"][class_id]
            image_with_category_payload = {
                'id': id,
                'category': category_name,
                'categoryMatchResult': 100 * score,
                'action': 'UPDATE'
            }
            logging.info(f"ImagePayload with id: {id} and {filename} has been handled successfully with result "
                  f"{category_name}: {100 * score:.1f}%")
        except Exception as e:
            logging.error(f"Error on handling ImagePayload with id: {id} and {filename}")
            logging.error(e)
            image_with_category_payload = {
                'id': id,
                'action': 'DELETE'
            }
        # Step 4: Send ImageWithCategoryPayload to commit or rollback in SAGA
        producer.send(PRODUCE_SAVING_NEW_IMAGE_CATEGORY_TOPIC, image_with_category_payload)

if __name__ == "__main__":
    try:
        # Subscribe to a specific topics
        consumer.subscribe([LISTEN_ADDING_NEW_IMAGE_TOPIC])
        logging.info("Kafka consumer has been successfully started")
        logging.info(f"Listening bootstrap_servers: {KAFKA_BOOTSTRAP_SERVERS}, topics: {LISTEN_ADDING_NEW_IMAGE_TOPIC}")
        # Poll for new message
        while True:
            msg = consumer.poll(timeout_ms=1000, max_records=1)
            if msg:
                for record in msg.values():
                    record_value = record[0].value.decode("utf-8")
                    process_image(record_value)
    except Exception as e:
        logging.error('Error while consuming new message')
        logging.error(e)