package co.edu.unab.sebastianlizcano.unabgo.FutureImplementation

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    //Cliente HTTP con timeout de 5 minutos (300 segundos)
    private val client = OkHttpClient.Builder()
        .connectTimeout(300, TimeUnit.SECONDS) // conexión
        .readTimeout(300, TimeUnit.SECONDS)    // lectura
        .writeTimeout(300, TimeUnit.SECONDS)   // escritura
        .build()

    //Retrofit configurado para la UNAB
    val api: UnabApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://unab.edu.co/") // dominio raíz con "/" al final
            .addConverterFactory(GsonConverterFactory.create())
            .client(client) // usa el cliente con timeout extendido
            .build()
            .create(UnabApiService::class.java)
    }
}