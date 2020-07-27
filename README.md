### *This is my final project for CS308: Advanced Software Design and Implementation. This project was created using the Agile Project Management strategy with weekly sprints over the course of a month. Due to Covid-19, we worked virtually and had daily calls where we updated each other on progress and merged our work. I primarily worked in the src.ooga.model.actions package, as well as writing the EntityWrapper class hierarchy (EntityWrapper, EntityModel, EntityView) which actions operate on. I also helped write components of these entities in the src.ooga.model.controlschemes package, among other things.*

BOOGA (Bob's Object Oriented Game Arcade)
====

This project implements a player for multiple related games.

Names:
Varun Kosgi,
Shruthi Kumar,
Alex Oesterling,
Abhijay Suhag,
Frank Tang

### Timeline

Start Date: March 28, 2020

Finish Date: April 25, 2020

Hours Spent: 200+ (~40-50 hours each)

### Primary Roles
#### Alex
* Entity class design and its composition architecture
* Entity Model/View setup (EntityWrapper)
* Camera implementation
* JUnit Testing

#### Shruthi
* Saving, loading, restarting games
* JSON Parsers (Entity/Game)
* Controller management (View/Model Manager)
* JUnit Testing
     
#### Frank
* Physics Engine
* Collision Engine
* Entity behaviors/interactions 
* JUnit Testing

#### Abhijay
* GUI set-up (game select screen)
* LevelParser
* GamePad implementation
* JUnit Testing

#### Varun
* User configuration settings + pause menu 
* Frontend + Controller interactions (managers, title screen)
* keybindings + control scheme switching
* JUnit Testing

### Resources Used
(https://design.cs.duke.edu/#/dashboard)[Design Checklist]
(https://junit.org/junit5/docs/current/user-guide/#overview-getting-started-example-projects)[JUnit]

### Running the Program

Main class: 
Main.java

Data files needed: 
- The data files are provided in the resources folder. The user is free to add their own files assuming
they follow the correct format. If the format is incorrect, default values are used. 
- For example, the data for Mario is in the resources/mario folder. 


Features implemented:
- Save Game: Can save a current game by pressing X
- Load Game: Can load a new game by pressing load game on the title screen 
- GamePad use: Player 2 can use a controller if they wish 
- Extra games: support additional games past the 3 original (Mario, RivalRacer, FlappyBird) like Doodle Jump, 
    Monster Dash and Smash
- Local multiplayer
- Artificial players
- Preferences (music switching and volume changing) 


### Notes/Assumptions

Assumptions or Simplifications:
- Our biggest assumption is that the data files that are input into the system are not corrupt. We have implemented exception handling by
using default values when this occurs. 
- We also have no win screen currently. There is a loss screen but adding a win screen would require the addition of a WinLevel action 
that is called when a certain brick is passed. This WinLevel action would trigger the win screen. 

Interesting data files:
- Our most developed game is Mario. The data files to play this game are located in the resources/mario folder. 

Known Bugs:
- Flappy Bird: holding space down makes Flappy float forever
- Using attack in Smash in the same frame as the previous attach causes the player to disappear  

Extra credit:
- GamePad
- Controller Config

### Impressions
Though it was hard work, our team really enjoyed the process of creating a game from scratch. We were able to create basic implementations
of famous games despite our still developing knowledge and skills.

