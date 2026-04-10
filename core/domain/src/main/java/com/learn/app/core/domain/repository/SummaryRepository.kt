package com.learn.app.core.domain.repository

import com.learn.app.core.model.CalendarSummary
import com.learn.app.core.model.Summary

interface SummaryRepository {
    suspend fun getCalendarSummary(childId: String, from: String, to: String): CalendarSummary
    suspend fun getSummary(childId: String, from: String, to: String): Summary
}
