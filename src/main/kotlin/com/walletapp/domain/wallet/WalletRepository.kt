package com.walletapp.domain.wallet

import com.walletapp.domain.DomainRepository

interface WalletRepository : DomainRepository<Wallet, WalletIdentifier> {
    fun createNewWalletIdentifier(): WalletIdentifier
}