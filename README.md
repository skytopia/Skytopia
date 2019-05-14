Skytopia
=======================

Skytopia is a plugin for the Skyblock server that performs many useful functions.

Contributors
------------
* [lavuh](https://github.com/lavuh) (Plugin development)
* [JacquiRose](https://github.com/JacquiRose) (Plugin integration/testing)

Features
--------
- Adds many commands which exist in Essentials, but not CommandBook.
- Updates skulls at spawn with the top 3 voters and top 3 island owners.
- Keeps track of players' last logins and their last known IGNs.
- 

Compiling
---------
Compiling is not recommended at this current stage. You will need to use [Maven](https://maven.apache.org/) to compile:
* Spigot/CraftBukkit libraries from [BuildTools](https://www.spigotmc.org/wiki/buildtools/)
* sk89q-command-framework libraries from [our fork](https://github.com/skytopia/sk89q-command-framework)
* Skyblock libraries, which are not open source. There is no API currently.

Ensure you include `Sainttx Holograms` libraries. Place JAR into base directory.

Once you have these libraries compiled, on your commandline, type the following.
```
cd /path/to/Skytopia
mvn clean install
```
Maven automatically downloads the other required dependencies.
Output JAR will be placed in the `/target` folder which can be then put into the plugins folder.