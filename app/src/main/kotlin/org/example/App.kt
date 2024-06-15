package org.example

import java.io.File
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

val plantUmlJarPath = File("./plantuml.jar").absolutePath
val jsonFilePath = File("./getRuntimeManagementConfigResponse.json").absolutePath
val plantUmlFilePath = File("./sequenceDiagram.txt").absolutePath

data class RuntimeManagementConfig(
    val FunctionArn: String,
    val RuntimeVersionArn: String,
    val UpdateRuntimeOn: String
)

fun main() {
    // Verify that the PlantUML JAR file exists
    if (!File(plantUmlJarPath).exists()) {
        println("Error: PlantUML JAR file not found at $plantUmlJarPath")
        return
    }

    // Verify that the JSON file exists
    if (!File(jsonFilePath).exists()) {
        println("Error: JSON file not found at $jsonFilePath")
        return
    }

    // Read and parse the JSON file
    val getRuntimeManagementConfigResponse = File(jsonFilePath).readText()

    // Generate PlantUML text from the JSON data
    val plantUmlText = """
        @startjson
        ${getRuntimeManagementConfigResponse}
        @endjson
    """.trimIndent()

    // Write the PlantUML text to a file
    File(plantUmlFilePath).writeText(plantUmlText)

    // Generate SVG from the PlantUML text file
    val command = listOf("java", "-jar", plantUmlJarPath, "-tsvg", plantUmlFilePath)
    println("Running command: ${command.joinToString(" ")}")

    try {
        val processBuilder = ProcessBuilder(command)
        processBuilder.redirectErrorStream(true)
        val process = processBuilder.start()
        
        val output = process.inputStream.bufferedReader().use { it.readText() }
        val exitCode = process.waitFor()

        println("Exit code: $exitCode")
        if (exitCode == 0) {
            println("SVG file generated successfully.")
            println("Output: $output")
        } else {
            println("Error occurred during SVG generation. Exit code: $exitCode")
            println("Output: $output")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
