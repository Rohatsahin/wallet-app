package com.walletapp.domain.wallet.history

import java.math.BigDecimal
import java.time.Instant

data class WalletChangeHistory(
    val walletId: String,
    val amount: BigDecimal,
    val dateTime: Instant
) : Comparable<WalletChangeHistory> {

    override fun compareTo(other: WalletChangeHistory): Int {
        return this.dateTime.compareTo(other.dateTime)
    }
}
