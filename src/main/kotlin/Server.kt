import model.CandlestickManagerImpl
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.Netty
import org.http4k.server.asServer

class Server(
  port: Int = 9000,
) {

  lateinit var  service : CandlestickManager


  private val routes = routes(
    "candlesticks" bind Method.GET to { getCandlesticks(it) }
  )

  private val server: Http4kServer = routes.asServer(Netty(port))

  fun start() {
    service = CandlestickManagerImpl()
    server.start()
  }

  private fun getCandlesticks(req: Request): Response {
    val isin = req.query("isin")
      ?: return Response(Status.BAD_REQUEST).body("{'reason': 'missing_isin'}")

    val candlesticks =  service.getCandlesticks(isin)
    if (candlesticks.isEmpty()){
      return Response(Status.NOT_FOUND).body("{'reason': 'No Quotes found for this isin'}")
    }

    val body = jackson.writeValueAsBytes(candlesticks)

    return Response(Status.OK).body(body.inputStream())
  }
}
