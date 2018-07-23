rm /home/foghrye4/.minecraft/mods/1.12.2/iblis-*.jar
./gradlew build -Pversion=$1
cp ./build/libs/iblis-$1.jar "/home/foghrye4/.minecraft/mods/1.12.2/"
cp ./build/libs/iblis-$1.jar "/home/foghrye4/Minecraft-dev/Forge_server/mods/"
java -jar /home/foghrye4/Games/Minecraft.jar
cd ../../..
