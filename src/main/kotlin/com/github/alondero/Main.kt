package com.github.alondero

import com.github.alondero.downloader.DolphinDownloader
import com.github.alondero.downloader.RetroarchDownloader
import com.github.alondero.downloader.Rpcs3Downloader
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import net.sf.sevenzipjbinding.SevenZip

fun main(args: Array<String>) = runBlocking {
    SevenZip.initSevenZipFromPlatformJAR()
    val jobs = listOf(
        RetroarchDownloader(),
        Rpcs3Downloader(),
        DolphinDownloader()
    ).map { launch { it.download() } }

    jobs.forEach { it.join() }
}
