cd C:/git/gov-auto-download
call mvn clean package
cd C:/git/gov-auto-download/target
java -jar gov-auto-download-1.0-SNAPSHOT.jar
pause
