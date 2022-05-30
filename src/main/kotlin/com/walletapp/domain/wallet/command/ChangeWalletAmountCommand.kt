package com.walletapp.domain.wallet.command

import java.math.BigDecimal
import java.time.Instant

data class ChangeWalletAmountCommand(
    val amount: BigDecimal,
    val snapshotDate: Instant
)
