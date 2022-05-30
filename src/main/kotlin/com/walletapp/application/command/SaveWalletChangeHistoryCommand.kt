package com.walletapp.application.command

import com.walletapp.domain.wallet.history.WalletChangeHistory
import com.walletapp.domain.wallet.history.WalletChangeHistoryRepository
import kotlinx.coroutines.runBlocking
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.Instant

data class SaveWalletChangeHistoryCommand(
    val walletId: String,
    val amount: BigDecimal,
    val dateTime: Instant
) : ApplicationEvent(walletId)

@Component
class SaveWalletChangeHistoryCommandHandler(
    private val walletChangeHistoryRepository: WalletChangeHistoryRepository
) : ApplicationListener<SaveWalletChangeHistoryCommand> {

    override fun onApplicationEvent(command: SaveWalletChangeHistoryCommand) = with(command) {

        // spring context is not concurred so we must use runBlocking
        runBlocking {
            walletChangeHistoryRepository.upsertHistory(WalletChangeHistory(walletId, amount, dateTime))
        }
    }
}
