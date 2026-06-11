@echo off
set JAVA_HOME=%USERPROFILE%\.gradle\jdks\eclipse_adoptium-25-amd64-windows.2
set PATH=%JAVA_HOME%\bin;%PATH%
cd /d "%~dp0"
java -Xms2G -Xmx4G -jar paper-26.1.2-69.jar --nogui
