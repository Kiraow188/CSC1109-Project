# NOTICE: For the project to run as intended, you have to setup the database using XAMPP!

# Our project have two view mode that it can run in. One on the CLI and another is the GUI.

## How to run the CLI version
1. Due to the nature of the password masking using Java’s Console library, the CLI version can only be run via the console.
2. To do this, you can execute the **MainAtmCli.java** located in this path (*CSC1109-Project-main.zip\CSC1109-Project-main\sitATM\src\main\java\com\sitatm\sitatm*) using VSCode or via terminal execution.
## How to run the GUI version
1. For the GUI version, you can just run the **ATM.java** located in this path (*CSC1109-Project-main.zip\CSC1109-Project-main\sitATM\src\main\java\com\sitatm\sitatm*) using Intellij.
# Setting up XAMPP for Windows & Mac and Importing SQL Database

XAMPP is a free and open-source tool that allows users to create a local web server environment on their Windows system. This guide will walk you through the steps to set up XAMPP and import our  sitatm.sql database file.

## Step 1: Download and Install XAMPP

1.  Download the latest version of XAMPP from [https://www.apachefriends.org/download.html](https://www.apachefriends.org/download.html) according to your OS.
2.  Run the installer and follow the instructions to install XAMPP on your system.
3.  During the installation process, you will be prompted to choose the components you want to install. Ensure that Apache, MySQL, and PHP are selected.
4.  Once the installation is complete, start the Apache and MySQL modules from the XAMPP Control Panel.

## Step 2: Import the SQL Database

1.  Launch phpMyAdmin in your web browser by navigating to [http://localhost/phpmyadmin](http://localhost/phpmyadmin).
2. Click on “New” located on the left panel.
3. In the “Database name” textbox, enter “sitatm” and click the “Create” button.
4. Click on “Import” located at the top of the page.
5. Click on “Choose File" and select sitatm.sql
6. In the other options panel below, deselect “Enable foreign key checks”
7. Click on “Import” button
8. Wait for the import to complete. The time it takes to import the database will depend on the size of the .sql file.
