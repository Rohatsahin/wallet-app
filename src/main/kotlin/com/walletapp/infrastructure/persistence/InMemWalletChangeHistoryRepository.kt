package com.walletapp.infrastructure.persistence

import com.walletapp.domain.wallet.history.WalletChangeHistory
import com.walletapp.domain.wallet.history.WalletChangeHistoryRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemWalletChangeHistoryRepository : WalletChangeHistoryRepository {
    private final val dataStore = ConcurrentHashMap<String, TreeSet<WalletChangeHistory>>()

    override fun upsertHistory(history: WalletChangeHistory) {
        synchronized(this) {
            val histories = dataStore[history.walletId] ?: TreeSet<WalletChangeHistory>()
            histories.add(history)
            dataStore.put(history.walletId, histories)
        }
    }

    override fun search(walletId: String, startDatetime: Instant, endDatetime: Instant): Set<WalletChangeHistory> {
        val histories = dataStore[walletId] ?: return emptySet()

        // histories are sorted by date time in tree set
        return histories
            .filter { it.dateTime.isAfter(startDatetime) && it.dateTime.isBefore(endDatetime) }
            .toSet()
    }
}