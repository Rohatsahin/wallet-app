package com.walletapp.infrastructure.api

import com.walletapp.application.query.GetWalletChangeHistoryQuery
import com.walletapp.infrastructure.api.model.request.CreateAmountRequest
import com.walletapp.infrastructure.api.model.response.GetWalletHistoryResponse
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController("wallet-controller")
@Api(value = "wallet-controller", description = "update wallet amount and fetch historical transaction of operations")
@RequestMapping("/wallet")
@Profile("!test")
class WalletController(private val applicationEventPublisher: ApplicationEventPublisher) {

    @PostMapping("/{walletId}")
    @ApiOperation("add amount to wallet")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun addAmount(
        @PathVariable("walletId") @ApiParam(
            "walletId for operations",
            defaultValue = "test-123e4567-e89b-42d3-a456-556642440000"
        ) walletId: String,
        @RequestBody request: CreateAmountRequest
    ) = withContext(MDCContext()) {
        applicationEventPublisher.publishEvent(request.toCommand(walletId))
    }


    @GetMapping("/{walletId}/get-wallet-amount-history")
    @ApiOperation("get wallet amount history between time range")
    suspend fun getWalletHistory(
        @PathVariable("walletId") @ApiParam(
            "walletId for searching history changes",
            defaultValue = "test-123e4567-e89b-42d3-a456-556642440000"
        ) walletId: String,
        @RequestParam("startDatetime") @ApiParam(
            "wallet history changes startDatetime",
            defaultValue = "2011-10-05T10:48:01+00:00"
        ) startDatetime: Instant,
        @RequestParam("endDatetime") @ApiParam(
            "wallet history changes endDatetime",
            defaultValue = "2011-10-05T18:48:02+00:00"
        ) endDatetime: Instant
    ): List<GetWalletHistoryResponse> = withContext(MDCContext()) {
        val response = mutableListOf<GetWalletHistoryResponse>()
        applicationEventPublisher.publishEvent(
            GetWalletChangeHistoryQuery(
                walletId,
                startDatetime,
                endDatetime,
                response
            )
        )

        return@withContext response
    }
}