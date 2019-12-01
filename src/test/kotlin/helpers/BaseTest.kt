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
        // looks like CloudFlare block access from Russia to typicode.com today
        // RestAssured.proxy("178.18.62.195", 80);
        // TODO: explore, does we need to set additional log level here or not
        // looks like CloudFlare block access from Russia to typicode.com today
        // RestAssured.proxy("178.18.62.195", 80);
        // TODO: explore, does we need to set additional log level here or not
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }
}