package com.github.alondero.downloader

import com.github.alondero.EmulatorDownloader
import com.github.kittinunf.fuel.httpDownload
import org.jsoup.Jsoup
import java.nio.file.Files.createTempDirectory
import java.nio.file.Files.createTempFile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val BASE_RETROARCH_URL = "http://buildbot.libretro.com"

class RetroarchDownloader: EmulatorDownloader {
    override val emulatorName = "RetroArch"

    override fun download() {
        println("Starting $emulatorName Download")

        val downloadables = mutableListOf<Pair<LocalDateTime, String>>()

        Jsoup.connect("${BASE_RETROARCH_URL}/nightly/windows/x86_64/")
                .get()
                .getElementsByTag("tr")
                .filter{ it.getElementsByClass("fb-n")[0].text().contains("_RetroArch")}
                .forEach {
                    val nameElement = it.getElementsByClass("fb-n")[0]
                    val url = nameElement.getElementsByTag("a").attr("href")
                    val date = LocalDateTime.parse(it.getElementsByClass("fb-d")[0].text(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    downloadables += date to url
                }

        val downloadPath = BASE_RETROARCH_URL + downloadables.sortedByDescending { (date, _) -> date }.first().second

        val tempFile = createTempFile(createTempDirectory("retroarch"), "retroarch", ".7z").toFile()

        downloadPath.httpDownload().destination{ _, _ -> tempFile }.response()

        unpack(tempFile, arrayOf("retroarch_debug"))

        println("Finished $emulatorName")
    }
}