package com.evg3559programmer.overtimecalendar.di.modules

import android.content.Context
import androidx.room.Room
import com.evg3559programmer.overtimecalendar.di.modules.ConfigureDB.ConfigurationDao
import com.evg3559programmer.overtimecalendar.di.modules.UserDB.UserDBDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

   @Singleton
   @Provides
   fun provideRoomDatabase (
      @ApplicationContext context: Context
   ): DaysworkDB = Room.databaseBuilder(context, DaysworkDB::class.java, DaysworkDB.DB_NAME)
      .fallbackToDestructiveMigration()
      .build()

   @Singleton
   @Provides
   fun provideDaysworkDao(db: DaysworkDB) = db.daysworkDao()

   @Singleton
   @Provides
   fun provideUserDB (db: DaysworkDB): UserDBDao = db.userDBDao()

   @Singleton
   @Provides
   fun provideAppConfiguration(db: DaysworkDB): ConfigurationDao = db.configurationDao()

}