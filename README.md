# Device API

Simple scala web server to catalogue devices. Implemented entirely in a functional manner, using cats and http4s.


To run the app: `sbt run`

Example commands (GET, POST, PUT, DELETE):

curl -i http://localhost:8080/devices/123

curl -v -H "Content-Type: application/json" -X POST http://localhost:8080/device -d '{"name":"device_name","connectionDetails":{"address":"119.29.202.66","password":"password"}}'

curl -v -H "Content-Type: application/json" -X PUT http://localhost:8080/device -d '{"id":"123","name":"device_name","connectionDetails":{"address":"119.29.202.66","password":"password"}}'

curl -v -X DELETE http://localhost:8080/device/123