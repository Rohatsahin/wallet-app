package com.walletapp.domain.wallet.event

import com.walletapp.domain.DomainEvent
import java.math.BigDecimal
import java.time.Instant

data class WalletCreatedEvent(
    val walletId: String,
    val amount: BigDecimal,
    val snapshotDate: Instant,
    val version: Long
) : DomainEvent {
    override fun getType(): String = "wallet.created"
}