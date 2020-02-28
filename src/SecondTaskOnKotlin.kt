import java.lang.reflect.InvocationTargetException
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

fun main(args: Array<String>) {
    SecondTaskInOneFile.run(args)
}

class SecondTaskInOneFile {
    fun getLines(scanner: Scanner, N: Int): Array<String?> {
        val strings = arrayOfNulls<String>(N)
        for (i in 0 until N) {
            strings[i] = scanner.nextLine()
        }
        return strings
    }

    companion object {
        @Throws(ClassNotFoundException::class, NoSuchMethodException::class, InvocationTargetException::class, InstantiationException::class, IllegalAccessException::class)
        @JvmStatic
        fun run(args: Array<String>) {
            val scanner = Scanner(System.`in`)
            val settingsBuilder = StringBuilder()
            for (i in 0..5) {
                settingsBuilder.append(scanner.nextLine())
                settingsBuilder.append("\n")
            }
            settingsBuilder.setLength(settingsBuilder.length - 1)
            Settings.parseEntrySettings(settingsBuilder.toString())
            val pushAmount = scanner.nextLine().toInt()
            val parser = PushParser()
            val parsedPushes = ArrayList<Push>()
            for (i in 0 until pushAmount) {
                val pushParams = scanner.nextLine().toInt()
                val pushStrings = arrayOfNulls<String>(pushParams)
                for (j in 0 until pushParams) {
                    pushStrings[j] = scanner.nextLine()
                }
                parsedPushes.add(parser.parseInputStringIntoPush(pushStrings))
            }
            val pushesToShow = parsedPushes.stream().filter { x: Push -> !x.filter() }.peek { obj: Push -> obj.print() }.toArray()
            if (pushesToShow.isEmpty()) {
                println("-1")
            }
        }
    }
}

internal interface Filterable {
    fun filter(): Boolean
}

internal abstract class Push : Filterable {
    var text: String? = null
    var type: String? = null
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
    @Throws(ClassNotFoundException::class, NoSuchMethodException::class, IllegalAccessException::class, InvocationTargetException::class, InstantiationException::class)
    fun parseInputStringIntoPush(input: Array<String?>): Push {
        for (param in input) {
            if (param!!.startsWith("type")) {
                val keyAndValue = param.split(" ".toRegex()).toTypedArray()
                if (listOf(*parsableClasses).contains(keyAndValue[1])) {
                    val clazz = Class.forName(keyAndValue[1])
                    val constructor = clazz.getConstructor(Array<String>::class.java)
                    val obj = constructor.newInstance(*arrayOf<Any>(input))
                    return obj as Push
                }
            }
        }
        throw ClassNotFoundException("No TYPE param in push input")
    }

    companion object {
        val parsableClasses = arrayOf(
                LocationAgePush::class.java.name,
                LocationPush::class.java.name,
                AgeSpecificPush::class.java.name,
                TechPush::class.java.name,
                GenderAgePush::class.java.name,
                GenderPush::class.java.name
        )
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
        val settings = input.split("\n".toRegex()).toTypedArray()
        for (setting in settings) {
            val keyAndValue = setting.split(" ".toRegex()).toTypedArray()
            when (keyAndValue[0]) {
                "time" -> time = keyAndValue[1].toLong()
                "gender" -> gender = keyAndValue[1][0]
                "age" -> age = keyAndValue[1].toInt()
                "os_version" -> os_version = keyAndValue[1].toInt()
                "x_coord" -> x_coord = keyAndValue[1].toFloat()
                "y_coord" -> y_coord = keyAndValue[1].toFloat()
                else -> throw MissingFormatArgumentException("Нерпавильный формат введенных данных")
            }
        }
    }
}

internal class AgeSpecificPush(input: Array<String>) : Push() {
    var age = 0
    var expiry_date: Long = 0
    override fun filter(): Boolean {
        return PushFilters.filterByAge(age) || PushFilters.filterByExpiryDate(expiry_date)
    }

    init {
        for (param in input) {
            val kv = param.split(" ".toRegex()).toTypedArray()
            when (kv[0]) {
                "expiry_date" -> expiry_date = kv[1].toLong()
                "age" -> age = kv[1].toInt()
                "text" -> text = kv[1]
                "type" -> type = kv[1]
            }
        }
    }
}

internal class GenderAgePush(input: Array<String>) : Push() {
    var age = 0
    var gender = 0.toChar()
    override fun filter(): Boolean {
        return PushFilters.filterByGender(gender) || PushFilters.filterByAge(age)
    }

    init {
        for (param in input) {
            val kv = param.split(" ".toRegex()).toTypedArray()
            when (kv[0]) {
                "gender" -> gender = kv[1][0]
                "age" -> age = kv[1].toInt()
                "text" -> text = kv[1]
                "type" -> type = kv[1]
            }
        }
    }
}

internal class GenderPush(input: Array<String>) : Push() {
    var gender = 0.toChar()
    override fun filter(): Boolean {
        return PushFilters.filterByGender(gender)
    }

    init {
        for (param in input) {
            val kv = param.split(" ".toRegex()).toTypedArray()
            when (kv[0]) {
                "gender" -> gender = kv[1][0]
                "text" -> text = kv[1]
                "type" -> type = kv[1]
            }
        }
    }
}

internal class LocationAgePush(input: Array<String>) : Push() {
    var x_coord = 0f
    var y_coord = 0f
    var radius = 0
    var age = 0
    override fun filter(): Boolean {
        return PushFilters.filterByLocation(x_coord, y_coord, radius) || PushFilters.filterByAge(age)
    }

    init {
        for (param in input) {
            val kv = param.split(" ".toRegex()).toTypedArray()
            when (kv[0]) {
                "x_coord" -> x_coord = kv[1].toFloat()
                "y_coord" -> y_coord = kv[1].toFloat()
                "radius" -> radius = kv[1].toInt()
                "age" -> age = kv[1].toInt()
                "text" -> text = kv[1]
                "type" -> type = kv[1]
            }
        }
    }
}

internal class LocationPush(input: Array<String>) : Push() {
    var x_coord = 0f
    var y_coord = 0f
    var radius = 0
    var expiry_date: Long = 0
    override fun filter(): Boolean {
        return PushFilters.filterByLocation(x_coord, y_coord, radius) || PushFilters.filterByExpiryDate(expiry_date)
    }

    init {
        for (param in input) {
            val kv = param.split(" ".toRegex()).toTypedArray()
            when (kv[0]) {
                "x_coord" -> x_coord = kv[1].toFloat()
                "y_coord" -> y_coord = kv[1].toFloat()
                "radius" -> radius = kv[1].toInt()
                "expiry_date" -> expiry_date = kv[1].toLong()
                "text" -> text = kv[1]
                "type" -> type = kv[1]
            }
        }
    }
}

internal class TechPush(input: Array<String>) : Push() {
    var os_version = 0
    override fun filter(): Boolean {
        return PushFilters.filterByOSVersion(os_version)
    }

    init {
        for (param in input) {
            val kv = param.split(" ".toRegex()).toTypedArray()
            when (kv[0]) {
                "os_version" -> os_version = kv[1].toInt()
                "text" -> text = kv[1]
                "type" -> type = kv[1]
            }
        }
    }
}