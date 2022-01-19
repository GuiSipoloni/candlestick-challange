import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import conf.DBConfig
import service.InstrumentService
import service.QuoteService

fun main() {
  println("starting up")

  val server = Server()
  val instrumentStream = InstrumentStream()
  val quoteStream = QuoteStream()
  val instrumentService: InstrumentService = InstrumentService()
  val quoteService: QuoteService = QuoteService()

  DBConfig.startUpDBConnection()

  instrumentStream.connect { event ->
    instrumentService.createOrDelete(event)
  }

  quoteStream.connect { event ->
    println(event)
    quoteService.createQuote(event)
  }

  server.start()
}

val jackson: ObjectMapper =
  jacksonObjectMapper()
    .registerModule(JavaTimeModule())
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
