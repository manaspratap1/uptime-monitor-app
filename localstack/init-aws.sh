#!/bin/bash

echo "Creating SQS queues..."

awslocal sqs create-queue \
  --queue-name uptime-check-dlq

awslocal sqs create-queue \
  --queue-name uptime-check-queue

echo "Creating queue redrive policy..."

DLQ_URL=$(awslocal sqs get-queue-url \
  --queue-name uptime-check-dlq \
  --query QueueUrl \
  --output text)

DLQ_ARN=$(awslocal sqs get-queue-attributes \
  --queue-url "$DLQ_URL" \
  --attribute-names QueueArn \
  --query 'Attributes.QueueArn' \
  --output text)

QUEUE_URL=$(awslocal sqs get-queue-url \
  --queue-name uptime-check-queue \
  --query QueueUrl \
  --output text)

awslocal sqs set-queue-attributes \
  --queue-url "$QUEUE_URL" \
  --attributes "{\"RedrivePolicy\":\"{\\\"deadLetterTargetArn\\\":\\\"$DLQ_ARN\\\",\\\"maxReceiveCount\\\":\\\"3\\\"}\"}"

echo "SQS setup complete."