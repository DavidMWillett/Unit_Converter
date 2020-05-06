package converter

import java.util.Scanner

/*
 * Program entry point. Loops until exit, calling convert function of Converter object with input and printing output
 * of conversion each time.
 */

fun main() {
    var finished = false
    do {
        print("Enter what you want to convert (or exit): ")
        val input = readLine()!!
        if (input == "exit")
            finished = true
        else
            println(Converter.convert(input))
    } while (!finished)
}

/*
 * Converter object. Provides convert function which will return the output corresponding to the requested conversion
 * or "Parse error" if the input cannot be parsed.
 */

object Converter {
    var sourceNumber = 0.0
    var sourceUnit = Unit.NULL
    var targetNumber = 0.0
    var targetUnit = Unit.NULL

    /*
     * Tries to parse input string; returns output string if parsable, otherwise error message.
     */
    fun convert(input: String): String {
        return if (parse(input)) {
            output()
        } else {
            "Parse error"
        }
    }

    /*
     * Parses each word of input string, assigning member variables accordingly. Returns true if parsing is successful,
     * otherwise false.
     */
    fun parse(input: String): Boolean {
        val scanner = Scanner(input)

        if (!scanner.hasNextDouble()) return false
        sourceNumber = scanner.nextDouble()
        if (!UnitParser.parse(scanner)) return false
        sourceUnit = UnitParser.unit
        if (!scanner.hasNext()) return false
        scanner.next() // Skips past the random filler word.
        if (!UnitParser.parse(scanner)) return false
        targetUnit = UnitParser.unit
        if (scanner.hasNext()) return false
        return true
    }

    /*
     * Builds output on basis of member variables, calculating target number if possible.
     */
    fun output(): String {

        if (sourceUnit == Unit.NULL || targetUnit == Unit.NULL ||
                sourceUnit.dimension != targetUnit.dimension) {
            return "Conversion from ${sourceUnit.plural} to ${targetUnit.plural} is impossible"
        }
        if (sourceUnit.dimension == Dimension.LENGTH && sourceNumber < 0.0) {
            return "Length shouldn't be negative"
        }
        if (sourceUnit.dimension == Dimension.MASS && sourceNumber < 0.0) {
            return "Weight shouldn't be negative"
        }
        targetNumber = (sourceNumber - sourceUnit.offset) *
                sourceUnit.multiplier / targetUnit.multiplier + targetUnit.offset
        return "$sourceNumber ${if (sourceNumber == 1.0) sourceUnit.singular else sourceUnit.plural} is " +
                "$targetNumber ${if (targetNumber == 1.0) targetUnit.singular else targetUnit.plural}"
    }

    /*
     * Provides parse function to parse a string representing a unit. If successful, stores as unit property and
     * returns true, otherwise sets unit to Unit.NULL and returns false.
     */
    object UnitParser {
        var unit = Unit.NULL

        fun parse(scanner: Scanner): Boolean {
            if (!scanner.hasNext()) return false
            var word = scanner.next().toLowerCase()
            if (word == "degree" || word == "degrees") {
                if (!scanner.hasNext()) return false
                word = scanner.next().toLowerCase()
                unit = when (word) {
                    "celsius" -> Unit.CELSIUS
                    "fahrenheit" -> Unit.FAHRENHEIT
                    else -> Unit.NULL
                }
            } else {
                unit = when (word) {
                    "meter", "meters", "m" -> Unit.METER
                    "kilometer", "kilometers", "km" -> Unit.KILOMETER
                    "centimeter", "centimeters", "cm" -> Unit.CENTIMETER
                    "millimeter", "millimeters", "mm" -> Unit.MILLIMETER
                    "mile", "miles", "mi" -> Unit.MILE
                    "yard", "yards", "yd" -> Unit.YARD
                    "foot", "feet", "ft" -> Unit.FOOT
                    "inch", "inches", "in" -> Unit.INCH

                    "gram", "grams", "g" -> Unit.GRAM
                    "kilogram", "kilograms", "kg" -> Unit.KILOGRAM
                    "milligram", "milligrams", "mg" -> Unit.MILLIGRAM
                    "pound", "pounds", "lb" -> Unit.POUND
                    "ounce", "ounces", "oz" -> Unit.OUNCE

                    "celsius", "dc", "c" -> Unit.CELSIUS
                    "kelvin", "kelvins", "k" -> Unit.KELVIN
                    "fahrenheit", "df", "f" -> Unit.FAHRENHEIT

                    else -> Unit.NULL
                }
            }
            return true
        }
    }

    enum class Unit(val dimension: Dimension, val singular: String, val plural: String,
                    val multiplier: Double, val offset: Double) {

        NULL(Dimension.NULL, "???", "???", 1.0, 0.0),

        METER(Dimension.LENGTH, "meter", "meters", 1.0, 0.0),
        KILOMETER(Dimension.LENGTH, "kilometer", "kilometers", 1000.0, 0.0),
        CENTIMETER(Dimension.LENGTH, "centimeter", "centimeters", 0.01, 0.0),
        MILLIMETER(Dimension.LENGTH, "millimeter", "millimeters", 0.001, 0.0),
        MILE(Dimension.LENGTH, "mile", "miles", 1609.35, 0.0),
        YARD(Dimension.LENGTH, "yard", "yards", 0.9144, 0.0),
        FOOT(Dimension.LENGTH, "foot", "feet", 0.3048, 0.0),
        INCH(Dimension.LENGTH, "inch", "inches", 0.0254, 0.0),

        GRAM(Dimension.MASS, "gram", "grams", 1.0, 0.0),
        KILOGRAM(Dimension.MASS, "kilogram", "kilograms", 1000.0, 0.0),
        MILLIGRAM(Dimension.MASS, "milligram", "milligrams", 0.001, 0.0),
        POUND(Dimension.MASS, "pound", "pounds", 453.592, 0.0),
        OUNCE(Dimension.MASS, "ounce", "ounces", 28.3495, 0.0),

        CELSIUS(Dimension.TEMPERATURE, "degree Celsius", "degrees Celsius", 1.0, 0.0),
        KELVIN(Dimension.TEMPERATURE, "Kelvin", "Kelvins", 1.0, 273.15),
        FAHRENHEIT(Dimension.TEMPERATURE, "degree Fahrenheit", "degrees Fahrenheit", 5.0 / 9.0, 32.0)
    }

    enum class Dimension { NULL, LENGTH, MASS, TEMPERATURE }
}
