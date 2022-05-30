package com.walletapp.application.external

open class NotFoundException : RuntimeException {
    val messageArgs: Array<out Any>

    constructor(messageCode: String) : super(messageCode) {
        this.messageArgs = arrayOf()
    }
}