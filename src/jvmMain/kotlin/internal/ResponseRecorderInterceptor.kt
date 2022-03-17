package internal

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody

class ResponseRecorderInterceptor(): Interceptor {
    var lastResponse: String = ""
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val responseBodyCopy: ResponseBody = response.peekBody(Long.MAX_VALUE)
        lastResponse = responseBodyCopy.string()
        return response
    }
}