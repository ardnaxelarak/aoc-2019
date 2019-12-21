@echo off

for /f "tokens=1,* delims= " %%a in ("%*") do set ALL_BUT_FIRST=%%b

mvn exec:java -Dexec.mainClass="%1" -Dexec.args="%ALL_BUT_FIRST%"
