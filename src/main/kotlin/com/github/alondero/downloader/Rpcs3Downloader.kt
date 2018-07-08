package com.github.alondero.downloader

import com.github.alondero.EmulatorDownloader
import com.github.kittinunf.fuel.httpDownload
import org.jsoup.Jsoup
import java.nio.file.Files
import java.nio.file.Files.createTempDirectory
import java.nio.file.Files.createTempFile
import java.nio.file.Paths

class Rpcs3Downloader: EmulatorDownloader {
    override val emulatorName = "RPCS3"

    override fun download() {
        println("Starting $emulatorName Download")

        val url = Jsoup.connect("https://rpcs3.net/download")
            .get()
            .getElementsByAttributeValueEnding("href", "win64.7z").attr("href")

        val tempFile = createTempFile(createTempDirectory(emulatorName), emulatorName, ".7z").toFile()

        val (_, _, result) = url.httpDownload().destination{ _, _ -> tempFile }.response()

        Files.write(Paths.get(tempFile.toURI()), result.get()) // Unsure why I need to manually do this for this file....

        unpack(tempFile, arrayOf("dev_hdd0"))

        println("Finished $emulatorName")
    }
}