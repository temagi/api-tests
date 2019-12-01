package helpers

import io.restassured.RestAssured
import io.restassured.parsing.Parser
import java.io.IOException
import java.util.Objects
import java.util.Properties

abstract class BaseTest {
    init {
        try {
            val properties = Properties()
            val resource = javaClass.classLoader.getResourceAsStream("config.properties")
            properties.load(Objects.requireNonNull(resource))
            RestAssured.baseURI = properties.getProperty("baseurl")
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        RestAssured.defaultParser = Parser.JSON
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }
}