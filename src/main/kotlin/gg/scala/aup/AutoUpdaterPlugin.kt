package gg.scala.aup

import gg.scala.commons.ExtendedScalaPlugin
import net.evilblock.cubed.util.CC
import org.bukkit.Bukkit
import java.io.File

/**
 * @author GrowlyX
 * @since 10/15/2021
 */
class AutoUpdaterPlugin : ExtendedScalaPlugin()
{

    private val filesToUpdate = arrayListOf<String>()
    private lateinit var downloaderDirectory: String

    override fun enable()
    {
        saveDefaultConfig()

        this.downloaderDirectory = this.config.getString("downloader-folder")

        fetchPossibleUpdates()

        Runtime.getRuntime().addShutdownHook(
            Thread {
                fetchPossibleUpdates()

                this.filesToUpdate.forEach(this::executeFileUpdate)
            }
        )
    }

    private fun fetchPossibleUpdates()
    {
        val file = File(this.downloaderDirectory)
        val subFiles = file.list()

        if (subFiles != null)
        {
            if (subFiles.isEmpty())
            {
                this.broadcastColor("No required updates have been found.")
            }

            for (subFile in subFiles)
            {
                if (this.filesToUpdate.contains(subFile))
                {
                    continue
                }

                this.filesToUpdate.add(subFile)
                this.broadcastColor("${CC.YELLOW}$subFile${CC.GREEN} will be updated on the next reboot.")
            }
        } else
        {
            this.broadcastColor("No required updates have been found.")
        }
    }

    private fun broadcastColor(message: String)
    {
        Bukkit.getConsoleSender().sendMessage("${CC.GREEN}[AUP] $message")
    }

    private fun executeFileUpdate(file: String)
    {
        try
        {
            val container = server.worldContainer.path

            Runtime.getRuntime()
                .exec("cp -R $downloaderDirectory/$file $container/plugins")
        } catch (ignored: Exception) {
            println("[AUP] Failed to update $file!")
        }
    }
}
