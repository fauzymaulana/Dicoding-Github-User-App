package com.papero.gituser.utilities.network

import com.papero.gituser.BuildConfig
import com.papero.gituser.data.remote.service.GithubService
import com.papero.gituser.utilities.libraries.Header
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class RequestClient {

    private fun getHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val tlsEcdsa = CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256
        val tlsRsa = CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256
        val tlsDhe = CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
        val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_2)
            .cipherSuites(tlsEcdsa, tlsRsa, tlsDhe)
            .build()

        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                val reqBuilder = request.newBuilder()
                reqBuilder.header("Accept", Header.HEADER_APP_JSON)
                reqBuilder.header("Content-Type", Header.HEADER_APP_JSON)
                reqBuilder.header("Authorization", "Bearer ${BuildConfig.GITHUB_API}")

                val response = chain.proceed(reqBuilder.build())
                return@addInterceptor response.newBuilder().build()
            }
            .addInterceptor(interceptor)
            .connectionSpecs(listOf(spec, ConnectionSpec.CLEARTEXT))
            .connectTimeout(30000, TimeUnit.MILLISECONDS)
            .readTimeout(30000, TimeUnit.MILLISECONDS)
            .writeTimeout(30000, TimeUnit.MILLISECONDS)
            .build()
    }

    private fun getClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(getHttpClient())
            .build()
    }


    fun user(): GithubService {
        return getClient().create(GithubService::class.java)
    }
}