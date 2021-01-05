@echo off
if "%OS%" == "Windows_NT" setlocal
rem Guess RAINBOW_HOME if not defined

set CURRENT_DIR=%cd%
if not "%RAINBOW_HOME%" == "" goto gotHome
cd ..
set RAINBOW_HOME=%cd%
cd %CURRENT_DIR%

:gotHome
echo RAINBOW_HOME=%RAINBOW_HOME%
if exist "%RAINBOW_HOME%\bin\shutdown.bat" goto okHome
echo The RAINBOW_HOME environment variable is not defined correctly
goto end

:okHome
set LIB=%RAINBOW_HOME%\lib
set CLASSPATH=%LIB%\core.jar
set MET_ARG=-Xms512m -Xmx1024m

echo CLASSPATH=%CLASSPATH%
java %MEM_ARG% -classpath "%CLASSPATH%" rainbow.core.platform.Shutdown

:end