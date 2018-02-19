package com.shazam.fork.stat

import com.google.gson.Gson
import com.shazam.fork.model.Device
import com.shazam.fork.model.Pool
import com.shazam.fork.system.io.FileManager
import com.shazam.fork.system.io.FileType
import java.io.FileReader
import java.io.FileWriter

class TestExecutionReporter(val fileManager: FileManager,
                            val gson: Gson) {

    companion object {
        val filetype = FileType.TEST_EXECUTION_REPORT
    }

    fun add(pool: Pool, device: Device, execution: TestExecution) {
        val file = fileManager.createFile(filetype, pool, device, execution.test)
        FileWriter(file).use {
            gson.toJson(execution, it)
            it.flush()
        }
    }

    fun getTests(pool: String, device: Device): List<TestExecution> {
        return fileManager.getFiles(filetype, pool, device.safeSerial).map {
            gson.fromJson(FileReader(it), TestExecution::class.java)
        }
    }
}
