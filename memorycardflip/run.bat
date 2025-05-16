@echo off
echo Compiling Memory Card Game...
javac -d bin src\MemoryCardGame.java
if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Running Memory Card Game...
java -cp bin MemoryCardGame
