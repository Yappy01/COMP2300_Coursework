# COMP2300_Coursework. Sexual_Health_Education_App


## Instructions on running project
This project utilizes Maven to run. Hence some prior modification to the run configuration of your compiler might be needed.
### Recommended compiler - IntelliJ
1. Open the .env file and copy the content of the file. (The env file is not included in this github repo for security reasons, please unzip the submission zip version for the env file.)
2. In the Run/ Debug Configuration, add a new configuration and choose Maven.
3. In the command line section of the configuration, add this line "clean javafx:run".
4. In Java Options section, change the JRE to Oracle OPENJDK 23.0.2.
5. If you have do not have any, head to oracle website and search and download JDK 23.
6. In Java options, click on modify and allow environment variable to be edited.
7. Paste the content of the .env file into the environment variable box.
8. Once done, click on apply and ok.
9. You should be able to run the project by pressing on run now.

## Notes
Currently, only the Login/Registration page, Main page and the information page is functional and accessible. The other buttons is present but will only reveal a replacement action for pressing on the button.