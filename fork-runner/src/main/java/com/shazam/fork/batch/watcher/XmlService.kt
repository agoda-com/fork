package com.shazam.fork.batch.watcher

import com.android.ddmlib.testrunner.TestIdentifier
import com.shazam.fork.model.Device
import com.shazam.fork.model.Pool
import com.shazam.fork.summary.TestSuite
import com.shazam.fork.system.io.FileManager
import com.shazam.fork.system.io.FileType
import org.simpleframework.xml.core.Persister
import java.io.File

class XmlService(private val fileManager: FileManager) {
    private val parser = Persister()

    fun splitAndSave(file: File) {
        val suite = parser.read(TestSuite::class.java, file, false)
        suite.testCase.forEach {
            val arr = file.absolutePath.split('/').dropLast(1)
            val serial = arr.last()
            val poolName = arr.dropLast(1).last()
            val device = Device.Builder().withSerial(serial).build()
            val pool = Pool.Builder().withName(poolName).build()
            val output = fileManager.createFile(FileType.TEST, pool, device, TestIdentifier(it.classname, it.name))
            parser.write(it, output)
            file.delete()
        }
    }
}
