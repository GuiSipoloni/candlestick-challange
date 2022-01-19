package conf

import Instruments
import Quotes
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

class DBConfig {
    companion object{
        fun startUpDBConnection(){
            Database.connect("jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;", "org.h2.Driver")
            transaction {
                addLogger(StdOutSqlLogger)
                SchemaUtils.create(Instruments)
                SchemaUtils.create(Quotes)
            }
        }
    }
}