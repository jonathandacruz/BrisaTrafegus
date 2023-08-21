#!/bin/bash

aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name ml-document-input-queue
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name ml-document-output-queue
