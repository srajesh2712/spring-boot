curl -X POST http://localhost:8080/add-user      -H "Content-Type: application/json"      -d '{"name":"John Doe", "age":28, "status":"Learning Spring"}'


docker build 
docker build --target build -t spring-boot .

This will download the jdk, copy the files, build 


docker run -p 8084:8084 --name my-running-app spring-boot