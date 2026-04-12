#!/bin/bash
table_name="Asset"
hash_key="symbol"

awslocal dynamodb create-table \
                --table-name "$table_name" \
                --key-schema AttributeName="$hash_key",KeyType=HASH \
                --attribute-definitions AttributeName="$hash_key",AttributeType=S \
                --billing-mode PAY_PER_REQUEST

echo "DynamoDB table '$table_name' created successfully with hash key '$hash_key'"

table_name="Order"
hash_key="orderId"
gsi_hash_key="userId"
gsi_range_key="status"

awslocal dynamodb create-table \
               --table-name "$table_name" \
               --attribute-definitions AttributeName="$hash_key",AttributeType=S \
                                        AttributeName="$gsi_hash_key",AttributeType=S \
                                        AttributeName="$gsi_range_key",AttributeType=S \
               --key-schema AttributeName="$hash_key",KeyType=HASH \
               --provisioned-throughput ReadCapacityUnits=10,WriteCapacityUnits=5 \
               --global-secondary-indexes \
                               "[
                                   {
                                     \"IndexName\": \"statusIndex\",
                                     \"KeySchema\": [{\"AttributeName\":\"$gsi_hash_key\",\"KeyType\":\"HASH\"},
                                                    {\"AttributeName\":\"$gsi_range_key\",\"KeyType\":\"RANGE\"}],
                                     \"Projection\": {
                                                       \"ProjectionType\":\"ALL\"
                                                     },
                                     \"ProvisionedThroughput\": {
                                                         \"ReadCapacityUnits\": 10,
                                                         \"WriteCapacityUnits\": 5
                                                         }
                                   }
                               ]"

echo "DynamoDB table '$table_name' created successfully with hash key '$hash_key'"

table_name="User"
hash_key="userId"
awslocal dynamodb create-table \
                --table-name "$table_name" \
                --key-schema AttributeName="$hash_key",KeyType=HASH \
                --attribute-definitions AttributeName="$hash_key",AttributeType=S \
                --billing-mode PAY_PER_REQUEST

echo "DynamoDB table '$table_name' created successfully with hash key '$hash_key'"
echo "Executed tables.sh script"