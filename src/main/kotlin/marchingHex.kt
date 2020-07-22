import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.noise.valueQuintic
import org.openrndr.math.Vector2
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

const val hex_W = 900
const val hex_H = 600

var hex_seed = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE)
var hex_res = 50
var hex_scale = .003

val hexHeight = sin(Math.toRadians(30.0)) * hex_res;
val hexRadius = cos(Math.toRadians(30.0)) * hex_res;
val hexRectangleHeight = 2 * hexHeight + hex_res;
val hexRectangleWidth = 2 * hexRadius;

var stepX = 1
var stepY = 1

fun generatePoints(points: MutableList<Triple<Double, Double, Double>>) =
    (0..hex_W step hexRectangleWidth.toInt())
        .forEach { x ->
            (0..hex_H step hexRectangleHeight.toInt())
                .forEach { y ->
                    hexPoints(x.toDouble(), y.toDouble())
                        .forEach {
                            points.add(
                                Triple(
                                    it.x,
                                    it.y,
                                    abs(
                                        valueQuintic(
                                            hex_seed,
                                            (x + stepX++) * hex_res * hex_scale,
                                            (y + stepY++) * hex_res * hex_scale
                                        )
                                    )
                                )
                            )

                            stepX += 2
                            stepY += 2
                        }
                }
        }

fun hexPoints(x: Double, y: Double) = listOf(
    Vector2(x + hexRadius, y), // A
    Vector2(x + hexRectangleWidth, y + hexHeight), // B
    Vector2(x, y + hexHeight), // C
    Vector2(x + hexRectangleWidth, y + hexHeight + hex_res), // D
    Vector2(x, y + hexHeight + hex_res), // E
    Vector2(x + hexRadius, y + hexRectangleHeight) // F
)

fun generateMarchingHex(points: MutableList<Triple<Double, Double, Double>>, marchingHex: MutableList<Vector2>) =
    (0 until points.size - 1 step 6).forEach {
        marchingHex.addAll(segregateHex(points, points[it]))
    }

fun segregateHex(
    points: MutableList<Triple<Double, Double, Double>>,
    p: Triple<Double, Double, Double>
): List<Vector2> {
    val one = Vector2(p.first + hexRadius * .5, p.second + hexHeight * .5) // AB/2
    val two = Vector2(p.first + hexRectangleWidth, p.second + hexHeight + hex_res * .5) // BD/2
    val three = Vector2(p.first + hexRadius * .5, p.second + hexRectangleHeight * .75) // DF/2
    val four = Vector2(p.first + hexRadius * .5, p.second + hexRectangleHeight * .75) // FE/2
    val five = Vector2(p.first, p.second + hexHeight + hex_res * .5) // EC/2
    val six = Vector2(p.first + hexRadius * .5, p.second + hexHeight * .5) // CA/2

    val binary = listOf(
        p,
        points[points.indexOf(p) + 1],
        points[points.indexOf(p) + 2],
        points[points.indexOf(p) + 3],
        points[points.indexOf(p) + 4],
        points[points.indexOf(p) + 5]
    ).map { if (it.third < .5) 0 else 1 }.joinToString("").toInt(2)

    println(binary)

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
        16, 47 -> listOf()
        17, 46 -> listOf()
        18, 45 -> listOf()
        19, 44 -> listOf()
        20, 43 -> listOf()
        21, 42 -> listOf()
        22, 41 -> listOf()
        23, 40 -> listOf()
        24, 39 -> listOf()
        25, 38 -> listOf()
        26, 37 -> listOf()
        27, 36 -> listOf()
        28, 35 -> listOf()
        29, 34 -> listOf()
        30, 33 -> listOf()
        31, 32 -> listOf(one, six)
        else -> listOf()
    }
}

fun main() = application {
    configure {
        width = 900
        height = 600
    }

    program {
        val points = mutableListOf<Triple<Double, Double, Double>>()
        val marchingHex = mutableListOf<Vector2>()

        generatePoints(points)
        generateMarchingHex(points, marchingHex)

        extend {
            drawer.stroke = ColorRGBa.WHITE
            drawer.strokeWeight = 2.0

            points.forEach { t ->
                drawer.stroke = if (t.third < .5) ColorRGBa.BLUE else ColorRGBa.YELLOW
                drawer.circle(t.first, t.second, 4.0)
            }

            drawer.stroke = ColorRGBa.WHITE

            marchingHex.zipWithNext().filterIndexed { i, _ -> i % 2 == 0 }.forEach {
                drawer.lineStrip(it.toList())
            }
        }
    }
}
