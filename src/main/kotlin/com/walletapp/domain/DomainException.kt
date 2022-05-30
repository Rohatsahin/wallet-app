package com.walletapp.domain

open class DomainException : RuntimeException {
    val messageArgs: Array<out Any>

    constructor(messageCode: String) : super(messageCode) {
        this.messageArgs = arrayOf()
    }
}