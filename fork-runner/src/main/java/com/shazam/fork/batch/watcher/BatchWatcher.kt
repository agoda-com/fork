package com.shazam.fork.batch.watcher

import com.shazam.fork.system.io.FileManager
import com.shazam.fork.system.io.FileType
import com.sun.nio.file.SensitivityWatchEventModifier
import org.slf4j.LoggerFactory

import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import java.nio.file.WatchService
import java.nio.file.attribute.BasicFileAttributes
import java.util.HashMap
import java.util.concurrent.Executors

import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.StandardWatchEventKinds.OVERFLOW

class BatchWatcher(fileManager: FileManager) {

    private val xmlService = XmlService(fileManager)

    private val logger = LoggerFactory.getLogger(BatchWatcher::class.java)
    private val folder = fileManager.createDirectory(FileType.TEST_BATCH)

    private val watcher: WatchService = FileSystems.getDefault().newWatchService()
    private val executor = Executors.newSingleThreadExecutor()

    fun stop() {
        try {
            watcher.close()
        } catch (e: IOException) {
            logger.error("Can't close watcher", e)
        }
        executor.shutdown()
    }

    fun start() {

        val keys = HashMap<WatchKey, Path>()

        val register = { p: Path ->
            if (!p.toFile().exists() || !p.toFile().isDirectory) {
                throw RuntimeException("folder $p does not exist or is not a directory")
            }
            Files.walkFileTree(p, object : SimpleFileVisitor<Path>() {
                override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
                    val watchKey = dir.register(watcher, arrayOf<WatchEvent.Kind<*>>(ENTRY_CREATE), SensitivityWatchEventModifier.HIGH)
                    keys.put(watchKey, dir)
                    return FileVisitResult.CONTINUE
                }
            })
        }

        register.invoke(folder)

        executor.submit {
            while (true) {
                val key: WatchKey = watcher.take()

                val dir = keys[key]
                if (dir == null) {
                    System.err.println("WatchKey $key not recognized!")
                    continue
                }

                key.pollEvents().stream()
                        .filter { e -> e.kind() !== OVERFLOW }
                        .map { e -> (e as WatchEvent<Path>).context() }
                        .forEach { p ->
                            val absPath = dir.resolve(p)
                            if (absPath.toFile().isDirectory) {
                                register(absPath)
                            } else {
                                xmlService.splitAndSave(absPath.toFile())
                            }
                        }

                val valid = key.reset()
                if (!valid) {
                    break
                }
            }
        }
    }
}