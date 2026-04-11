package com.learn.app.feature.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.learn.app.core.model.CalendarDay
import com.learn.app.core.model.CalendarStatus
import com.learn.app.core.model.Summary
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val DAY_OF_WEEK_LABELS = listOf("日", "月", "火", "水", "木", "金", "土")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    childId: String,
    onBack: () -> Unit,
    onDaySelected: (date: String) -> Unit,
    viewModel: SummaryViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onErrorDismiss()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxHeight(),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Text("集計")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                modifier = Modifier.height(48.dp),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp),
            ) {
                // 月ナビゲーション
                item {
                    MonthNavigationBar(
                        yearMonth = uiState.yearMonth,
                        onPrevious = viewModel::onPreviousMonth,
                        onNext = viewModel::onNextMonth,
                    )
                }

                // カレンダー
                item {
                    CalendarGrid(
                        yearMonth = uiState.yearMonth,
                        dayMap = uiState.calendarSummary?.days?.associateBy { it.date } ?: emptyMap(),
                        onDayClick = onDaySelected,
                        modifier = Modifier.padding(horizontal = 12.dp),
                    )
                }

                // 凡例
                item {
                    LegendRow(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }

                // 統計
                uiState.summary?.let { summary ->
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        SummaryStats(summary = summary, modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthNavigationBar(
    yearMonth: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Filled.ChevronLeft, contentDescription = "前の月")
        }
        Text(
            text = "${yearMonth.year}年${yearMonth.monthValue}月",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        IconButton(onClick = onNext) {
            Icon(Icons.Filled.ChevronRight, contentDescription = "次の月")
        }
    }
}

@Composable
private fun CalendarGrid(
    yearMonth: YearMonth,
    dayMap: Map<String, CalendarDay>,
    onDayClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        // 曜日ヘッダー
        Row(modifier = Modifier.fillMaxWidth()) {
            DAY_OF_WEEK_LABELS.forEachIndexed { index, label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = when (index) {
                        0 -> Color(0xFFE53935) // 日
                        6 -> Color(0xFF1E88E5) // 土
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 日付グリッド
        val firstDay = yearMonth.atDay(1)
        // DayOfWeek: MONDAY=1...SUNDAY=7, 日曜始まりに変換
        val startOffset = (firstDay.dayOfWeek.value % 7) // 日=0, 月=1,..., 土=6
        val daysInMonth = yearMonth.lengthOfMonth()
        val totalCells = startOffset + daysInMonth
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayNum = cellIndex - startOffset + 1
                    if (dayNum < 1 || dayNum > daysInMonth) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val dateStr = yearMonth.atDay(dayNum).toString()
                        val calDay = dayMap[dateStr]
                        DayCell(
                            dayNum = dayNum,
                            calendarDay = calDay,
                            dayOfWeekIndex = col,
                            onClick = { onDayClick(dateStr) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    dayNum: Int,
    calendarDay: CalendarDay?,
    dayOfWeekIndex: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor = when (calendarDay?.status) {
        CalendarStatus.GREEN -> Color(0xFFC8E6C9)
        CalendarStatus.YELLOW -> Color(0xFFFFF9C4)
        CalendarStatus.RED -> Color(0xFFFFCDD2)
        else -> Color.Transparent
    }
    val textColor = when (dayOfWeekIndex) {
        0 -> Color(0xFFE53935)
        6 -> Color(0xFF1E88E5)
        else -> MaterialTheme.colorScheme.onSurface
    }
    val today = LocalDate.now().dayOfMonth
    val isToday = calendarDay?.date == LocalDate.now().toString()

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (isToday) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
            )
        }
        Text(
            text = dayNum.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

@Composable
private fun LegendRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LegendItem(color = Color(0xFFC8E6C9), label = "全完了")
        LegendItem(color = Color(0xFFFFF9C4), label = "一部完了")
        LegendItem(color = Color(0xFFFFCDD2), label = "未完了")
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color),
        )
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun SummaryStats(summary: Summary, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // 合計
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("期間合計", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatMinutes(summary.totalMinutes),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        // 教科別
        if (summary.bySubject.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("教科別", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    summary.bySubject.sortedByDescending { it.minutes }.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(item.subject, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                formatMinutes(item.minutes),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
        }

        // タスク別
        if (summary.byTask.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("タスク別", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    summary.byTask.sortedByDescending { it.minutes }.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    item.subject,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Text(
                                formatMinutes(item.minutes),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

private fun formatMinutes(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return if (h > 0) "${h}時間${m}分" else "${m}分"
}
