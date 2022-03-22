#!/bin/sh

if [ $# -eq 0 ]
  then
    echo "Please insert copy path in the first argument. example ./prepare_generate_response ./bdit/graphql/*.graphql"
fi

rm src/commonMain/graphql/query/*.graphql
cp $1 src/commonMain/graphql/query/
