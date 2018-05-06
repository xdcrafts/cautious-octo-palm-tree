#!/bin/sh

set -e

DRIVER='org.postgresql.Driver'
URL="jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_DATABASE}"
LIQUIBASE_CLASSPATH='/postgresql.jar'

liquibase \
    --logLevel=info \
    --driver=${DRIVER} \
    --classpath=${LIQUIBASE_CLASSPATH} \
    --changeLogFile="db.changelog.xml" \
    --url="${URL}" \
    --username=${DB_USERNAME} \
    --password=${DB_PASSWORD} \
    --databaseChangeLogTableName="octo_palm_liquibase_changelog" \
    --databaseChangeLogLockTableName="octo_palm_liquibase_changelog_loc" \
    --labels="${LABELS}" \
    clearCheckSums \
&& \
liquibase \
    --logLevel=info \
    --driver=${DRIVER} \
    --classpath=${LIQUIBASE_CLASSPATH} \
    --changeLogFile="db.changelog.xml" \
    --url="${URL}" \
    --username=${DB_USERNAME} \
    --password=${DB_PASSWORD} \
    --databaseChangeLogTableName="octo_palm_liquibase_changelog" \
    --databaseChangeLogLockTableName="octo_palm_liquibase_changelog_loc" \
    --labels="${LABELS}" \
    update
