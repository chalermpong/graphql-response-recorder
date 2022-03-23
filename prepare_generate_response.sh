#!/bin/sh

if [ $# -eq 0 ]
  then
    echo "Please insert graphql folder path to the first argument. Example: ./prepare_generate_response ../bdit-ios/Core/Core/GraphQL/"
fi

rm -r src/commonMain/graphql/query
cp -r $1 src/commonMain/graphql/query