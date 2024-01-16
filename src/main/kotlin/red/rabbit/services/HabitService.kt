package red.rabbit.services

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import red.rabbit.DatabaseFactory.dbQuery
import red.rabbit.models.habit.Habit
import red.rabbit.models.habit.Habits

class HabitService {

    private fun resultRowToHabit(row: ResultRow): Habit =
        Habit(
            id = row[Habits.id],
            name = row[Habits.name],
            isGood = row[Habits.isGood]
        )

    suspend fun allHabits(): List<Habit> = dbQuery {
        exposedLogger.info("Select all habits from DB")
        Habits.selectAll().map(::resultRowToHabit)
    }

    suspend fun habit(id: Int): Habit? = dbQuery {
        exposedLogger.info("Select habit with id $id from DB")
        Habits
            .select { Habits.id eq id }
            .map(::resultRowToHabit)
            .singleOrNull()
    }

    suspend fun addNewHabit(name: String, isGood: Boolean): Habit? = dbQuery {
        exposedLogger.info("Insert new habit with name $name to DB")
        val insertStatement = Habits.insert {
            it[Habits.name] = name
            it[Habits.isGood] = isGood
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToHabit)
    }

    suspend fun editHabit(id: Int, name: String, isGood: Boolean): Boolean = dbQuery {
        exposedLogger.info("Update habit with id $id and name $name to DB")
        Habits.update({ Habits.id eq id }) {
            it[Habits.name] = name
            it[Habits.isGood] = isGood
        } > 0
    }

    suspend fun deleteHabit(id: Int): Boolean = dbQuery {
        exposedLogger.info("Delete habit with id $id from DB")
        Habits.deleteWhere { Habits.id eq id } > 0
    }

    suspend fun getHabitByName(name: String): Habit? {
        exposedLogger.info("Select habit with name $name from DB")
        val habit = dbQuery {
            Habits
                .select { Habits.name eq name }
                .map(::resultRowToHabit)
                .singleOrNull()
        }
        exposedLogger.info("Acquired $habit from DB")
        return habit
    }
}