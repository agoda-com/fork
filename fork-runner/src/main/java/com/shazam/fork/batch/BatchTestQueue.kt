package com.shazam.fork.batch

import com.shazam.fork.batch.tasks.TestTask
import java.util.concurrent.PriorityBlockingQueue

class BatchTestQueue : PriorityBlockingQueue<TestTask>(100, TestTaskComparator())