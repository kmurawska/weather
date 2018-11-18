Example project with kafaka and cassandra based on JEE 8.

* Start consumer:
  
 	 ```
	docker exec -it kafka1 bash /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server kafka1:9091 --topic current-weather --from-beginning
	 ```




Consumer:

- isolation.level=read_committed - In read_committed mode, the consumer will read only those transactional messages which have been successfully committed. It will continue to read non-transactional messages as before
