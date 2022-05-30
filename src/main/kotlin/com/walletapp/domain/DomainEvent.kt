package com.walletapp.domain

interface DomainEvent {
    fun getType(): String
}