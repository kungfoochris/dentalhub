package com.abhiyantrik.dentalhub.logging

import android.content.Context
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.html.HTMLLayout
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.encoder.LayoutWrappingEncoder
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import ch.qos.logback.core.util.FileSize
import ch.qos.logback.core.util.StatusPrinter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import timber.log.Timber


class FileTree(ctx:Context): Timber.Tree(){
    private val mLogger: Logger = LoggerFactory.getLogger(FileTree::class.java)
    private val LOG_PREFIX = "dentalhub"

    init {
        val logDirectory: String = ""+ctx.filesDir+ "/logs";
        configureLogger(logDirectory);
    }

    private fun configureLogger(logDirectory: String) {
        val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
        loggerContext.reset()

        val rollingFileAppender = RollingFileAppender<ILoggingEvent>()
        rollingFileAppender.context = loggerContext
        rollingFileAppender.isAppend = true
        rollingFileAppender.file = "$logDirectory/$LOG_PREFIX-latest.html"
        // /data/data/com.abhiyantrik.dentalhub/files/logs/dentalhub-latest.html

        val fileNamingPolicy = SizeAndTimeBasedFNATP<ILoggingEvent>()
        fileNamingPolicy.context = loggerContext
        fileNamingPolicy.setMaxFileSize(FileSize(1000000))

        val rollingPolicy = TimeBasedRollingPolicy<ILoggingEvent>()
        rollingPolicy.context = loggerContext
        rollingPolicy.fileNamePattern = "$logDirectory/$LOG_PREFIX.%d{yyyy-MM-dd}.%i.html"
        rollingPolicy.maxHistory = 5
        rollingPolicy.timeBasedFileNamingAndTriggeringPolicy = fileNamingPolicy
        rollingPolicy.setParent(rollingFileAppender) // parent and context required!

        rollingPolicy.start()

        val htmlLayout = HTMLLayout()
        htmlLayout.context = loggerContext
        htmlLayout.pattern = "%d{HH:mm:ss.SSS}%level%thread%msg"
        htmlLayout.start()

        val encoder = LayoutWrappingEncoder<ILoggingEvent>()
        encoder.context = loggerContext
        encoder.layout = htmlLayout
        encoder.start()
        rollingFileAppender.rollingPolicy = rollingPolicy;
        rollingFileAppender.encoder = encoder;
        rollingFileAppender.start();

        val root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
        root.level = Level.DEBUG
        root.addAppender(rollingFileAppender)

        StatusPrinter.print(loggerContext);

    }
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        mLogger.debug("$priority : $tag : $message")
    }

}