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
        Habits.selectAll().map(::resultRowToHabit)
    }

    suspend fun habit(id: Int): Habit? = dbQuery {
        Habits
            .select { Habits.id eq id }
            .map(::resultRowToHabit)
            .singleOrNull()
    }

    suspend fun addNewHabit(name: String, isGood: Boolean): Habit? = dbQuery {
        val insertStatement = Habits.insert {
            it[Habits.name] = name
            it[Habits.isGood] = isGood
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToHabit)
    }

    suspend fun editHabit(id: Int, name: String, isGood: Boolean): Boolean = dbQuery {
        Habits.update({ Habits.id eq id }) {
            it[Habits.name] = name
            it[Habits.isGood] = isGood
        } > 0
    }

    suspend fun deleteHabit(id: Int): Boolean = dbQuery {
        Habits.deleteWhere { Habits.id eq id } > 0
    }

    suspend fun getHabitByName(name: String): Habit? {
        val habit = dbQuery {
            Habits
                .select { Habits.name eq name }
                .map(::resultRowToHabit)
                .singleOrNull()
        }
        exposedLogger.info("$habit")
        return habit
    }
}