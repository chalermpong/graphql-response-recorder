package cli

import cli.type.PhotoSize
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.api.Query
import internal.GraphqlResponseRecorder
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.system.exitProcess

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
    if (args.size != 4) {
        println("should add args [templatePath] [outputPath] [apolloServer] [outputExtension]")
        println("Example: --args=\"../TestGraphQLGenerator/template/gql-generator-template ../TestGraphQLGenerator/output https://apollo-fullstack-tutorial.herokuapp.com/graphql swift\"")
        return
    }

    val templatePathString = args[0]
    val outputPathString = args[1]
    val apolloServerURL = args[2]
    val extension: String = args[3]
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
    }
}
