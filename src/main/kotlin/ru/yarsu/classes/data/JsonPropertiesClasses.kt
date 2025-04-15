package ru.yarsu.classes.data

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.util.UUID

data class TaskShortData(
    @JsonProperty("Id") val id: UUID,
    @JsonProperty("Title") val title: String,
    @JsonProperty("IsClosed") val isClosed: Boolean,
)

data class IdData(
    @JsonProperty("Id") val id: UUID,
)

data class AuthorCategoryData(
    @JsonProperty("AuthorId") val authorId: UUID,
    @JsonProperty("CategoryOwnerId") val categoryId: UUID,
)

data class TaskFullData(
    @JsonProperty("Id") val id: UUID,
    @JsonProperty("Title") val title: String,
    @JsonProperty("RegistrationDateTime") val registrationDateTime: LocalDateTime,
    @JsonProperty("StartDateTime") val startDateTime: LocalDateTime,
    @JsonProperty("EndDateTime") val endDateTime: LocalDateTime?,
    @JsonProperty("Importance") val importance: String,
    @JsonProperty("Urgency") val urgency: Boolean,
    @JsonProperty("Percentage") val percentage: Int,
    @JsonProperty("Description") val description: String,
    @JsonProperty("Author") val author: UUID,
    @JsonProperty("AuthorEmail") val authorEmail: String,
    @JsonProperty("Category") val category: UUID?,
    @JsonProperty("CategoryDescription") val categoryDescription: String,
)

data class TaskImportantUrgentData(
    @JsonProperty("Id") val id: UUID,
    @JsonProperty("Title") val title: String,
    @JsonProperty("Importance") val importance: String,
    @JsonProperty("Urgency") val urgency: Boolean,
    @JsonProperty("Percentage") val percentage: Int,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TaskStatisticData(
    @JsonProperty("statisticByRegistrationDateTime") val registration: StatisticData?,
    @JsonProperty("statisticByStartDateTime") val start: StatisticData?,
    @JsonProperty("statisticByEndDateTime") val end: StatisticData?,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class StatisticData(
    @JsonProperty("Понедельник") val monday: Int?,
    @JsonProperty("Вторник") val tuesday: Int?,
    @JsonProperty("Среда") val wednesday: Int?,
    @JsonProperty("Четверг") val thursday: Int?,
    @JsonProperty("Пятница") val friday: Int?,
    @JsonProperty("Суббота") val saturday: Int?,
    @JsonProperty("Воскресенье") val sunday: Int?,
    @JsonProperty("Не заполнено") val none: Int?,
)

data class CategoryData(
    @JsonProperty("Id") val id: UUID,
    @JsonProperty("Description") val description: String,
    @JsonProperty("Color") val color: Color,
    @JsonProperty("Owner") val owner: UUID?,
    @JsonProperty("OwnerName") val ownerName: String?,
)

data class CategoryTaskData(
    @JsonProperty("TaskId") val taskId: UUID,
    @JsonProperty("TaskTitle") val taskTitle: String,
    @JsonProperty("Author") val author: UUID,
    @JsonProperty("AuthorLogin") val authorLogin: String,
)

data class UserData(
    @JsonProperty("Id") val id: UUID,
    @JsonProperty("Login") val login: String,
    @JsonProperty("RegistrationDateTime") val registration: LocalDateTime,
    @JsonProperty("Email") val email: String,
)

data class TaskIdTitleData(
    @JsonProperty("Id") val id: UUID,
    @JsonProperty("Title") val title: String,
)

data class CategoryIdDescriptionData(
    @JsonProperty("Id") val id: UUID,
    @JsonProperty("Description") val description: String,
)

data class DeleteUserData(
    @JsonProperty("Tasks") val tasks: List<TaskIdTitleData>,
    @JsonProperty("Categories") val categories: List<CategoryIdDescriptionData>,
)
