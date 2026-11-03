#!/bin/sh
set -e

if [ -n "$SPRING_DATASOURCE_URL" ]; then
    case "$SPRING_DATASOURCE_URL" in
        postgres://*)
            export SPRING_DATASOURCE_URL="jdbc:postgresql://${SPRING_DATASOURCE_URL#postgres://}"
            ;;
        postgresql://*)
            export SPRING_DATASOURCE_URL="jdbc:postgresql://${SPRING_DATASOURCE_URL#postgresql://}"
            ;;
    esac
fi

if [ -n "$DATABASE_URL" ]; then
    case "$DATABASE_URL" in
        postgres://*)
            export DATABASE_URL="jdbc:postgresql://${DATABASE_URL#postgres://}"
            ;;
        postgresql://*)
            export DATABASE_URL="jdbc:postgresql://${DATABASE_URL#postgresql://}"
            ;;
    esac
fi

exec java -jar app.jar "$@"
