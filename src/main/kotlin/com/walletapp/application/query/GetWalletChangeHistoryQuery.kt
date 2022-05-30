package com.walletapp.application.query

import com.walletapp.domain.wallet.history.WalletChangeHistoryRepository
import com.walletapp.infrastructure.api.model.response.GetWalletHistoryResponse
import kotlinx.coroutines.runBlocking
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import java.time.Instant

data class GetWalletChangeHistoryQuery(
    val walletId: String,
    val startDateTime: Instant,
    val endDateTime: Instant,
    val queryResponse: MutableList<GetWalletHistoryResponse>
) : ApplicationEvent(queryResponse)

@Component
class GetWalletChangeHistoryQueryHandler(
    private val walletChangeHistoryRepository: WalletChangeHistoryRepository
) : ApplicationListener<GetWalletChangeHistoryQuery> {

    override fun onApplicationEvent(query: GetWalletChangeHistoryQuery) {

        // spring context is not concurred so we must use runBlocking
        runBlocking {
            val historyResponses = walletChangeHistoryRepository
                .search(query.walletId, query.startDateTime, query.endDateTime)
                .map { GetWalletHistoryResponse(it.dateTime, it.amount) }

            // add all results to query response
            query.queryResponse.addAll(historyResponses)
        }
    }
}



