FROM openjdk:8-jre-alpine

RUN wget -O liquibase.tar.gz https://github.com/liquibase/liquibase/releases/download/liquibase-parent-3.5.3/liquibase-3.5.3-bin.tar.gz
RUN wget -O postgresql.jar https://jdbc.postgresql.org/download/postgresql-42.2.1.jar

RUN apk add --no-cache bash && mkdir liquibase && cd liquibase && tar -zxf ../liquibase.tar.gz && rm ../liquibase.tar.gz && ln -s /liquibase/liquibase /usr/local/bin/liquibase

COPY ./changes /changes
COPY ./db.changelog.xml /db.changelog.xml
COPY ./run.sh /run.sh
COPY ./wait-for-it.sh /wait-for-it.sh

# Configuration environment variables:

# database host name
# DB_HOST

# database port
# DB_PORT

# database name
# DB_DATABASE

# database username
# DB_USERNAME

# database password
# DB_PASSWORD

# Specify what dictionaries should be included/excluded while migration
# LABELS

CMD ./wait-for-it.sh ${DB_HOST}:${DB_PORT} && ./run.sh
