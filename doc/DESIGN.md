# Names:
Alex Oesterling, axo
Shruthi Kumar, sk593
Varun Kosgi, vsk10
Abhijay Suhag, as866
Frank Tang, ft39

# Roles:
* Alex Oesterling:
    * Wrote Action inheritance hierarchy
    * Wrote Entity class structure with Wrapper, Model, and View
    * Refactored Collision Engine based on other's work to not get entities stuck inside of each other
    * Wrote initial XML Entity parser (deprecated)
    * Refactored certain classes such as EntityJSONParser and the Level class hierarchy

* Shruthi Kumar:
    * Wrote Action factory to create Actions using reflection
    * JSON file setup logic + parsing logic (done through EntityJSONParser implementation)
    * GameController refactoring/streamlining (implemented ViewManager, ModelManager usage) 
    * Saving/Loading Game
    * Reset Games when user dies  
    * Suspend point saving (in game saving)
    * Refactored exception handling to use inheritance and display alerts
 
* Frank Tang:
    * Wrote the initial implementations of the CollisionEngine and PhysicsEngine to detect collisions between entities and apply basic physics properties
    * Implemented and designed the LevelParser to allow us to create Infinite and Finite levels for a variety of game types
    * Consolidated the multiple prototype controllers into one GameController by making a GameParser to implement reflection when parsing game files
    * Added capabilities for the Camera and Levels to work in both the X and the Y direction with the GameStatusProfile to get the scrolling status of a game
    * Game Parsing (implemented parsing of game files)

* Varun Kosgi:
    * User Configuration and control bind updating from Pause Menu (InGameMenu, ConfigurationMenu, VolumeSliders)
    * Consolidated multiple prototype Game subclasses into a single class to streamline Game selection and front-end instantiation of different games (Game, AudioVideoManager)
    * GUI Goodies - going Home, hard Reset (methods in ViewManager) 
    * System-wide audio switching of music and sound effects (AudioVideoManager)
    * Welcome screen and game title screens (Welcome, TitleScreen)
    * Implemented Local Multiplayer (methods in GameParser and TitleScreen)
    * JUnit Testing for front-end configuration menus


* Abhijay Suhag: 
    * GUI set-up - Game selection visuals and animations (GameCabinet, GameSelector, GamePreview)
    * Encapsulation of stage access and scene management/switching in StageManager 
    * Refactored front end hierarchy to simpilfy main and reduce unnecessary object instantiation (ProgramLauncher)
    * Refactored game selection GUI into skin, animations, and logic
    * GamePad implementation (use of outside controller, JXInput)
    * Exception handling to give files default game textures, values and soundtracks, allowing user to continue playing game (deprecated)
    * JUnit Testing for front end and game parsing exceptions


# Design Goals
* The design goals of this project were to use a data driven design to facilitate the creation of an abstract game engine which can handle a variety of 2D games in the platformer and sidescroller genre. Our primary goal is to make a flexible design which would allow us to completely write games using data files only. Our second goal is to make this design robust and efficient, using techniques learned throughout this course to make the code efficient, clean, and well-designed.
    * We primarily use parsers to manage the interactions between the data files and the back end side. The parsers are used to read data from the files and populate values. Once the parsers are made, the GameController handles running the interactions between objects and implementing the game logc (through the use of the physics engine and collision engine). The GameController has access to the ViewManager which takes care of displaying entities and their actions on the GUI. 
  
* Some examples of new features we wanted to make easy to add are: 
    * Entire new games: Add this feature by making a new gamename package in the src/resources directory, and then add entities, images, levels, as you wish. Each game directory requires a gamenameGame.json and gamenameSelect.json in its directory to run.
    * Modifying existing games: This bullet point is about how we wanted to make sure that we could modify existing games through primarily data files for bug fixing, changing constants, or just adding new features like levels and enemies. This is easily done because our game engine defines the rules of a game through the interactions of the Entity and Level objects. 

# Assumptions Made and Simplifications
* One assumption we made was that we assume the user knows what keybindings or controller controls are used in each game. Though they can see this in the Config screen when the user pauses the game or look at the game's data file, our current implementation has no tutorial screen.

* Another assumption that we made was that all side scrolling games will consist of either FiniteLevels, InfiniteLevels, or both. This assumption was made so that we could hard code the major differences (such as absolute versus relative position spawning) between these two classes in two subclasses of the Level superclass. So far, this assumption has held true, as we have different examples of games that use strictly FiniteLevels, InfiniteLevels, or a mix of both. 

* Our initial implementation for exception handling was not as robust as it should have been, leading to errors not being thrown or caught. We implemented a new design that used an abstract parent Exception class, and then sub classes that handled specific issues. However, there are still some exceptions that are handled, but their alerts are not as informative as they should be. Thus, our current exception handling assumes that in some cases (an example would be when a game's GameSelect.json file is missing or incorrect), the user will know which file is incorrect without us giving specific file information. 


# Describe, in detail, how to add new features to your project, especially ones you were not able to complete by the deadline
* In our plan we said we would implement scores (including high scores) and player profiles. 
    * We have a SetScore action that will increment the score of the player. However, we have not implemented a way to keep track of this in the Controller so the score value only stays encapsulated within the entity that uses this Action. In order to fully implement this, we would need to have a running counter in the GameController class that prints out the player entity's score as it runs through the game. This wouldn't require any additional code past actually printing out the score as the data files and the player entity will handle changing the physical score. 
    * If we wanted to add high scores, we would implement a similar function to how we save a game. We would likely have a JSON file that keeps track of the high scores of all the games. If the user gets a score that is higher than the current score in the JSON file, we would overwrite the value in this file. In the pause screen, we would just add another TextField that displays the high score value.
    * To implement player profiles, we would likely want to create a JSON file template that will hold user data. When the user boots up the game, the user should have the ability to save their profile before pressing "Play". Each user will hava unique JSON file that contains their personal info. If they want to load their profile on the home screen, this will also be an option. In this case, the player profile will be loaded from their JSON profile file through a parser (similar to our entity parser). Then the user can play their game as they wish and could theoretically save data about their games should they choose (this would all depend on what features we allow them to have). If they want to save their data, we would just implement a function similar to our save game function that updates JSON files. 

* If you wanted to add a new game, you would have to add all the necessary data files with correct parameters. These game data files can include anything you want them to have as long as the actions you are trying to implement exist (if it doesn't, you could add your own Actions to the code) and the way you format your data is correct. Then you add the name of the game to the GameList.json file and the gif image of the game (for the Game Select Screen) to the resources folder. Then, you can run the game and play it. Thus, being able to add a new game basically is dependent upon you adding the correct files with the correct parameters.
* If you wanted to add a new action, you would add the specific Action to the actions folder and extend the abstract Action superclass. The name of the Action also has to be added to a Actions properties file so that reflection can be implemented properly. Then, you would implement the execute() method with what you wanted that action to do.   