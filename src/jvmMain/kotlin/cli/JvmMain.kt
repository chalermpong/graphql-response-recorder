package cli

import com.apollographql.apollo3.api.Optional
import internal.GraphqlResponseRecorder
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    runBlocking {

        val responseRecorder = GraphqlResponseRecorder(
            serverUrl = "https://apollo-fullstack-tutorial.herokuapp.com/graphql"
        )
        //var cursor: String? = null
//        val responses = intArrayOf(1).map {
//            val query = LaunchesQuery(
//                pageSize = 3,
//                after = Optional.presentIfNotNull(cursor)
//            )
//            val response = responseRecorder.fetch(query)
//            cursor = response.model.data?.launches?.cursor
//            response.raw
//        }
        test()
        //println(responses.joinToString("\n\n"))
    }
}

suspend fun test() {
    println("Testtttt")
}
