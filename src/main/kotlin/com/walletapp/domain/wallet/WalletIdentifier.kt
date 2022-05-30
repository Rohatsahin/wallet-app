package com.walletapp.domain.wallet


data class WalletIdentifier(private val id: String) {

    // this is persistence system identifier like as IBAN
    val identifier: String
        get() {
            return id;
        }
}
