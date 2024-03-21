import xyz.jpenilla.resourcefactory.bukkit.bukkitPluginYml
import xyz.jpenilla.resourcefactory.paper.PaperPluginYml.Load
import xyz.jpenilla.resourcefactory.paper.paperPluginYml
import xyz.jpenilla.runpaper.task.RunServer

plugins {
  id("carbon.shadow-platform")
  id("xyz.jpenilla.resource-factory") version "0.0.1"
  id("xyz.jpenilla.run-paper")
  id("carbon.permissions")
  id("carbon.configurable-plugins")
}

dependencies {
  implementation(projects.carbonchatCommon)

  // Server
  compileOnly(libs.foliaApi)
  implementation(libs.paperTrail)

  // Commands
  implementation(libs.cloudPaper)

  // Misc
  implementation(libs.bstatsBukkit)

  // Plugins
  compileOnly(libs.placeholderapi)
  compileOnly(libs.miniplaceholders)
  compileOnly(libs.essentialsXDiscord) {
    exclude("org.spigotmc", "spigot-api")
  }
  compileOnly(libs.discordsrv) {
    isTransitive = false
  }
  compileOnly(libs.towny)
  compileOnly(libs.mcmmo) {
    isTransitive = false
  }
  compileOnly(libs.factionsUuid)

  runtimeDownload(libs.guice) {
    exclude("com.google.guava")
  }
}

configurablePlugins {
  dependency(libs.towny)
  dependency(libs.mcmmo)
  dependency(libs.factionsUuid)
}

tasks {
  shadowJar {
    relocateDependency("io.papermc.papertrail")
    relocateDependency("io.leangen.geantyref")
    relocateCloud()
  }
  withType(RunServer::class).configureEach {
    version.set(libs.versions.minecraft)
    downloadPlugins {
      url("https://download.luckperms.net/1533/bukkit/loader/LuckPerms-Bukkit-5.4.120.jar")
      github("MiniPlaceholders", "MiniPlaceholders", libs.versions.miniplaceholders.get(), "MiniPlaceholders-Paper-${libs.versions.miniplaceholders.get()}.jar")
      github("MiniPlaceholders", "PlaceholderAPI-Expansion", "1.2.0", "PlaceholderAPI-Expansion-1.2.0.jar")
      hangar("PlaceholderAPI", libs.versions.placeholderapi.get())
    }
  }
  register<RunServer>("runServer2") {
    pluginJars.from(shadowJar.flatMap { it.archiveFile })
    runDirectory.set(layout.projectDirectory.dir("run2"))
  }
}

runPaper.folia.registerTask()

val paperYml = paperPluginYml {
  name = rootProject.name
  loader = "net.draycia.carbon.paper.CarbonPaperLoader"
  main = "net.draycia.carbon.paper.CarbonPaperBootstrap"
  apiVersion = "1.20"
  authors = listOf("Draycia", "jmp")
  website = GITHUB_REPO_URL
  foliaSupported = true

  dependencies {
    server("LuckPerms", Load.BEFORE, true)
    server("PlaceholderAPI", Load.BEFORE, false)
    server("EssentialsDiscord", Load.BEFORE, false)
    server("DiscordSRV", Load.BEFORE, false)
    server("MiniPlaceholders", Load.BEFORE, false)

    // Integrations
    server("Towny", Load.BEFORE, false)
    server("mcMMO", Load.BEFORE, false)
    server("Factions", Load.BEFORE, false)
  }
}

val bukkitYml = bukkitPluginYml {
  name = rootProject.name
  main = "carbonchat.libs.io.papermc.papertrail.RequiresPaperPlugins"
  apiVersion = "1.20"
  authors = listOf("Draycia", "jmp")
  website = GITHUB_REPO_URL
}

sourceSets.main {
  resourceFactory {
    factories(paperYml.resourceFactory(), bukkitYml.resourceFactory())
  }
}

carbonPermission.permissions.get().forEach {
  setOf(bukkitYml.permissions, paperYml.permissions).forEach { container ->
    container.register(it.string) {
      description = it.description
      childrenMap = it.children
    }
  }
}

publishMods.modrinth {
  modLoaders.addAll("paper", "folia")
}

configurations.runtimeDownload {
  exclude("org.checkerframework", "checker-qual")
}
