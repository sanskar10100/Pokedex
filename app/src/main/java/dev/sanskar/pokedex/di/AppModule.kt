package dev.sanskar.pokedex.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sanskar.pokedex.BuildConfig
import dev.sanskar.pokedex.db.PokedexDB
import dev.sanskar.pokedex.db.PokedexDao
import dev.sanskar.pokedex.network.Api
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideOkHttpLoggingInterceptor() = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        })
        .build()

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(provideOkHttpLoggingInterceptor())
            .build()
    }

    @Singleton
    @Provides
    fun provideApi(retrofit: Retrofit): Api = retrofit.create(Api::class.java)

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) =
        Room.databaseBuilder(
            appContext,
            PokedexDB::class.java,
            "pokedex_db"
        ).build()

    @Singleton
    @Provides
    fun provideDao(db: PokedexDB) = db.pokedexDao()
}