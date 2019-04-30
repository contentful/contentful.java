#!/usr/bin/env bash

folder="../resources/$(basename $(echo $0) | cut -d'-' -f1)"
file="$(basename $0 | cut -d'-' -f2)"
output="$(echo ${folder}/${file} | sed 's#sh#json#g')"

mkdir --parent ${folder}
touch ${output}

curl --verbose \
    -H 'Authorization: Bearer '${RICH_TEXT_DELIVERY_TOKEN}  \
    'https://cdn.contentful.com/spaces/'${RICH_TEXT_SPACE_ID}'/environments/human-readable/sync?initial=true' \
    | sed 's/'${RICH_TEXT_SPACE_ID}'/<space_id>/g' \
    | sed 's/'${RICH_TEXT_DELIVERY_TOKEN}'/<access_token>/g' \
    | tee ${output}
