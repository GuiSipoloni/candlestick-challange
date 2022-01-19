import com.fasterxml.jackson.module.kotlin.readValue
import org.http4k.client.WebsocketClient
import org.http4k.core.Uri
import org.http4k.websocket.Websocket

class InstrumentStream(
  uriString: String = "ws://producer:8032/instruments",
) {

  private val uri = Uri.of(uriString)

  private lateinit var ws: Websocket

  fun connect(onEvent: (InstrumentEvent) -> Unit) {
    ws = WebsocketClient.nonBlocking(uri) { println("Connected")}

    ws.onMessage {
      val event = jackson.readValue<InstrumentEvent>(it.body.stream)
      onEvent(event)
    }

    ws.onError {
      println(it)
    }
  }
}


class QuoteStream(
  uriString: String = "ws://producer:8032/quotes",
) {

  private val wsURI = Uri.of(uriString)

  private lateinit var ws: Websocket

  fun connect(onEvent: (QuoteEvent) -> Unit) {
    ws = WebsocketClient.nonBlocking(wsURI) { println("Connected") }

    ws.onMessage {
      val event = jackson.readValue<QuoteEvent>(it.body.stream)
      onEvent(event)
    }

    ws.onError {
      println(it)
    }
  }
}
