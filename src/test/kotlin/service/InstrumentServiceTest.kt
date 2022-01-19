package service

import conf.DBConfig
import model.CandlestickManagerImpl
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal
import java.time.LocalDateTime
import InstrumentEvent
import Instrument
import io.mockk.impl.annotations.SpyK
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.*

internal class InstrumentServiceTest{


    @BeforeEach
    fun createDB(){
        DBConfig.startUpDBConnection()
    }

    @AfterEach
    fun cleanDB(){
        transaction {
            TransactionManager.current().exec("truncate table instruments")
        }
    }

    @Test
    fun createOrDeleteTestAdd() {
        // given
        val instrumentEvent = InstrumentEvent(
            type = InstrumentEvent.Type.ADD,
            data = Instrument(isin = "test",
                              description = "this is a test" )
            )
        // when
        val spy = spyk<InstrumentService>()
        spy.createOrDelete(instrumentEvent)

        //then
        verify(exactly = 1) { spy.createInstrument(instrumentEvent.data)}
    }

    @Test
    fun createOrDeleteTestDelete() {
        // given
        val instrumentEvent = InstrumentEvent(
            type = InstrumentEvent.Type.DELETE,
            data = Instrument(isin = "test",
                description = "this is a test" )
        )
        // when
        val spy = spyk<InstrumentService>()
        spy.createOrDelete(instrumentEvent)

        //then
        verify(exactly = 1) { spy.deleteInstrument(instrumentEvent.data)}
    }

}