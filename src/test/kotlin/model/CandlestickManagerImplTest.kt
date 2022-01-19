package model

import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal
import java.time.LocalDateTime
import Candlestick
import conf.DBConfig
import org.h2.command.ddl.TruncateTable
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

internal class CandlestickManagerImplTest {

    @BeforeEach
    fun createDB(){
        DBConfig.startUpDBConnection()
    }

    @AfterEach
    fun cleanDB(){
        transaction {
            TransactionManager.current().exec("truncate table quotes")
        }
    }

    @Test
    fun getCandlesticksLast30min() {
        // given
        val openDate = LocalDateTime.now()
        val closeDate = openDate.plusSeconds(1)
        val openPrice = 10.2
        val closePrice = 20.2
        transaction {
            Quotes.insertAndGetId {
                it[isin] = "test"
                it[price] = BigDecimal.valueOf(openPrice)
                it[createdAt] = openDate
            }
            Quotes.insertAndGetId {
                it[isin] = "test"
                it[price] = BigDecimal.valueOf(closePrice)
                it[createdAt] = closeDate
            }
        }
        // when
        val candlestickManagerImpl = CandlestickManagerImpl()
        val result = candlestickManagerImpl.getCandlesticks("test")

        //then
        val candlestick = createCandlestick(openDate, closeDate, openPrice, closePrice, closePrice, openPrice)
        assertEquals(listOf(candlestick), result)
    }

    @Test
    fun getCandlesticksLastThan30min() {
        // given
        val openDate = LocalDateTime.now().minusMinutes(40)
        val closeDate = openDate.plusSeconds(1)
        val openPrice = 10.2
        val closePrice = 20.2
        transaction {
            Quotes.insertAndGetId {
                it[isin] = "test"
                it[price] = BigDecimal.valueOf(openPrice)
                it[createdAt] = openDate
            }
            Quotes.insertAndGetId {
                it[isin] = "test"
                it[price] = BigDecimal.valueOf(closePrice)
                it[createdAt] = closeDate
            }
        }
        // when
        val candlestickManagerImpl = CandlestickManagerImpl()
        val result = candlestickManagerImpl.getCandlesticks("test")

        //then
        val candlestick = createCandlestick(openDate, closeDate, openPrice, closePrice, closePrice, openPrice)
        assertEquals(listOf(candlestick),result)
    }

    private fun createCandlestick(openDate: LocalDateTime, closeDate: LocalDateTime, openPrice: Double
                                  , closePrice: Double, highPrice: Double, lowPrice: Double) = Candlestick(
        openTimestamp = openDate,
        closeTimestamp = closeDate,
        openPrice = openPrice,
        closingPrice = closePrice,
        highPrice = highPrice,
        lowPrice = lowPrice
    )
}