# How to setup using Intellij  
Make sure the JavaFX plugin is enabled  
To be able to work with JavaFX in IntelliJ IDEA, the JavaFX bundled plugin must be enabled:  
1. In the Settings dialog (Ctrl+Alt+S), select Plugins.
2. Switch to the Installed tab and make sure that the JavaFX plugin is enabled.
3. If the plugin is disabled, select the checkbox next to it.
4. Apply the changes and close the dialog. Restart the IDE if prompted.  
**Install scene builder**  
[Scene Builder - Gluon (gluonhq.com)](https://gluonhq.com/products/scene-builder/)  
Remember the installation path to scenebuilder.exe
On Intellij go to File > Settings > Language & Framework > JavaFX > paste or navigate to scenebuilder.exe > click "OK"  
![intellij Scenebuilder](https://github.com/Kiraow188/CSC1109-Project/blob/Jace-Branch/md_src/scenebuilderIntellij.png)
# Setup JavaFX and Maven on VScode
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
# Setup Scenebuilder on Windows
Scenebuilder is a tool to help visually build JavaFX application  
**Install scene builder**  
[Scene Builder - Gluon (gluonhq.com)](https://gluonhq.com/products/scene-builder/)  
_Take note of the installation path!_  
**Install scene builder add-on in visual studio code:**  
[SceneBuilder extension for Visual Studio Code - Visual Studio Marketplace](https://marketplace.visualstudio.com/items?itemName=bilalekrem.scenebuilderextension#:~:text=You%20can%20configure%20by%20clicking,your%20file%20into%20Scene%20Builder.)  
1. press ctrl+shift+p
2. type in “configure scene builder path”
3. find your scene builder installation path and select that folder
4. Go to main.fxml and hit ctrl+shift+p 
5. type in “open in scene builder”  
![Scenebuilder](https://github.com/Kiraow188/CSC1109-Project/blob/Jace-Branch/md_src/scenebuilder.png)
