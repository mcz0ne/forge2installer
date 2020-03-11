package moe.z0ne.mc.forge2installer

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.ConsoleAppender
import net.minecraftforge.installer.SimpleInstaller
import net.minecraftforge.installer.actions.ClientInstall
import net.minecraftforge.installer.actions.ProgressCallback
import net.minecraftforge.installer.json.Util
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*
import java.util.function.Predicate


fun newPattern(lc: LoggerContext, colored: Boolean = false): PatternLayoutEncoder {
    val pattern = PatternLayoutEncoder()
    pattern.context = lc
    pattern.pattern = if (colored) {
        "%highlight(%.-1p) %gray([%d{HH:mm:ss.SSS}]) %cyan(\\(%t\\)) %magenta(%c{20}): %m%n"
    } else {
        "%.-1p [%d{HH:mm:ss.SSS}] \\(%t\\) %c{20}: %m%n"
    }
    pattern.start()

    return pattern
}

fun configureLogger(): Logger {
    val lc = LoggerFactory.getILoggerFactory() as LoggerContext

    val consoleAppender = ConsoleAppender<ILoggingEvent>()
    consoleAppender.name = "console"
    consoleAppender.target = "System.out"
    consoleAppender.encoder = newPattern(lc, true)
    consoleAppender.context = lc
    consoleAppender.start()
    val log = lc.getLogger("root")
    log.isAdditive = false
    log.detachAndStopAllAppenders()
    log.addAppender(consoleAppender)
    log.level = Level.TRACE

    return log
}

fun main(args: Array<String>) {
    val logger = configureLogger()
    logger.info("Starting forge 2 installer")
    val target = File(args[0])
    val mon = ProgressCallback { message, priority -> logger.debug("{}: {}", priority.toString(), message) }
    val optPred = Predicate { _: String? -> true }

    // disable download progress bar
    SimpleInstaller.headless = true
    val clientInstall = ClientInstall(Util.loadInstallProfile(), mon)
    clientInstall.run(target, optPred)
}