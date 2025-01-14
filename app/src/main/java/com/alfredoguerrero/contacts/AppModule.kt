package com.alfredoguerrero.contacts

import android.app.Application
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.alfredoguerrero.contacts.framework.data.ContactsDS
import com.alfredoguerrero.contacts.framework.data.ContactsDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSqlDriver(app: Application): SqlDriver{
        return AndroidSqliteDriver(
            schema = ContactsDatabase.Schema,
            context = app,
            name = "contacts.db"
        )
    }

    @Provides
    @Singleton
    fun provideContactsDataSource(driver: SqlDriver): ContactsDataSource{
        return ContactsDS(ContactsDatabase(driver))
    }
}