import org.openrndr.application
import org.openrndr.color.ColorHSVa
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.noise.valueQuintic
import org.openrndr.math.Vector2
import kotlin.math.abs
import kotlin.random.Random

const val DEBUG = false
const val W = 900
const val H = 600

var seed = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE)
var res = 10
var scale = .003

fun generatePoints(
    points: MutableList<MutableList<Double>>,
    seconds: Double
) =
    (0 until W / res)
        .forEach { x ->
            (0 until H / res)
                .forEach { y ->
                    points[x][y] =
                        abs(valueQuintic(seed, x * res * res * scale, y * res * res * scale, seconds))
                }
        }

fun generateMarchingSquares(points: MutableList<MutableList<Double>>, marchingSquares: MutableList<Vector2>) =
    (0 until points.size - 1)
        .forEach { x ->
            (0 until points[x].size - 1)
                .forEach { y ->
                    marchingSquares.addAll(segregate(points, x, y))
                }
        }

fun segregate(points: List<List<Double>>, x: Int, y: Int): List<Vector2> {
    val one = Vector2((x + .5) * res, y * res * 1.0)
    val two = Vector2((x + 1.0) * res, (y + .5) * res)
    val three = Vector2((x + .5) * res, (y + 1.0) * res)
    val four = Vector2(x * res * 1.0, (y + .5) * res)

    return when (listOf(
        points[x][y],
        points[x + 1][y],
        points[x][y + 1],
        points[x + 1][y + 1]
    ).map { if (it < .5) 0 else 1 }.joinToString("").toInt(2)) {
        1, 14 -> listOf(two, three)
        2, 13 -> listOf(three, four)
        3, 12 -> listOf(two, four)
        4, 11 -> listOf(one, two)
        5, 10 -> listOf(one, three)
        6 -> listOf(one, four, two, three)
        7, 8 -> listOf(one, four)
        9 -> listOf(one, two, three, four)
        else -> listOf()
    }
}

fun main() = application {
    configure {
        width = W
        height = H
    }

    program {
        keyboard.keyDown.listen {
            if (it.name == "r") {
                seed = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE)
                res = 10
                scale = .003
            }

            if (it.name == "j" && res > 1) {
                res -= 1
            }

            if (it.name == "l") {
                res += 1
            }

            if (it.name == "-" && scale > .001) {
                scale -= .001
            }

            if (it.name == "+") {
                scale += .001
            }
        }

        keyboard.keyRepeat.listen {
            if (it.name == "j" && res > 1) {
                res -= 1
            }

            if (it.name == "l") {
                res += 1
            }

            if (it.name == "-" && scale > .001) {
                scale -= .001
            }

            if (it.name == "+") {
                scale += .001
            }
        }

        extend {
            drawer.stroke = ColorRGBa.WHITE
            drawer.strokeWeight = 2.0

            val points = MutableList(W / res + 1) { MutableList(H / res + 1) { .0 } }
            val marchingSquares = mutableListOf<Vector2>()
            val colors = mutableListOf<ColorRGBa>()

            if (DEBUG) {
                (0 until points.size)
                    .forEach { x ->
                        (0 until points[x].size)
                            .forEach { y ->
                                drawer.fill = if (points[x][y] < .5) ColorRGBa.GRAY else ColorRGBa.WHITE
                                drawer.circle(x * res.toDouble(), y * res.toDouble(), 4.0)
                            }
                    }
            }

            generatePoints(points, seconds)
            generateMarchingSquares(points, marchingSquares)
            colors.addAll(marchingSquares.zipWithNext().map {
                ColorHSVa(255 * valueQuintic(seed, it.first), .5, 1.0).toRGBa()
            })

            marchingSquares.zipWithNext().filterIndexed { i, _ -> i % 2 == 0 }.zip(colors).forEach {
                drawer.stroke = it.second
                drawer.lineStrip(it.first.toList())
            }
        }
    }
}
