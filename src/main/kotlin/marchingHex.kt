import org.openrndr.application
import org.openrndr.color.ColorHSVa
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.noise.valueQuintic
import org.openrndr.math.Vector2
import kotlin.math.abs
import kotlin.random.Random

const val hex_W = 900
const val hex_H = 600

var hex_seed = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE)
var hex_res = 10
var hex_scale = .01

fun generatePoints(points: MutableList<MutableList<Double?>>, seconds: Double) =
    (0 until hex_W / hex_res step 2)
        .forEach { x ->
            (0 until hex_H / hex_res step 4)
                .forEach { y ->
                    points[x][y] =
                        abs(valueQuintic(hex_seed, x * hex_res * hex_scale, y * hex_res * hex_scale, seconds))
                    points[x + 1][y + 1] =
                        abs(valueQuintic(hex_seed, x * hex_res * hex_scale, y * hex_res * hex_scale, seconds))
                    points[x + 1][y + 2] =
                        abs(valueQuintic(hex_seed, x * hex_res * hex_scale, y * hex_res * hex_scale, seconds))
                    points[x][y + 3] =
                        abs(valueQuintic(hex_seed, x * hex_res * hex_scale, y * hex_res * hex_scale, seconds))
                }
        }

fun generateMarchingHex(points: MutableList<MutableList<Double?>>, marchingHex: MutableList<Vector2>) =
    (1 until hex_W / hex_res - 1)
        .forEach { x ->
            val startY = if (x % 2 == 0) 0 else 2
            (startY until (hex_H / hex_res - 2) step 4).forEach { y -> marchingHex.addAll(segregateHex(points, x, y)) }
        }

fun segregateHex(points: MutableList<MutableList<Double?>>, x: Int, y: Int): List<Vector2> {
    val one = Vector2((x + .5) * hex_res, (y + .5) * hex_res) // AB/2
    val two = Vector2((x + 1.0) * hex_res, (y + 1.5) * hex_res) // BD/2
    val three = Vector2((x + .5) * hex_res, (y + 2.5) * hex_res) // DF/2
    val four = Vector2((x - .5) * hex_res, (y + 2.5) * hex_res) // FE/2
    val five = Vector2((x - 1.0) * hex_res, (y + 1.5) * hex_res) // EC/2
    val six = Vector2((x - .5) * hex_res, (y + .5) * hex_res) // CA/2

    val binary = listOf(
        points[x][y], // A
        points[x + 1][y + 1], // B
        points[x - 1][y + 1], // C
        points[x + 1][y + 2], // D
        points[x - 1][y + 2], // E
        points[x][y + 3] // F
    ).map { if (it!! < .5) 0 else 1 }.joinToString("").toInt(2)

    return when (binary) {
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
        else -> listOf()
    }
}

fun main() = application {
    configure {
        width = 900
        height = 600
    }

    program {
        extend {
            drawer.stroke = ColorRGBa.WHITE
            drawer.strokeWeight = 2.0

            val points = MutableList(hex_W / hex_res) { MutableList<Double?>(hex_H / hex_res) { null } }
            val marchingHex = mutableListOf<Vector2>()
            val colors = mutableListOf<ColorRGBa>()

            generatePoints(points, seconds)
            generateMarchingHex(points, marchingHex)
            colors.addAll(marchingHex.zipWithNext().map {
                ColorHSVa(255 * valueQuintic(seed, it.first), .5, 1.0).toRGBa()
            })

            if (DEBUG) {
                (0 until points.size)
                    .forEach { x ->
                        (0 until points[x].size)
                            .forEach { y ->
                                if (points[x][y] != null) {
                                    drawer.stroke = if (points[x][y]!! < .5) ColorRGBa.BLUE else ColorRGBa.YELLOW
                                    drawer.circle(x * hex_res.toDouble(), y * hex_res.toDouble(), 4.0)
                                }
                            }
                    }
            }

            marchingHex.zipWithNext().filterIndexed { i, _ -> i % 2 == 0 }.zip(colors).forEach {
                drawer.stroke = it.second
                drawer.lineStrip(it.first.toList())
            }
        }
    }
}
