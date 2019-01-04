#!/usr/bin/env bash

folder="../resources/$(basename $(echo $0) | cut -d'-' -f1)"
file="$(basename $0 | cut -d'-' -f2)"

mkdir --parent ${folder}

for id in $(curl \
        --silent \
        -H "Authorization: Bearer ${RICH_TEXT_DELIVERY_TOKEN}" \
        "https://cdn.contentful.com/spaces/${RICH_TEXT_SPACE_ID}/environments/human-readable/entries?content_type=rich&fields.name\[match\]=simple&select=sys.id" \
        | grep id | cut -d'"' -f4); do
    name="$(curl \
        --silent \
        -H "Authorization: Bearer ${RICH_TEXT_DELIVERY_TOKEN}" \
        "https://cdn.contentful.com/spaces/${RICH_TEXT_SPACE_ID}/environments/human-readable/entries?content_type=rich&sys.id=${id}&select=fields.name" \
        | grep name | cut -d'"' -f4)"
    output="$(echo ${folder}/${name}.json)"
    echo ${name}

    curl --silent \
        -H "Authorization: Bearer ${RICH_TEXT_DELIVERY_TOKEN}"  \
        "https://cdn.contentful.com/spaces/${RICH_TEXT_SPACE_ID}/environments/human-readable/entries?sys.id=${id}" \
        | sed 's/'${RICH_TEXT_SPACE_ID}'/<space_id>/g' \
        | sed 's/'${RICH_TEXT_DELIVERY_TOKEN}'/<access_token>/g' \
        | tee ${output}
done
