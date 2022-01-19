package service

import Instrument
import InstrumentEvent
import Instruments
import Quotes
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class InstrumentService {
        fun createOrDelete(event: InstrumentEvent) {
            if (event.type == InstrumentEvent.Type.ADD) {
                createInstrument(event.data)
            }

            if (event.type == InstrumentEvent.Type.DELETE) {
                deleteInstrument(event.data)
            }
        }

        fun createInstrument(instrument: Instrument) {
            transaction {
                addLogger(StdOutSqlLogger)
                Instruments.insert { it[isin] = instrument.isin
                                     it[description] = instrument.description
                                    }
            }
        }

        fun deleteInstrument(instrument: Instrument) {
            transaction {
                addLogger(StdOutSqlLogger)
                Quotes.deleteWhere { Quotes.isin eq instrument.isin }
                Instruments.deleteWhere { Instruments.isin eq instrument.isin }
            }
        }
}