@echo off
echo Checking for MySQL connector JAR...
if not exist mysql-connector-java-8.0.27.jar (
    echo Downloading MySQL connector...
    curl -o mysql-connector-java-8.0.27.jar https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.27/mysql-connector-java-8.0.27.jar
    if %errorlevel% neq 0 (
        echo Failed to download MySQL connector. Please download it manually from https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.27/mysql-connector-java-8.0.27.jar
        pause
        exit /b 1
    )
)
echo Compiling Java application...
javac -cp "mysql-connector-java-8.0.27.jar" src\main\java\DBMSGuiApp.java -d target\classes
if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b 1
)
echo Running DBMS GUI Application...
java -cp "target\classes;mysql-connector-java-8.0.27.jar" DBMSGuiApp
pause
