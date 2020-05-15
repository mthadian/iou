# iou

Technologies: Spring-boot,H2-DB.


Setup Steps:


Method 1:

•	Copy the file  /iou-1.jar to local machine

•	Using java, run the file, i.e, java -jar iou-1.jar. By default, the application will run on port 8080,
however to change port, run as java -jar -Dserver.port={port} iou-1.jar ,eg, java -jar -Dserver.port=8081 iou-1.jar or on powershell 
java -jar “-Dserver.port=8081” iou-1.jar


Method 2:

Open the folder from an ide like Eclipse, Netbeans or IntelliJ as a maven project. The prerequisite in this case is maven and java 8.

Testing

Please note that the endpoints were created that the user’s name is unique for easy testability (Not a real world scenario),
this means that trying to create a user, e.g Creating Adam again will alert as a duplicate user.




DB UI


url: server-url:port /h2-console

Jdbc url: jdbc:h2:~/dbIOU1

Username:root

Password:


