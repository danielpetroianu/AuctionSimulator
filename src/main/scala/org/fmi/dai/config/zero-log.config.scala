package org.fmi.dai.config

import com.dongxiguo.zeroLog.Filter
import com.dongxiguo.zeroLog.formatters.SimpleFormatter
import com.dongxiguo.zeroLog.appenders.ConsoleAppender


// Set global default logging level to Warning, and send logs to ConsoleLogger
object ZeroLoggerFactory {
  final def newLogger(singleton: Singleton) = (Filter.All, SimpleFormatter, ConsoleAppender)
}