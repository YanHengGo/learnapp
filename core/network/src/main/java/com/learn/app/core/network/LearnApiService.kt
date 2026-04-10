package com.learn.app.core.network

import com.learn.app.core.network.request.CreateChildRequest
import com.learn.app.core.network.request.CreateTaskRequest
import com.learn.app.core.network.request.DailyItemRequest
import com.learn.app.core.network.request.LoginRequest
import com.learn.app.core.network.request.ReorderRequest
import com.learn.app.core.network.request.SignupRequest
import com.learn.app.core.network.request.UpdateChildRequest
import com.learn.app.core.network.request.UpdateDailyRequest
import com.learn.app.core.network.request.UpdateTaskRequest
import com.learn.app.core.network.response.CalendarSummaryDto
import com.learn.app.core.network.response.ChildDto
import com.learn.app.core.network.response.DailyLogDto
import com.learn.app.core.network.response.DailyViewDto
import com.learn.app.core.network.response.MeDto
import com.learn.app.core.network.response.SignupDto
import com.learn.app.core.network.response.SummaryDto
import com.learn.app.core.network.response.TaskDto
import com.learn.app.core.network.response.TokenDto
import com.learn.app.core.network.response.UpdateDailyResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface LearnApiService {

    // ── Auth ──────────────────────────────────────────────────────────────

    @POST("api/v1/auth/signup")
    suspend fun signup(@Body body: SignupRequest): SignupDto

    @POST("api/v1/auth/login")
    suspend fun login(@Body body: LoginRequest): TokenDto

    @GET("api/v1/me")
    suspend fun getMe(): MeDto

    // ── Children ──────────────────────────────────────────────────────────

    @GET("api/v1/children")
    suspend fun getChildren(): List<ChildDto>

    @POST("api/v1/children")
    suspend fun createChild(@Body body: CreateChildRequest): ChildDto

    @PATCH("api/v1/children/{childId}")
    suspend fun patchChild(
        @Path("childId") childId: String,
        @Body body: Map<String, Any?>,
    ): ChildDto

    @PUT("api/v1/children/{id}")
    suspend fun updateChild(
        @Path("id") id: String,
        @Body body: UpdateChildRequest,
    ): ChildDto

    @DELETE("api/v1/children/{childId}")
    suspend fun deleteChild(@Path("childId") childId: String)

    // ── Tasks ─────────────────────────────────────────────────────────────

    @GET("api/v1/children/{childId}/tasks")
    suspend fun getTasks(
        @Path("childId") childId: String,
        @Query("archived") archived: Boolean = false,
    ): List<TaskDto>

    @POST("api/v1/children/{childId}/tasks")
    suspend fun createTask(
        @Path("childId") childId: String,
        @Body body: CreateTaskRequest,
    ): TaskDto

    // reorder は tasks/{taskId} より前に定義する必要がある
    @PUT("api/v1/children/{childId}/tasks/reorder")
    suspend fun reorderTasks(
        @Path("childId") childId: String,
        @Body body: ReorderRequest,
    )

    @PUT("api/v1/children/{childId}/tasks/{taskId}")
    suspend fun updateTask(
        @Path("childId") childId: String,
        @Path("taskId") taskId: String,
        @Body body: UpdateTaskRequest,
    ): TaskDto

    @PATCH("api/v1/tasks/{taskId}")
    suspend fun patchTask(
        @Path("taskId") taskId: String,
        @Body body: Map<String, Any?>,
    ): TaskDto

    // ── Daily ─────────────────────────────────────────────────────────────

    @GET("api/v1/children/{childId}/daily-view")
    suspend fun getDailyView(
        @Path("childId") childId: String,
        @Query("date") date: String,
    ): DailyViewDto

    @GET("api/v1/children/{childId}/daily")
    suspend fun getDailyLog(
        @Path("childId") childId: String,
        @Query("date") date: String,
    ): DailyLogDto

    @PUT("api/v1/children/{childId}/daily")
    suspend fun updateDailyLog(
        @Path("childId") childId: String,
        @Query("date") date: String,
        @Body body: UpdateDailyRequest,
    ): UpdateDailyResponseDto

    // ── Summary ───────────────────────────────────────────────────────────

    @GET("api/v1/children/{childId}/calendar-summary")
    suspend fun getCalendarSummary(
        @Path("childId") childId: String,
        @Query("from") from: String,
        @Query("to") to: String,
    ): CalendarSummaryDto

    @GET("api/v1/children/{childId}/summary")
    suspend fun getSummary(
        @Path("childId") childId: String,
        @Query("from") from: String,
        @Query("to") to: String,
    ): SummaryDto
}
