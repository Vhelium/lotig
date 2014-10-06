# Lairs of the Insane Gods #


## Project overview ##
___

### Users ###
___

* Timon Blattner - Lead Developer
* vhtwink - 2nd user account of Timon Blattner

### Folder structure ###
___

* *_packer_input* - Texture Packer runnable + unpacked asset files
* *android* - Android platform specific code
* *ios* - iOS platform specific code
* *desktop* - PC platform specific code (Windows/OSX/Unix)
* *core* - Platform independent code (main part)

### Asset folders ###

#### Sprites without packing ####
* Entity animations (players, monsters): /android/assets/gfx/entities
* Tile sets: android/assets/gfx/packs

#### Sprites with packing ####
* Bullet assets: /_packer_input/bullets
* Effect animations: /_packer_input/effects
* Gui assets /_packer_input/gui
* Item icons /_packer_input/items
* General game assets: /_packer_input/assets

## Project setup ##
___

### Software & Tools ###
___

* [Git](http://git-scm.com/downloads) basic git
* [KDiff3](http://kdiff3.sourceforge.net/) (for visual merging)
* LibGDX Texture packer (included in the repository under */_packer_input*)

Optional:

* Recommended: [Eclipse ADT bundle](http://developer.android.com/sdk/index.html)
* Eclipse set up as described [here](https://github.com/libgdx/libgdx/wiki/Setting-up-your-Development-Environment-%28Eclipse,-Intellij-IDEA,-NetBeans%29)

### Set up Git ###
___

* Install & configure Git: [Link](https://confluence.atlassian.com/display/BITBUCKET/Set+up+Git) (no need for step 2)
* Create a workspace folder, e.g. "*workspaceLOTIG*" where you would like to have it (e.g. *'User/Documents'*)
* Create a folder inside this workspace named "*LairsOfTheInsaneGods*"
* Open up your terminal and type: *"cd /path/to/the/workspaceLOTIG/LairsOfTheInsaneGods"* (adjust the path to fit yours)
* Go on Bitbucket, click on "Clone" in the top left corner and copy the link that pops up ( [example](http://i.imgur.com/BkonRVW.png) )
* Switch back to your terminal and type: *"git clone https://theLinkYouJustCopied.git"*
* Git should now clone the repository in the folder "*LairsOfTheInsaneGods*"
* Type in the terminal: *"git remote add origin https://theLinkYouJustCopied.git"*

### Update your local repository ###
___

* Type: *"cd /path/to/the/workspaceLOTIG/LairsOfTheInsaneGods"*
* Type: *"git pull origin master*" to update your local repository

### Commit changes ###
___

*Note: 'type' refers to typing a command into terminal*

* Type: *"cd /path/to/the/workspaceLOTIG/LairsOfTheInsaneGods"*
* Type: *"git status -s"* to view your changes made to the local repository
* Type: *"git add -A"* to select all your changes or type *"git add fileName"* to add a specific file to the commit
* Type: *"git commit -m "Enter your commit message here""* to apply your changes. Always enter a message with the -m flag followed by the text enclosed in quotes.
* Type: *"git push origin master"* to apply your changes to the remote repository (on the server)

### Resolve conflicts###
___

* [Merging conflicts](http://www.gitguys.com/topics/merging-with-a-conflict-conflicts-and-resolutions/)
* [Merging with a GUI](http://www.gitguys.com/topics/merging-with-a-gui/)

### Packing the sprites with Texture Packer ###
___

* Open terminal and type: *"cd /path/to/the/workspaceLOTIG/LairsOfTheInsaneGods/_packer_input"*
* Type: *"java -jar gdx-texturepacker.jar"
* At the top of the new window click on 'Open project' which opens a dialog
* Navigate to the *_packet_input* folder and select the file called *"lotig"* and click 'open'
* Now press the 'Pack'em all' button and wait for the sprites to be packed into *../assets/gfx/packs*