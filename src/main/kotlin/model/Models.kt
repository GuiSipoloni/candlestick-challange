import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.select
import java.time.LocalDateTime

data class InstrumentEvent(val type: Type, val data: Instrument) {
  enum class Type {
    ADD,
    DELETE
  }
}

object Instruments: IntIdTable() {
  val isin = varchar("isin", 50).uniqueIndex("isin_unique")
  val description = varchar("description", 255)
}
typealias ISIN = String

data class Instrument(val id:Int? = null, val isin:ISIN, val description:String)

data class QuoteEvent(val data: Quote)

data class Quote(val id: Int? = null, val isin:ISIN, val price:Price, val createdAt: LocalDateTime = LocalDateTime.now())

typealias Price = Double

object Quotes: IntIdTable() {
  val isin = varchar("isin", 50)
  val price = decimal("price", 10, 2)
  val createdAt = datetime("created_at").default(LocalDateTime.now())

  fun toQuote(row: ResultRow): Quote =
    Quote(
      id = row[Quotes.id].value,
      isin = row[Quotes.isin],
      price = row[Quotes.price].toDouble(),
      createdAt = row[Quotes.createdAt]
    )
  fun getById(id: Int?): Quote {
    return select{ Quotes.id eq id }.map { it -> Quotes.toQuote(it) }.get(0)
  }

}

interface CandlestickManager {
  fun getCandlesticks(isin: String): List<Candlestick>
}

data class Candlestick(
val openTimestamp: LocalDateTime,
var closeTimestamp: LocalDateTime,
val openPrice: Price,
var highPrice: Price,
var lowPrice: Price,
var closingPrice: Price
)
