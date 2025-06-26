# online-tutor
Java microservice for online tutor management

## Features

* Tutor registration and session management
* Student registration and session management
* Tutor and student communication using chat and websocket
* Zoom integration
* IntaSend payment integration
* Search for tutors and students
* Admin dashboard for managing sessions and payments
* Swagger API documentation
* Spring Security for authentication and authorization

## Getting Started

1. Clone the repository
2. Build and run the application.
3. Access the API documentation at http://localhost:8081/swagger-ui.html

## Contributing

1. Fork the repository
2. Create a new branch
3. Make your changes
4. Submit a pull request


## Deployment

1. mvn install:install-file -Dfile="D:/Mumly App/online-tutor/lib/mumly-event-0.0.1-SNAPSHOT.jar" -DgroupId=com.vivatech -DartifactId=mumly-event -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar
   - replace the file path with your local path
2. mvn clean install -Pdefault -DskipTests
3. Rename file to online-tutor.jar
4. Upload the file in deliverables folder on server
5. Copy the jar file to the tutor folder and move the old jar file to the deliverables folder by name online-tutor.jar.version_no
6. Stop the running application -> ps aux | grep "jarfilename" -> kill -9 PID
7. Start the application -> nohup /opt/java17/bin/java -jar online-tutor.jar > logs/app.log 2>&1 &
