@echo off
REM Verify if JORAM_HOME is well defined
if not exist "%JORAM_HOME%\samples\bin\clean.bat" goto nokHome
REM Verify if JAVA_HOME is well defined
if not exist "%JAVA_HOME%\bin\java.exe" goto nokJava
REM Test the argument number
if "%1" == "" goto nokArg

set CONFIG_DIR=%JORAM_HOME%\samples\config
set JORAM_BIN=%JORAM_HOME%\ship\bin
set RUN_DIR=%JORAM_HOME%\samples\run
set SERVER_RUN_DIR=%RUN_DIR%\server%1

REM  Building the Classpath
set CLASSPATH=%JORAM_BIN%\felix.jar

mkdir %RUN_DIR%
mkdir %SERVER_RUN_DIR%
copy %CONFIG_DIR%\a3config.dtd %SERVER_RUN_DIR%\a3config.dtd
copy %CONFIG_DIR%\a3debug.cfg %SERVER_RUN_DIR%\a3debug.cfg
copy %CONFIG_DIR%\distributed_a3servers.xml %SERVER_RUN_DIR%\a3servers.xml
copy %CONFIG_DIR%\config.properties %SERVER_RUN_DIR%\config.properties

set PATH=%JAVA_HOME%\bin;%PATH%

echo == Launching a persistent server#%1 ==
start /D %SERVER_RUN_DIR% java -Dosgi.shell.telnet.port=1600%1 -Dfelix.config.properties=file:config.properties -Dfr.dyade.aaa.agent.AgentServer.id=%1 -Dcom.sun.management.jmxremote -classpath %CLASSPATH% org.apache.felix.main.Main
goto end
:nokHome
echo The JORAM_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end
:nokJava
echo The JAVA_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end
:nokArg
echo !! Missing server id argument (0, 1 or 2) - see a3servers.xml !!
goto end

:end
