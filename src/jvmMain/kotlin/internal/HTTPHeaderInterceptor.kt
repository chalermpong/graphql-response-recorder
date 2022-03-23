package internal

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.Request

class HTTPHeaderInterceptor(): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest: Request = chain.request().newBuilder()
            .header("X-App", "Blockdit-100.0")
            .build()
        return chain.proceed(newRequest)
    }
}