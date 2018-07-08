package com.github.alondero.downloader

import com.github.alondero.EmulatorDownloader
import com.github.kittinunf.fuel.httpDownload
import org.jsoup.Jsoup
import java.nio.file.Files
import java.nio.file.Paths

class DolphinDownloader: EmulatorDownloader {
    override val emulatorName = "Dolphin-x64"

    override fun download() {
        println("Starting $emulatorName Download")

        val url = Jsoup.connect("https://dolphin-emu.org/download")
                .get()
                .getElementsByAttributeValueEnding("href", "-x64.7z").attr("href")

        val tempFile = Files.createTempFile(Files.createTempDirectory(emulatorName), emulatorName, ".7z").toFile()

        url.httpDownload().destination{ _, _ -> tempFile }.response()

        unpack(tempFile, emptyArray(), includeEmulatorName = false)

        println("Finished $emulatorName")
    }
}