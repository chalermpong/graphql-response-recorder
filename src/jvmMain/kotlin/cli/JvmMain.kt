package cli

import cli.type.PhotoSize
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.api.Query
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import internal.GraphqlResponseRecorder
import kotlinx.coroutines.runBlocking
import java.io.File

val queryMap = mapOf<String, Query<*>>(
    "responsePost1All" to ArticleQuery(
        "624c1f8191d05825de905b11",
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
        extension = args[3]
    }


    val template = File(templatePathString).readText(Charsets.UTF_8)

    runBlocking {
        val responseRecorder = GraphqlResponseRecorder(apolloServerURL)
        val responses = queryMap.map { (name, query) ->
            val response = responseRecorder.fetch(query)
            val filePath = outputPathString + "/${name}.result.${extension}"
            val outputDirectory = File(filePath)
            outputDirectory.printWriter().use {
                val result = response.raw
                val gson: Gson = GsonBuilder().setPrettyPrinting().create()
                val je: JsonElement? = JsonParser.parseString(result)
                val prettyJsonString: String = gson.toJson(je)
                val output = template
                    .replace("{variableName}", name)
                    .replace("{result}", result)
                val outputWithPrettyJson = "$output\n\n//${prettyJsonString.replace("\n", "\n//")}"
                it.write(outputWithPrettyJson)
                println("Recorded: file://${outputDirectory.canonicalPath}")
            }
        }
        println("===== Finished!!! =====")
        println("args [templatePath] [outputPath] [apolloServer] [outputExtension]")
        println("Example: --args=\"../TestGraphQLGenerator/template/gql-generator-template ../TestGraphQLGenerator/output https://apollo-fullstack-tutorial.herokuapp.com/graphql swift\"")
    }
}
