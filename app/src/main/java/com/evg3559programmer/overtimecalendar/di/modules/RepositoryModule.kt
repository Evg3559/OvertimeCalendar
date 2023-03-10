package com.evg3559programmer.overtimecalendar.di.modules

import com.evg3559programmer.overtimecalendar.composeUI.CacheMapper
import com.evg3559programmer.overtimecalendar.composeUI.MainRepository
import com.evg3559programmer.overtimecalendar.di.modules.ConfigureDB.ConfigurationDao
import com.evg3559programmer.overtimecalendar.di.modules.UserDB.UserDBDao
import com.evg3559programmer.overtimecalendar.di.modules.daysworkDB.DaysworkDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

      @Singleton
      @Provides
      fun provideMainRepository(daysworkDao: DaysworkDao,
                                userDBDao: UserDBDao,
                                configurationDao: ConfigurationDao,
                                cacheMapper: CacheMapper): MainRepository =
         MainRepository(daysworkDao, userDBDao, configurationDao, cacheMapper)


}