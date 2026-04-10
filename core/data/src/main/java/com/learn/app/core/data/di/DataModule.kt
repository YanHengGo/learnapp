package com.learn.app.core.data.di

import com.learn.app.core.data.repository.AuthRepositoryImpl
import com.learn.app.core.data.repository.ChildrenRepositoryImpl
import com.learn.app.core.data.repository.DailyRepositoryImpl
import com.learn.app.core.data.repository.SummaryRepositoryImpl
import com.learn.app.core.data.repository.TaskRepositoryImpl
import com.learn.app.core.domain.repository.AuthRepository
import com.learn.app.core.domain.repository.ChildrenRepository
import com.learn.app.core.domain.repository.DailyRepository
import com.learn.app.core.domain.repository.SummaryRepository
import com.learn.app.core.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindChildrenRepository(impl: ChildrenRepositoryImpl): ChildrenRepository

    @Binds @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Binds @Singleton
    abstract fun bindDailyRepository(impl: DailyRepositoryImpl): DailyRepository

    @Binds @Singleton
    abstract fun bindSummaryRepository(impl: SummaryRepositoryImpl): SummaryRepository
}
