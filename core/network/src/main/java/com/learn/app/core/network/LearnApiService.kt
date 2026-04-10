package com.learn.app.core.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface LearnApiService {

    // Auth
    @POST("api/v1/auth/login")
    suspend fun login(@Body body: Map<String, String>): TokenResponse

    @POST("api/v1/auth/signup")
    suspend fun signup(@Body body: Map<String, String>): TokenResponse

    @GET("api/v1/me")
    suspend fun getMe(): UserResponse

    // Children
    @GET("api/v1/children")
    suspend fun getChildren(): ChildrenResponse

    @POST("api/v1/children")
    suspend fun createChild(@Body body: Map<String, String?>): ChildResponse

    @PATCH("api/v1/children/{childId}")
    suspend fun updateChild(
        @Path("childId") childId: String,
        @Body body: Map<String, String?>,
    ): ChildResponse

    @DELETE("api/v1/children/{childId}")
    suspend fun deleteChild(@Path("childId") childId: String)

    // Tasks
    @GET("api/v1/children/{childId}/tasks")
    suspend fun getTasks(
        @Path("childId") childId: String,
        @Query("archived") archived: Boolean = false,
    ): TasksResponse

    @POST("api/v1/children/{childId}/tasks")
    suspend fun createTask(
        @Path("childId") childId: String,
        @Body body: Map<String, Any?>,
    ): TaskResponse

    @PUT("api/v1/children/{childId}/tasks/{taskId}")
    suspend fun updateTask(
        @Path("childId") childId: String,
        @Path("taskId") taskId: String,
        @Body body: Map<String, Any?>,
    ): TaskResponse

    @PATCH("api/v1/tasks/{taskId}")
    suspend fun patchTask(
        @Path("taskId") taskId: String,
        @Body body: Map<String, Any?>,
    ): TaskResponse

    // Study Logs
    @GET("api/v1/children/{childId}/daily")
    suspend fun getDailyLogs(
        @Path("childId") childId: String,
        @Query("date") date: String,
    ): StudyLogsResponse

    @PUT("api/v1/children/{childId}/daily")
    suspend fun updateDailyLogs(
        @Path("childId") childId: String,
        @Query("date") date: String,
        @Body body: Map<String, Any>,
    ): StudyLogsResponse

    // Summary
    @GET("api/v1/children/{childId}/calendar-summary")
    suspend fun getCalendarSummary(
        @Path("childId") childId: String,
        @Query("from") from: String,
        @Query("to") to: String,
    ): CalendarSummaryResponse

    @GET("api/v1/children/{childId}/summary")
    suspend fun getSummary(
        @Path("childId") childId: String,
        @Query("from") from: String,
        @Query("to") to: String,
    ): SummaryResponse
}
