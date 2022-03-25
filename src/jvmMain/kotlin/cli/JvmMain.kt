package cli

import cli.type.PhotoSize
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.api.Query
import internal.GraphqlResponseRecorder
import kotlinx.coroutines.runBlocking
import java.io.File

val queryMap = mapOf<String, Query<*>>(
    "ArticleResultData" to ArticleQuery(
        "623437cf8cb557398dc8a503",
        false,
        Optional.Absent,
        PhotoSize.s80x80,
        PhotoSize.s80x80,
        PhotoSize.s80x80,
        PhotoSize.s80x80,
        PhotoSize.s80x80,
        PhotoSize.s80x80,
        PhotoSize.s80x80),
)

fun main(args: Array<String>) {

    var templatePathString = "./template/gql-swift-generator-template"
    var outputPathString = "../bdit-ios/LongtunmanScreenshotTests/QueryResultGenerated/"
    var apolloServerURL = "https://graph.staging.blockdit.com/graphql"
    var extension: String = "swift"
    if (args.size == 4) {
        templatePathString = args[0]
        outputPathString = args[1]
        apolloServerURL = args[2]
        extension: String = args[3]

    }


    val template = File(templatePathString).readText(Charsets.UTF_8)

    runBlocking {
        val responseRecorder = GraphqlResponseRecorder(apolloServerURL)
        val responses = queryMap.map { (name, query) ->
            val response = responseRecorder.fetch(query)
            val outputDirectory = File(outputPathString + "/${name}.result.${extension}")
            outputDirectory.printWriter().use {
                val newResult = response.raw.replace("\"", "\\\"").trim()
                val output = template
                    .replace("{variableName}", name)
                    .replace("{result}", newResult)
                it.write(output)
            }
        }
        println("===== Finished!!! =====")
        println("args [templatePath] [outputPath] [apolloServer] [outputExtension]")
        println("Example: --args=\"../TestGraphQLGenerator/template/gql-generator-template ../TestGraphQLGenerator/output https://apollo-fullstack-tutorial.herokuapp.com/graphql swift\"")
    }
}
