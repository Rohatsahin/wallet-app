package com.walletapp.domain.wallet.command

import com.walletapp.domain.wallet.WalletIdentifier
import java.math.BigDecimal
import java.time.Instant

data class CreateWalletCommand(
    val walletId: WalletIdentifier,
    val amount: BigDecimal,
    val snapshotDate: Instant
)
