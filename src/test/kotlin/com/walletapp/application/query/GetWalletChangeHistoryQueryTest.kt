package com.walletapp.application.query

import com.nhaarman.mockito_kotlin.given
import com.walletapp.domain.wallet.history.WalletChangeHistory
import com.walletapp.domain.wallet.history.WalletChangeHistoryRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant

@ExtendWith(MockitoExtension::class)
internal class GetWalletChangeHistoryQueryHandlerTest {

    @Mock
    private lateinit var walletChangeHistoryRepository: WalletChangeHistoryRepository

    @InjectMocks
    private lateinit var getWalletChangeHistoryQueryHandler: GetWalletChangeHistoryQueryHandler

    @Test
    fun `it should return wallet history to existing date range`() {
        // given
        val query =
            GetWalletChangeHistoryQuery("test", Instant.now(), Instant.now().plus(Duration.ofDays(3)), mutableListOf())

        val changeHistory = WalletChangeHistory("test", BigDecimal.ONE, Instant.now().plus(Duration.ofHours(2)))
        given(walletChangeHistoryRepository.search(query.walletId, query.startDateTime, query.endDateTime)).willReturn(
            setOf(changeHistory)
        )

        // when
        getWalletChangeHistoryQueryHandler.onApplicationEvent(query)

        // then
        assertThat(query.queryResponse.size).isEqualTo(1)
        assertThat(query.queryResponse[0].amount).isEqualTo(changeHistory.amount)
        assertThat(query.queryResponse[0].dateTime).isEqualTo(changeHistory.dateTime)
    }
}