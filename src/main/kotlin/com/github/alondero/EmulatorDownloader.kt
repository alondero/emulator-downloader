package com.github.alondero

import net.sf.sevenzipjbinding.ArchiveFormat
import net.sf.sevenzipjbinding.ExtractOperationResult
import net.sf.sevenzipjbinding.SevenZip
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

val AUTO_DETECT_ARCHIVE_FORMAT: ArchiveFormat? = null

interface EmulatorDownloader {
    val emulatorName: String

    fun download()

    fun createOrReplace(pathLoc: String, bytes: ByteArray) {
        var tempFile: File? = null
        val path = Paths.get(pathLoc)

        if (path.toFile().exists()) {
            tempFile = Files.move(path, path.parent.resolve("temp")).toFile()
        }

        try {
            Files.write(path, bytes)
            tempFile?.delete()
        } catch (e: IOException) {
            if (tempFile != null) {
                Files.move(tempFile.toPath(), path)
            }
        }
    }

    fun unpack(tempFile: File, filters: Array<String>, includeEmulatorName: Boolean = true) {
        RandomAccessFile(tempFile, "r").use { file ->
            SevenZip.openInArchive(AUTO_DETECT_ARCHIVE_FORMAT, RandomAccessFileInStream(file)).use {
                filters.fold(it.simpleInterface.archiveItems.toList()) {items, filter -> items.filterNot { it.path.contains(filter) }}
                    .forEach { item ->
                        var size = 0L
                        val bytes = ByteArrayOutputStream()
                        val result = item.extractSlow { data ->
                            data.size.also {
                                bytes.write(data)
                                size += it
                            }
                        }

                        val name = "S:\\Emulators\\" + (if (includeEmulatorName) "$emulatorName\\" else "") + item.path

                        when (result) {
                            ExtractOperationResult.OK ->
                                when {
                                    (item.isFolder) -> Paths.get(name).toFile().mkdirs()
                                    else -> createOrReplace(name, bytes.toByteArray())
                                }
                            else -> println("Error extracting item: $result")
                        }
                    }
            }
        }
    }
}

