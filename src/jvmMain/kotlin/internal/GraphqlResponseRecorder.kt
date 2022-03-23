package internal

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Query
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient

class GraphqlResponseRecorder(
    serverUrl: String,
    okHttpInterceptors: List<Interceptor> = emptyList()
) {
    data class RecordSpec(val name: String, val query: Query<*>)

    private val responseInterceptor = ResponseRecorderInterceptor()
    private val httpHeaderInterceptor = HTTPHeaderInterceptor()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(httpHeaderInterceptor)
        .addInterceptor(responseInterceptor)
        .apply {
            okHttpInterceptors.forEach { this.addInterceptor(it) }
        }
        .build()


    private val apolloClient = ApolloClient.Builder()
        .serverUrl(serverUrl)
        .okHttpClient(okHttpClient)
        .build()

    suspend fun fetchAll(queries: List<Query<*>>): List<String> {
        return queries.map {
            fetch(it).raw
        }
    }

    class Response<D: Query.Data>(val model: ApolloResponse<D>, val raw: String)

    suspend fun<D: Query.Data> fetch(query: Query<D>): Response<D> {
        val response = apolloClient.query(query).execute()
        return Response(response, responseInterceptor.lastResponse)
    }
}