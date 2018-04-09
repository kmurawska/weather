FROM cassandra:3.11.2
RUN apt-get update \
    && apt-get install -y netcat
ADD state-service.sh /
ADD docker-entrypoint-wrapper.sh /
ADD wait-for-cassandra.sh /
ENTRYPOINT ["/docker-entrypoint-wrapper.sh"]