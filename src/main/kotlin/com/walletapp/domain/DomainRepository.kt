package com.walletapp.domain

interface DomainRepository<T, ID> {
    suspend fun load(id: ID): T
    suspend fun save(aggregate: T)
}