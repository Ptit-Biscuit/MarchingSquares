import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.noise.valueQuintic
import org.openrndr.math.Vector2
import kotlin.math.abs
import kotlin.random.Random

fun main() = application {
    configure {
        width = 900
        height = 600
    }

    program {
        extend {
            val res = 10
            drawer.stroke = ColorRGBa.WHITE

            MutableList((width + 1) * (height + 1)) {
                abs(valueQuintic(Random.nextInt(), it % width.toDouble(), (width / res) * it / width.toDouble()))
            }.let {
                (0 until width / res).forEach { x ->
                    (0 until height / res).forEach { y ->
                        val one = Vector2((x + .5) * res, y * 1.0 * res)
                        val two = Vector2((x + 1.0) * res, (y + .5) * res)
                        val three = Vector2((x + .5) * res, (y + 1.0) * res)
                        val four = Vector2(x * 1.0 * res, (y + .5) * res)

                        when (
                            listOf(it[x * y], it[(x + 1) * y], it[x * (y + 1)], it[(x + 1) * (y + 1)])
                                .joinToString("") { if (it < .5) "0" else "1" }.toInt(2)
                            ) {
                            1, 14 -> listOf(two, three)
                            2, 13 -> listOf(three, four)
                            3, 12 -> listOf(two, four)
                            4, 11 -> listOf(one, two)
                            5, 10 -> listOf(one, three)
                            6 -> listOf(one, four, two, three)
                            7, 8 -> listOf(one, four)
                            9 -> listOf(one, two, three, four)
                            else -> null
                        }?.let { drawer.lineStrips(it.zipWithNext { a, b -> listOf(a, b) }) }
                    }
                }
            }
        }
    }
}
