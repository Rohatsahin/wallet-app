package com.walletapp.domain.wallet

import com.walletapp.domain.DomainException
import com.walletapp.domain.wallet.command.ChangeWalletAmountCommand
import com.walletapp.domain.wallet.command.CreateWalletCommand
import com.walletapp.domain.wallet.event.WalletChangedEvent
import com.walletapp.domain.wallet.event.WalletCreatedEvent
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowableOfType
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant

internal class WalletTest {

    @Test
    fun `it should create wallet`() {
        // given
        val command = CreateWalletCommand(WalletIdentifier("test"), BigDecimal.TEN, Instant.now())

        // when
        val createdWallet = Wallet.create(command)

        // then
        assertThat(createdWallet.events.size).isEqualTo(1)

        val event = createdWallet.events[0] as WalletCreatedEvent
        assertThat(event.walletId).isEqualTo("test")
        assertThat(event.amount).isEqualTo(BigDecimal.TEN)
        assertThat(event.version).isEqualTo(0)

        assertThat(createdWallet.amount).isEqualTo(BigDecimal.TEN)
        assertThat(createdWallet.walletId.identifier).isEqualTo("test")
    }

    @Test
    fun `it should change wallet amount`() {
        // given
        val existingWallet = Wallet.create(CreateWalletCommand(WalletIdentifier("test"), BigDecimal.TEN, Instant.now()))
        existingWallet.events.clear()

        val changeCommand = ChangeWalletAmountCommand(BigDecimal.TEN, Instant.now())

        // when
        existingWallet.changeWalletAmount(changeCommand)

        // then
        assertThat(existingWallet.events.size).isEqualTo(1)

        val event = existingWallet.events[0] as WalletChangedEvent
        assertThat(event.walletId).isEqualTo("test")
        assertThat(event.amount).isEqualTo(BigDecimal.valueOf(20))
        assertThat(event.version).isEqualTo(1L)

        assertThat(existingWallet.amount).isEqualTo(BigDecimal.valueOf(20))
        assertThat(existingWallet.walletId.identifier).isEqualTo("test")
    }

    @Test
    fun `it should throw outdated exception when change command date time is outdated`() {
        // given
        val existingWallet = Wallet.create(CreateWalletCommand(WalletIdentifier("test"), BigDecimal.TEN, Instant.now()))
        existingWallet.events.clear()

        val changeCommand = ChangeWalletAmountCommand(BigDecimal.TEN, Instant.now().minus(Duration.ofHours(1)))

        // when
        val exception =
            catchThrowableOfType({ existingWallet.changeWalletAmount(changeCommand) }, DomainException::class.java)

        // then
        assertThat(exception.message).isEqualTo("wallet.change.request.is.outdated")

        assertThat(existingWallet.events.size).isEqualTo(0)
        assertThat(existingWallet.version).isEqualTo(0L)
        assertThat(existingWallet.amount).isEqualTo(BigDecimal.TEN)
        assertThat(existingWallet.walletId.identifier).isEqualTo("test")
    }
}