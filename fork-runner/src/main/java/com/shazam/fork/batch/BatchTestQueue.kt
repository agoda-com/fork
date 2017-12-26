package com.shazam.fork.batch

import com.shazam.fork.batch.tasks.TestTask
import java.util.concurrent.PriorityBlockingQueue

class BatchTestQueue(initialCapacity : Int) : PriorityBlockingQueue<TestTask>(initialCapacity, TestTaskComparator())