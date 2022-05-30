package com.walletapp.application.command

import com.walletapp.application.external.NotFoundException
import com.walletapp.domain.wallet.Wallet
import com.walletapp.domain.wallet.WalletIdentifier
import com.walletapp.domain.wallet.WalletRepository
import com.walletapp.domain.wallet.command.ChangeWalletAmountCommand
import com.walletapp.domain.wallet.command.CreateWalletCommand
import kotlinx.coroutines.runBlocking
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.Instant

data class SaveWalletAmountCommand(
    val walletId: String,
    val amount: BigDecimal,
    val dateTime: Instant
) : ApplicationEvent(walletId) {

    val walletIdentifier: WalletIdentifier
        get() {
            return WalletIdentifier(walletId)
        }
}

@Component
class SaveWalletAmountCommandHandler(
    private val walletRepository: WalletRepository
) : ApplicationListener<SaveWalletAmountCommand> {

    override fun onApplicationEvent(command: SaveWalletAmountCommand) = with(command) {

        // spring context is not concurred so we must use runBlocking
        runBlocking {
            val wallet = try {
                // fetch and if already exist change amount
                val existingWallet = walletRepository.load(walletIdentifier)
                existingWallet.changeWalletAmount(ChangeWalletAmountCommand(amount, dateTime))
                existingWallet
            } catch (ex: NotFoundException) {
                // if already not initialized create new unique wallet identifier and wallet
                val walletIdentifier = walletRepository.createNewWalletIdentifier()
                Wallet.create(CreateWalletCommand(walletIdentifier, amount, dateTime))
            }

            walletRepository.save(wallet)
        }
    }
}
