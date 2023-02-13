- Step 1: Install the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack).
- Step 2: Install Maven for Java in Visual Studio Code [Maven for Java - Visual Studio Marketplace](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-maven)
- Step 3: Download Maven on windows [Maven – Download Apache Maven](https://maven.apache.org/download.cgi) please select **BINARY ZIP ARCHIVE** (apache-maven-3.9.0-bin.zip)
- **For a more visual setup of Maven’s Environmental Variable click [here](https://phoenixnap.com/kb/install-maven-windows)**
- Step 4: Extract the folder to C:\Program Files\Maven\apache-maven-3.9.0
- Step 5: Search for “Edit the system environment variables” in Start
- Step 6: Click on “Environmental Variables“
- Step 7: Under System Variable, click “New” and add the following
  **Variable Name:** MAVEN_HOME
  **Variable Value:** C:\Program Files\Maven\apache-maven-3.9.0
  Click “OK”
- Step 8: Still, under System Variable, scroll down and look for “path” and click edit
- Step 9: Click “New” and type in “%MAVEN_HOME%\bin” press “OK” and close the window
- Step 10: In Visual Studio Code, open the Command Palette (Ctrl+Shift+P) and then select the command **Java: Create Java Project**.
- Step 11: Select the option **JavaFX** in the list, follow the wizard, which will help you scaffold a new JavaFX project via Maven Archetype.  
![start JavaFX Project](https://github.com/Kiraow188/CSC1109-Project/blob/Jace-Branch/md_src/Pasted%20image%2020230213214519.png)
- To run the JavaFX application, you can open the **Maven** Explorer, expand `hellofx` > `Plugins` > `javafx` and run the Maven goal: `javafx:run`.  
![Run JavaFX Project](https://github.com/Kiraow188/CSC1109-Project/blob/Jace-Branch/md_src/ezgif-4-6d89dd086b.gif)
