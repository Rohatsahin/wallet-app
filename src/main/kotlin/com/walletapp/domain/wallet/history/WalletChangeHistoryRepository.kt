package com.walletapp.domain.wallet.history

import java.time.Instant

interface WalletChangeHistoryRepository {
    fun upsertHistory(history: WalletChangeHistory)
    fun search(walletId: String, startDatetime: Instant, endDatetime: Instant): Set<WalletChangeHistory>
}