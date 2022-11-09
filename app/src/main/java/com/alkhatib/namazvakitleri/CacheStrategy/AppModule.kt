package com.alkhatib.namazvakitleri.CacheStrategy

import android.app.Application
import androidx.room.Room
import com.alkhatib.namazvakitleri.RetrofitApi.RetrofitAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://ezanvakti.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideCarListAPI(retrofit: Retrofit): RetrofitAPI =
        retrofit.create(RetrofitAPI::class.java)

    @Provides
    @Singleton
    fun provideDatabase(app: Application): PrayersDatabase =
        Room.databaseBuilder(app, PrayersDatabase::class.java, "prayers_database")
            .build()
}