package com.shazam.fork.injector.store

import com.shazam.fork.injector.GsonInjector.gson
import com.shazam.fork.injector.system.FileManagerInjector.fileManager
import com.shazam.fork.store.TestCaseStore

object TestCaseStoreInjector {
    private val INSTANCE by lazy { TestCaseStore(fileManager(), gson()) }

    @JvmStatic
    fun testCaseStore(): TestCaseStore {
        return INSTANCE
    }
}
