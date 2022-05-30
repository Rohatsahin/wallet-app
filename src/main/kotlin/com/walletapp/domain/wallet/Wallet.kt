package com.walletapp.domain.wallet

import com.walletapp.domain.DomainEvent
import com.walletapp.domain.DomainException
import com.walletapp.domain.wallet.command.ChangeWalletAmountCommand
import com.walletapp.domain.wallet.command.CreateWalletCommand
import com.walletapp.domain.wallet.event.WalletChangedEvent
import com.walletapp.domain.wallet.event.WalletCreatedEvent
import java.math.BigDecimal
import java.time.Instant

data class Wallet(
    // unchanged filed mutation not available
    val walletId: WalletIdentifier,

    // domain centric filed, drive by business
    var amount: BigDecimal,
    var snapshotDate: Instant,

    // audit and concurrency control fields
    val creationDate: Instant = Instant.now(),
    var updatedDate: Instant? = null,
    var version: Long = 0,

    // events for change capture
    var events: MutableList<DomainEvent> = mutableListOf()
) {

    companion object {
        fun create(command: CreateWalletCommand): Wallet {
            // create wallet
            val wallet = Wallet(
                command.walletId,
                command.amount,
                command.snapshotDate
            )

            // add event
            wallet.events.add(
                WalletCreatedEvent(
                    wallet.walletId.identifier,
                    wallet.amount,
                    wallet.snapshotDate,
                    wallet.version
                )
            )

            return wallet
        }
    }

    fun changeWalletAmount(command: ChangeWalletAmountCommand) {

        // incoming change is out date fail fast
        if (command.snapshotDate.isBefore(this.snapshotDate)) {
            throw DomainException("wallet.change.request.is.outdated")
        }

        // fast return if request already processed and idempotency
        if (command.snapshotDate.compareTo(this.snapshotDate) == 0) {
            return
        }

        // calculate new amount
        val newAmount = this.amount + command.amount

        this.amount = newAmount
        this.snapshotDate = command.snapshotDate
        this.updatedDate = Instant.now()
        this.version = ++version

        // add change event
        this.events.add(
            WalletChangedEvent(
                this.walletId.identifier,
                this.amount,
                this.snapshotDate,
                this.version
            )
        )
    }
}
