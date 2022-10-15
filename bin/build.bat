cd ..\src
javac -d ..\bin\ -cp ".\;..\lib\gson-2.9.1.jar" server.java
javac -d ..\bin\ -cp ".\;..\lib\gson-2.9.1.jar" client.java
cd ..\bin
cls
server
