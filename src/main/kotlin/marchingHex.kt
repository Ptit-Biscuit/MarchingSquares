import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.noise.valueQuintic
import org.openrndr.math.Vector2
import kotlin.math.abs
import kotlin.random.Random

fun generateValue(seed: Int, x: Int, y: Int, res: Double, s: Double = .0) = abs(valueQuintic(seed, x * res, y * res, s))

fun main() = application {
    configure {
        width = 900
        height = 600
    }

    program {
        extend {
            val res = 10
            val seed = Random.nextInt()

            drawer.stroke = ColorRGBa.WHITE
            (0 until width / res step 2).flatMap { x ->
                (0 until height / res step 4).flatMap { y ->
                    listOf(
                        Triple(x, y, generateValue(seed, x, y, res * .01)),
                        Triple(x + 1, y + 1, generateValue(seed, x + 1, y + 1, res * .01)),
                        Triple(x + 1, y + 2, generateValue(seed, x + 1, y + 2, res * .01)),
                        Triple(x, y + 3, generateValue(seed, x, y + 3, res * .01))
                    )
                }
            }.apply {
                (1 until width / res - 1).forEach { x ->
                    ((if (x % 2 == 0) 0 else 2) until (height / res - 2) step 4).forEach { y ->
                        val p = this.find { it.first == x && it.second == y }!!
                        val one = Vector2((p.first + .5) * res, (p.second + .5) * res) // AB/2
                        val two = Vector2((p.first + 1.0) * res, (p.second + 1.5) * res) // BD/2
                        val three = Vector2((p.first + .5) * res, (p.second + 2.5) * res) // DF/2
                        val four = Vector2((p.first - .5) * res, (p.second + 2.5) * res) // FE/2
                        val five = Vector2((p.first - 1.0) * res, (p.second + 1.5) * res) // EC/2
                        val six = Vector2((p.first - .5) * res, (p.second + .5) * res) // CA/2

                        when (listOf(
                            p.third, // A
                            find { it.first == p.first + 1 && it.second == p.second + 1 }?.third, // B
                            find { it.first == p.first - 1 && it.second == p.second + 1 }?.third, // C
                            find { it.first == p.first + 1 && it.second == p.second + 2 }?.third, // D
                            find { it.first == p.first - 1 && it.second == p.second + 2 }?.third, // E
                            find { it.first == p.first && it.second == p.second + 3 }?.third // F
                        ).joinToString("") { if (it != null && it > .5) "1" else "0" }.toInt(2)) {
                            1, 62 -> listOf(three, four)
                            2, 61 -> listOf(four, five)
                            3, 60 -> listOf(three, five)
                            4, 59 -> listOf(two, three)
                            5, 58 -> listOf(two, four)
                            6, 57 -> listOf(two, five, three, four)
                            7, 56 -> listOf(two, five)
                            8, 55 -> listOf(five, six)
                            9, 54 -> listOf(four, five, three, six)
                            10, 53 -> listOf(four, six)
                            11, 52 -> listOf(three, six)
                            12, 51 -> listOf(three, five, two, six)
                            13, 50 -> listOf(four, five, two, six)
                            14, 49 -> listOf(three, four, two, six)
                            15, 48 -> listOf(two, six)
                            16, 47 -> listOf(one, two)
                            17, 46 -> listOf(one, four, two, three)
                            18, 45 -> listOf(one, five, two, four)
                            19, 44 -> listOf(one, five, two, three)
                            20, 43 -> listOf(one, three)
                            21, 42 -> listOf(one, four)
                            22, 41 -> listOf(one, five, three, four)
                            23, 40 -> listOf(one, five)
                            24, 39 -> listOf(one, six, two, five)
                            25 -> listOf(one, two, three, four, five, six)
                            26, 37 -> listOf(one, six, two, four)
                            27, 36 -> listOf(one, six, two, three)
                            28, 35 -> listOf(one, six, three, five)
                            29, 34 -> listOf(one, six, four, five)
                            30, 33 -> listOf(one, six, three, four)
                            31, 32 -> listOf(one, six)
                            38 -> listOf(one, six, two, three, four, five)
                            else -> null
                        }?.let { drawer.lineStrips(it.zipWithNext { a, b -> listOf(a, b) }) }
                    }
                }
            }
        }
    }
}
