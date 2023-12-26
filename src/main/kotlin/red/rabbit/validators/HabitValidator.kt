package red.rabbit.validators

import io.ktor.server.plugins.requestvalidation.*
import red.rabbit.models.habit.HabitRequest
import red.rabbit.services.HabitService

fun RequestValidationConfig.habitValidation() {

    val habitService = HabitService()

    validate<HabitRequest> {
        if (it.name.isEmpty()) {
            ValidationResult.Invalid("Name shouldn't be empty")
        } else
            if (habitService.getHabitByName(it.name) != null) {
                ValidationResult.Invalid("Habit with name ${it.name} already exist")
            } else ValidationResult.Valid
    }
}