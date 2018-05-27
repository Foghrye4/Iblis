# Iblis Minecraft mod for Minecraft Forge versions 1.12-1.12.2.
Builds and info: http://minecraft.curseforge.com/projects/iblis
To setup development workspace:
1. Open console.
2. Run "git --clone https://github.com/Foghrye4/Iblis.git"
3. Run "cd iblis"
4. Run "./gradlew setupDecompWorkspace -Pv=1.0", where the "v" is a version string parameter.
5. Run "./gradlew eclipse -Pv=1.0" for Eclipse or "./gradlew idea -Pv=1.0" for IDEA.

To build mod from source run "./gradlew build -Pv=1.0". Replace "1.0" with your current version. This string will be added to file name.
