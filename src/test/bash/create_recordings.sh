#!/usr/bin/env bash
echo $0


for i in *.sh; do
    if [ "$(basename $0)" != "$i" ]; then
        echo "Executing $i"
        ./${i}
    fi
done
