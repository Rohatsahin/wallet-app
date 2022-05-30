package com.walletapp.infrastructure.api.model.response

import java.math.BigDecimal
import java.time.Instant

data class GetWalletHistoryResponse(
    val dateTime: Instant,
    val amount: BigDecimal
)
