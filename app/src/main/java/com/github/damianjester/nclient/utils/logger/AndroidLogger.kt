package com.github.damianjester.nclient.utils.logger

import android.util.Log

class AndroidLogger : Logger {
    override fun v(tag: LogTag, message: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.v(tag.value, message, throwable)
        } else {
            Log.v(tag.value, message)
        }
    }

    override fun d(tag: LogTag, message: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.d(tag.value, message, throwable)
        } else {
            Log.d(tag.value, message)
        }
    }

    override fun i(tag: LogTag, message: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.i(tag.value, message, throwable)
        } else {
            Log.i(tag.value, message)
        }
    }

    override fun w(tag: LogTag, message: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.w(tag.value, message, throwable)
        } else {
            Log.w(tag.value, message)
        }
    }

    override fun e(tag: LogTag, message: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.e(tag.value, message, throwable)
        } else {
            Log.e(tag.value, message)
        }
    }
}
