package com.github.darkpiv.gstorage

/**
 * Interceptor for all logs happens in the library
 */
interface LogInterceptor {

    /**
     * Will be triggered each time when a log is written
     *
     * @param message is the log message
     */
    fun onLog(message: String)
}
