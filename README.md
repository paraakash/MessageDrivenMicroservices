This is high performance horizontally scalable message driven architecture example 

Reader Service is a spring boot microservice and will read data from csv files from a folder and create json payload list of 10-20-30 records  (as configured parameter) 
and then this Json payload is send to Messaging engines like RabbitMQ using libraries like spring-boot-starter-amqp etc and send message 
to Q1

Processor 1 service will have multiple listeners to Q1 of Messaging engines like RabbitMQ. The number of listeners is a configurable parameter. 
Each listeners will receive the message from Q1 as json list and using Spring ThreadPoolTaskExecutor it will process each record in parallel. 
Once response of all records is received it will prepare response list json and send it to Messaging engines into new queue Q2.

Processor 2 service will have multiple listeners to Q2. Each listeners will receive the message from Q2 and write it into corresponding file.
Processor 2 service will write completion stats for each file into static concurrent hash map which can be displayed into 
web page using simple get request.
 
