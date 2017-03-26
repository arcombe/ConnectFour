# Game of Connect Four
Building the game from the sourse code can be done with IntelliJ and Java Platform, Standard Edition (Java SE) 8. 

Open the project in IntelliJ 

Build -> Build Artifact -> Build

If that does't work, do this first

Remove META-IF in src

File -> Project Structure -> Project Settings -> Artifacts -> Jar -> From modules with dependencies...

Select Main Class as GameMenu -> Ok

Change Output directory where jar will be saved to

Apply -> Ok

Then do the above again.

It is important that the playerDatabase.db file is in the same directory as the game otherwise the game wont work.

## Steps - Terminal
1. Clone or download and extract the repository
2. Navigate to the directory
3. Enter the command: java -jar Connect4Game.jar

## Steps - Finder 
1. Clone or download and extract the repository
2. Open the directory
3. Open the Connect4Game.jar





