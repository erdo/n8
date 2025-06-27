package com.kmpfoo.logging

import co.early.fore.core.WorkMode
import co.early.fore.core.WorkMode.ASYNCHRONOUS
import co.early.fore.core.delegate.Delegate
import co.early.fore.core.logging.Logger
import co.early.fore.core.logging.MultiplatformLogger
import co.early.fore.core.time.SystemTimeWrapper
import co.early.fore.core.time.getSystemTimeWrapper

class WarningsAndErrorsDelegate (
    tagPrefix: String? = null,
    override val workMode: WorkMode = ASYNCHRONOUS,
    override val logger: Logger = WarningsAndErrorsLogger(MultiplatformLogger(tagPrefix)),
    override val systemTimeWrapper: SystemTimeWrapper = getSystemTimeWrapper()
    ) : Delegate {

        // this is for iOS target benefit which doesn't like default parameters in constructors
        constructor(tagPrefix: String) : this(
            tagPrefix,
            ASYNCHRONOUS,
            WarningsAndErrorsLogger(MultiplatformLogger(tagPrefix)),
            getSystemTimeWrapper()
        )
        constructor() : this(null, ASYNCHRONOUS, WarningsAndErrorsLogger(MultiplatformLogger(null)), getSystemTimeWrapper())
    }