package secondtask

import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

fun main(args: Array<String>) {
    val settingsBuilder = StringBuilder()
    for (i in 0..5) {
        settingsBuilder.append(readLine())
        settingsBuilder.append("\n")
    }
    settingsBuilder.setLength(settingsBuilder.length - 1)
    Settings.parseEntrySettings(settingsBuilder.toString())
    val pushAmount = readLine()!!.toInt()
    val parser = PushParser()
    val parsedPushes = mutableListOf<Push>()
    for (i in 0 until pushAmount) {
        val pushParams = readLine()!!.toInt()
        val pushStrings = Array<String>(pushParams) {""}
        for (j in 0 until pushParams) {
            pushStrings[j] = readLine()!!
        }
        parsedPushes.add(parser.parseInputStringIntoPush(pushStrings))
    }
    val pushesToShow = parsedPushes.stream().filter { x: Push -> !x.filter() }.peek { obj: Push -> obj.print() }.toArray()
    if (pushesToShow.isEmpty()) {
        println("-1")
    }
}

internal interface Filterable {
    fun filter(): Boolean
}

internal abstract class Push() : Filterable {
    abstract val text: String
    abstract val type: String
    fun print() {
        println(text)
    }
}

internal object PushFilters {
    fun filterByAge(age: Int): Boolean {
        return age > Settings.age!!
    }

    fun filterByLocation(x_coord: Float, y_coord: Float, radius: Int): Boolean {
        val distance = sqrt((Settings.x_coord - x_coord.toDouble()).pow(2.0) + (Settings.y_coord - y_coord.toDouble()).pow(2.0))
        return distance > radius
    }

    fun filterByOSVersion(os_version: Int): Boolean {
        return Settings.os_version!! > os_version
    }

    fun filterByGender(gender: Char): Boolean {
        return Settings.gender != gender
    }

    fun filterByExpiryDate(expiry_date: Long): Boolean {
        return Settings.time!! > expiry_date
    }
}

internal class PushParser {
    fun parseInputStringIntoPush(input: Array<String>): Push {
        for (param in input) {
            if (param.startsWith("type")) {
                val keyAndValue = param.split(" ")
                return when (keyAndValue[1]) {
                    "LocationPush" -> parseLocationPush(input)
                    "AgeSpecificPush" -> parseAgeSpecificPush(input)
                    "TechPush" -> parseTechPush(input)
                    "LocationAgePush" -> parseLocationAgePush(input)
                    "GenderAgePush" -> parseGenderAgePush(input)
                    "GenderPush" -> parseGenderPush(input)
                    else -> throw IllegalArgumentException("This type is not defined: ${keyAndValue[1]}")
                }
            }
        }
        throw ClassNotFoundException("No TYPE param in push input")
    }

    private fun parseLocationPush(input: Array<String>): Push {
        val params = mutableMapOf<String, Any>()
        for (param in input) {
            val keyAndValue = param.split(" ")
            when (keyAndValue[0]) {
                "text" -> params["text"] = keyAndValue[1]
                "type" -> params["type"] = keyAndValue[1]
                "x_coord" -> params["x_coord"] = keyAndValue[1].toFloat()
                "y_coord" -> params["y_coord"] = keyAndValue[1].toFloat()
                "radius" -> params["radius"] = keyAndValue[1].toInt()
                "expiry_date" -> params["expiry_date"] = keyAndValue[1].toLong()
                else -> throw IllegalArgumentException("This Push parameter is not defined by system")
            }
        }

        return LocationPush(params["type"] as String, params["text"] as String,
                params["x_coord"] as Float, params["y_coord"] as Float, params["radius"] as Int,
                params["expiry_date"] as Long)
    }

    private fun parseAgeSpecificPush(input: Array<String>): Push {
        val params = mutableMapOf<String, Any>()
        for (param in input) {
            val keyAndValue = param.split(" ")
            when (keyAndValue[0]) {
                "text" -> params["text"] = keyAndValue[1]
                "type" -> params["type"] = keyAndValue[1]
                "age" -> params["age"] = keyAndValue[1].toInt()
                "expiry_date" -> params["expiry_date"] = keyAndValue[1].toLong()
                else -> throw IllegalArgumentException("This Push parameter is not defined by system")
            }
        }

        return AgeSpecificPush(params["type"] as String, params["text"] as String,
                params["age"] as Int, params["expiry_date"] as Long)
    }

    private fun parseTechPush(input: Array<String>): Push {
        val params = mutableMapOf<String, Any>()
        for (param in input) {
            val keyAndValue = param.split(" ")
            when (keyAndValue[0]) {
                "text" -> params["text"] = keyAndValue[1]
                "type" -> params["type"] = keyAndValue[1]
                "os_version" -> params["os_version"] = keyAndValue[1].toInt()
                else -> throw IllegalArgumentException("This Push parameter is not defined by system")
            }
        }

        return TechPush(params["type"] as String, params["text"] as String, params["os_version"] as Int)
    }

    private fun parseLocationAgePush(input: Array<String>): Push {
        val params = mutableMapOf<String, Any>()
        for (param in input) {
            val keyAndValue = param.split(" ")
            when (keyAndValue[0]) {
                "text" -> params["text"] = keyAndValue[1]
                "type" -> params["type"] = keyAndValue[1]
                "x_coord" -> params["x_coord"] = keyAndValue[1].toFloat()
                "y_coord" -> params["y_coord"] = keyAndValue[1].toFloat()
                "age" -> params["age"] = keyAndValue[1].toInt()
                "radius" -> params["radius"] = keyAndValue[1].toInt()
                else -> throw IllegalArgumentException("This Push parameter is not defined by system")
            }
        }

        return LocationAgePush(params["type"] as String, params["text"] as String,
                params["x_coord"] as Float, params["y_coord"] as Float,
                params["radius"] as Int, params["age"] as Int)
    }

    private fun parseGenderAgePush(input: Array<String>): Push {
        val params = mutableMapOf<String, Any>()
        for (param in input) {
            val keyAndValue = param.split(" ")
            when (keyAndValue[0]) {
                "text" -> params["text"] = keyAndValue[1]
                "type" -> params["type"] = keyAndValue[1]
                "gender" -> params["gender"] = keyAndValue[1][0]
                "age" -> params["age"] = keyAndValue[1].toInt()
                else -> throw IllegalArgumentException("This Push parameter is not defined by system")
            }
        }

        return GenderAgePush(params["type"] as String, params["text"] as String,
                params["gender"] as Char, params["age"] as Int)
    }

    private fun parseGenderPush(input: Array<String>): Push {
        val params = mutableMapOf<String, Any>()
        for (param in input) {
            val keyAndValue = param.split(" ")
            when (keyAndValue[0]) {
                "text" -> params["text"] = keyAndValue[1]
                "type" -> params["type"] = keyAndValue[1]
                "gender" -> params["gender"] = keyAndValue[1][0]
                else -> throw IllegalArgumentException("This Push parameter is not defined by system")
            }
        }

        return GenderPush(params["type"] as String, params["text"] as String,
                params["gender"] as Char)

    }
}

internal object Settings {
    var time: Long? = null
    var age: Int? = null
    var gender = 0.toChar()
    var os_version: Int? = null
    var x_coord = 0f
    var y_coord = 0f
    fun parseEntrySettings(input: String) {
        val settings = input.split("\n")
        for (setting in settings) {
            val keyAndValue = setting.split(" ")
            when (keyAndValue[0]) {
                "time" -> time = keyAndValue[1].toLong()
                "gender" -> gender = keyAndValue[1][0]
                "age" -> age = keyAndValue[1].toInt()
                "os_version" -> os_version = keyAndValue[1].toInt()
                "x_coord" -> x_coord = keyAndValue[1].toFloat()
                "y_coord" -> y_coord = keyAndValue[1].toFloat()
                else -> throw MissingFormatArgumentException("Wrong input format in system parameters")
            }
        }
    }
}

internal class AgeSpecificPush(override val type: String,
                               override val text: String,
                               val age: Int,
                               val expiryDate: Long) : Push() {
    override fun filter(): Boolean {
        return PushFilters.filterByAge(age) || PushFilters.filterByExpiryDate(expiryDate)
    }
}

internal class GenderAgePush(override val type: String,
                             override val text: String,
                             val gender: Char,
                             val age: Int) : Push() {
    override fun filter(): Boolean {
        return PushFilters.filterByGender(gender) || PushFilters.filterByAge(age)
    }
}

internal class GenderPush(override val type: String,
                          override val text: String,
                          val gender: Char) : Push() {
    override fun filter(): Boolean {
        return PushFilters.filterByGender(gender)
    }
}

internal class LocationAgePush(override val type: String,
                               override val text: String,
                               val xCoord: Float,
                               val yCoord: Float,
                               val radius: Int,
                               val age: Int) : Push() {
    override fun filter(): Boolean {
        return PushFilters.filterByLocation(xCoord, yCoord, radius) || PushFilters.filterByAge(age)
    }
}

internal class LocationPush(override val type: String,
                            override val text: String,
                            val xCoord: Float,
                            val yCoord: Float,
                            val radius: Int,
                            val expiryDate: Long) : Push() {
    override fun filter(): Boolean {
        return PushFilters.filterByLocation(xCoord, yCoord, radius) || PushFilters.filterByExpiryDate(expiryDate)
    }
}

internal class TechPush(override val type: String,
                        override val text: String,
                        val osVersion: Int ) : Push() {
    override fun filter(): Boolean {
        return PushFilters.filterByOSVersion(osVersion)
    }
}