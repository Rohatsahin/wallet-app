package com.walletapp.infrastructure.api.model.request

import com.walletapp.application.command.SaveWalletAmountCommand
import java.math.BigDecimal
import java.time.Instant

data class CreateAmountRequest(
    val amount: BigDecimal,
    val dateTime: Instant
) {
    fun toCommand(walletId: String): SaveWalletAmountCommand {
        return SaveWalletAmountCommand(walletId, amount, dateTime)
    }
}
