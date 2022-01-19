package service

import QuoteEvent
import Quotes
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal

class QuoteService {
    fun createQuote(event: QuoteEvent) {
        transaction {
            Quotes.insertAndGetId {
                it[isin] = event.data.isin
                it[price] = BigDecimal.valueOf(event.data.price)
                it[createdAt] = event.data.createdAt
            }
        }
    }
}