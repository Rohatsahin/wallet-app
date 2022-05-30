package com.walletapp.infrastructure.persistence

import com.walletapp.application.command.SaveWalletChangeHistoryCommand
import com.walletapp.application.external.NotFoundException
import com.walletapp.domain.wallet.Wallet
import com.walletapp.domain.wallet.WalletIdentifier
import com.walletapp.domain.wallet.WalletRepository
import com.walletapp.domain.wallet.event.WalletChangedEvent
import com.walletapp.domain.wallet.event.WalletCreatedEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemWalletRepository(
    private val applicationEventPublisher: ApplicationEventPublisher
) : WalletRepository {
    private final val logger = LoggerFactory.getLogger(InMemWalletRepository::class.java)

    @Value("\${test.userWalletId}")
    private lateinit var userWalletId: String

    private final val dataStore = ConcurrentHashMap<String, Wallet>()

    override fun createNewWalletIdentifier(): WalletIdentifier {
        return WalletIdentifier(userWalletId)
    }

    override suspend fun load(id: WalletIdentifier): Wallet {
        // load from datasource
        val wallet = dataStore[id.identifier]
            ?: throw NotFoundException("wallet.not.found")

        // return wallet like deserialization
        return Wallet(
            wallet.walletId,
            wallet.amount,
            wallet.snapshotDate,
            wallet.creationDate,
            wallet.updatedDate,
            wallet.version
        )
    }

    override suspend fun save(aggregate: Wallet) {
        dataStore[aggregate.walletId.identifier] = aggregate

        // publish successful persisted wallet data and it's change. if we use rdms or nosql listen
        // database stream and publish to external system like outbox pattern
        publishEventsLikeOutbox(aggregate)
    }


    private fun publishEventsLikeOutbox(wallet: Wallet) {
        if (wallet.events.isNotEmpty()) {
            wallet.events.forEach {
                when (it.getType()) {
                    "wallet.created" -> {
                        val event = it as WalletCreatedEvent
                        applicationEventPublisher.publishEvent(
                            SaveWalletChangeHistoryCommand(
                                event.walletId,
                                event.amount,
                                event.snapshotDate
                            )
                        )
                    }
                    "wallet.changed" -> {
                        val event = it as WalletChangedEvent
                        applicationEventPublisher.publishEvent(
                            SaveWalletChangeHistoryCommand(
                                event.walletId,
                                event.amount,
                                event.snapshotDate
                            )
                        )
                    }
                    else -> {
                        logger.info("unmatched event type  {}", it)
                    }
                }
            }
        }
    }
}