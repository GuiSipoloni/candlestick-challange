package model

import Candlestick
import CandlestickManager
import Quotes
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.ResultSet


class CandlestickManagerImpl : CandlestickManager {

    val LAST_30_MINUTES = "30"

    override fun getCandlesticks(isin: String): List<Candlestick> {
        val candlesticks = getLast30MinutesCandleSticks(isin)
        if (candlesticks.isEmpty()) {
            return getLastCandlesticks(isin)
        }
        return candlesticks
    }

    private fun getLast30MinutesCandleSticks(isin: String):List<Candlestick> {
        var candlesticks = listOf<Candlestick>()
        transaction {
            val query = """
                            select min(q.created_at) as openTimestamp
                                 , min(q.id) as openPrice
                                 , max(q.price) as highPrice
                                 , min(q.price) as lowPrice
                                 , max(q.id) as closePrice
                                 , max(q.created_at) as closeTimestamp
                            from quotes q where q.isin = '$isin' and q.created_at >= now() - interval $LAST_30_MINUTES minute
                            group by hour(q.created_at), minute(q.created_at)
                        """

            TransactionManager.current().exec(query) { rs ->
                while (rs.next()) {
                    candlesticks += (generateCandlestick(rs))
                }
            }
        }
        return candlesticks
    }

    private fun getLastCandlesticks(isin: String): List<Candlestick> {
        var candlesticks = listOf<Candlestick>()
        transaction {
            val query = """
                            select min(q.created_at) as openTimestamp
                                 , min(q.id) as openPrice
                                 , max(q.price) as highPrice
                                 , min(q.price) as lowPrice
                                 , max(q.id) as closePrice
                                 , max(q.created_at) as closeTimestamp
                            from quotes q where q.isin = '$isin'
                            group by hour(q.created_at), minute(q.created_at) 
                            limit 1
                        """
            TransactionManager.current().exec(query) { rs ->
                while (rs.next()) {
                    candlesticks += (generateCandlestick(rs))
                }
            }
        }
        return candlesticks
    }

    private fun generateCandlestick(rs: ResultSet): Candlestick {
        val openPrice = Quotes.getById(rs.getInt("openPrice")).price
        val closePrice = Quotes.getById(rs.getInt("closePrice")).price

        val candlestick = Candlestick(
            openTimestamp = rs.getTimestamp("openTimestamp").toLocalDateTime(),
            closeTimestamp = rs.getTimestamp("closeTimestamp").toLocalDateTime(),
            openPrice = openPrice,
            closingPrice = closePrice,
            highPrice = rs.getDouble("highPrice"),
            lowPrice = rs.getDouble("lowPrice")
        )
        println(">>>>>>>>>>>> $candlestick")
        return candlestick
    }
}

