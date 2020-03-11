@echo off
if !exist C:/Test/gov-auto-download goto
cd C:/Test/gov-auto-download
call mvn clean package
cd C:/git/gov-auto-download/target
java -jar gov-auto-download-1.0-SNAPSHOT.jar
:init
mkdir C:/Test/gov-auto-download
cd C:/Test/got-auto-download
git init

pause
