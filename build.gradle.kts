dependencies {
    compileOnly(project(":lobby"))
    compileOnly(fileTree("spigot"))
    api(project(":minigames:bedwars-shared"))
    compileOnly("net.dmulloy2:ProtocolLib:5.4.0")
}
