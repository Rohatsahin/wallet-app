package com.walletapp.application.command

import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.verify
import com.walletapp.application.external.NotFoundException
import com.walletapp.domain.wallet.Wallet
import com.walletapp.domain.wallet.WalletIdentifier
import com.walletapp.domain.wallet.WalletRepository
import com.walletapp.domain.wallet.command.CreateWalletCommand
import com.walletapp.domain.wallet.event.WalletChangedEvent
import com.walletapp.domain.wallet.event.WalletCreatedEvent
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.time.Instant

@ExtendWith(MockitoExtension::class)
internal class SaveWalletAmountCommandHandlerTest {

    @Mock
    private lateinit var walletRepository: WalletRepository

    @InjectMocks
    private lateinit var saveWalletAmountCommandHandler: SaveWalletAmountCommandHandler

    @Test
    fun `should create wallet when wallet not found`() = runBlockingTest {
        // given
        val command = SaveWalletAmountCommand("test", BigDecimal.TEN, Instant.now())

        given(walletRepository.load(command.walletIdentifier)).willThrow(NotFoundException::class.java)
        given(walletRepository.createNewWalletIdentifier()).willReturn(WalletIdentifier("test"))

        // when
        saveWalletAmountCommandHandler.onApplicationEvent(command)

        // then
        val walletCaptor = argumentCaptor<Wallet>()
        verify(walletRepository).save(walletCaptor.capture())

        val createdWallet = walletCaptor.firstValue

        Assertions.assertThat(createdWallet.events.size).isEqualTo(1)

        val event = createdWallet.events[0] as WalletCreatedEvent
        Assertions.assertThat(event.walletId).isEqualTo("test")
        Assertions.assertThat(event.amount).isEqualTo(BigDecimal.TEN)
        Assertions.assertThat(event.version).isEqualTo(0)

        Assertions.assertThat(createdWallet.amount).isEqualTo(BigDecimal.TEN)
        Assertions.assertThat(createdWallet.walletId.identifier).isEqualTo("test")

    }

    @Test
    fun `should update wallet amount`() = runBlockingTest {
        // given
        val existingWallet = Wallet.create(CreateWalletCommand(WalletIdentifier("test"), BigDecimal.TEN, Instant.now()))
        existingWallet.events.clear()

        val command = SaveWalletAmountCommand("test", BigDecimal.TEN, Instant.now())

        given(walletRepository.load(command.walletIdentifier)).willReturn(existingWallet)

        // when
        saveWalletAmountCommandHandler.onApplicationEvent(command)

        // then
        val walletCaptor = argumentCaptor<Wallet>()
        verify(walletRepository).save(walletCaptor.capture())

        val savedWallet = walletCaptor.firstValue

        Assertions.assertThat(savedWallet.events.size).isEqualTo(1)

        val event = savedWallet.events[0] as WalletChangedEvent
        Assertions.assertThat(event.walletId).isEqualTo("test")
        Assertions.assertThat(event.amount).isEqualTo(BigDecimal.valueOf(20))
        Assertions.assertThat(event.version).isEqualTo(1L)

        Assertions.assertThat(savedWallet.amount).isEqualTo(BigDecimal.valueOf(20))
        Assertions.assertThat(savedWallet.walletId.identifier).isEqualTo("test")
    }
}