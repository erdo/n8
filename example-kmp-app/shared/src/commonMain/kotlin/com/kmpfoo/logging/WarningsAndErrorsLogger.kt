package com.kmpfoo.logging

import co.early.fore.core.logging.Logger
import co.early.fore.core.logging.MultiplatformLogger

class WarningsAndErrorsLogger(val multiplatformLogger: MultiplatformLogger) : Logger {
    override fun e(message: String) {
        multiplatformLogger.e(message)
    }

    override fun w(message: String) {
        multiplatformLogger.w(message)
    }

    override fun i(message: String) {
    }

    override fun d(message: String) {
    }

    override fun v(message: String) {
    }

    override fun e(tag: String, message: String) {
        multiplatformLogger.e(tag, message)
    }

    override fun w(tag: String, message: String) {
        multiplatformLogger.w(tag, message)
    }

    override fun i(tag: String, message: String) {
    }

    override fun d(tag: String, message: String) {
    }

    override fun v(tag: String, message: String) {
    }

    override fun e(message: String, throwable: Throwable) {
        multiplatformLogger.e(message, throwable)
    }

    override fun w(message: String, throwable: Throwable) {
        multiplatformLogger.w(message, throwable)
    }

    override fun i(message: String, throwable: Throwable) {
    }

    override fun d(message: String, throwable: Throwable) {
    }

    override fun v(message: String, throwable: Throwable) {
    }

    override fun e(tag: String, message: String, throwable: Throwable) {
        multiplatformLogger.e(tag, message, throwable)
    }

    override fun w(tag: String, message: String, throwable: Throwable) {
        multiplatformLogger.w(tag, message, throwable)
    }

    override fun i(tag: String, message: String, throwable: Throwable) {
    }

    override fun d(tag: String, message: String, throwable: Throwable) {
    }

    override fun v(tag: String, message: String, throwable: Throwable) {
    }

}