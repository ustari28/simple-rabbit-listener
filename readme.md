# Simple Rabbit Listener
## Run
Environment vars for start the client.
````
rabbit.host=localhost #If you use TLS you need to use a host that match with the wildcard of the server
rabbit.port=5671|5672 #Depends on the port you had set up in your server
rabbit.username=guest
rabbit.password=guest
rabbit.ssl=TLSv1.2 #If you don't pass the rabbit.ssl you will use unsecure port.
rabbit.keystore.name=file:/PATH_TO_FILE.p12
rabbit.keystore.password=PASS_KEYSTORE
rabbit.truststore=file:/PATH_TO_FILE.jks
rabbit.truststore.password=PASS_TRUSTSTORE
````