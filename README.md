  * Start consumer:
  
 	 ```
	docker exec -it kafka1 bash /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server kafka1:9091 --topic temperature --from-beginning
	 ```