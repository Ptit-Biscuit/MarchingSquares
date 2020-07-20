import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.noise.valueQuintic
import org.openrndr.math.Vector2
import kotlin.math.abs
import kotlin.random.Random

const val DEBUG = false
const val W = 900
const val H = 600

val seed = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE)
var res = 10
var scale = .003

fun segregate(points: List<List<Double>>, x: Int, y: Int): List<Pair<Vector2, Vector2>> {
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
        1, 14 -> listOf(Pair(two, three))
        2, 13 -> listOf(Pair(three, four))
        3, 12 -> listOf(Pair(two, four))
        4, 11 -> listOf(Pair(one, two))
        5, 10 -> listOf(Pair(one, three))
        6 -> listOf(Pair(one, four), Pair(two, three))
        7, 8 -> listOf(Pair(one, four))
        9 -> listOf(Pair(one, two), Pair(three, four))
        else -> listOf()
    }
}

fun main() = application {
    val points = MutableList(W / res + 1) { MutableList(H / res + 1) { .0 } }
    val marchingSquares = mutableListOf<Pair<Vector2, Vector2>>()

    (0 until W step res).forEach { x ->
        (0 until H step res).forEach { y ->
            points[x / res][y / res] = abs(valueQuintic(seed, x * res * scale, y * res * scale))
        }
    }

    (0 until points.size - 1).forEachIndexed { ix, _ ->
        (0 until points[ix].size - 1).forEachIndexed { iy, _ ->
            marchingSquares.addAll(segregate(points, ix, iy))
        }
    }

    configure {
        width = W
        height = H
    }

    program {
        extend {
            drawer.stroke = ColorRGBa.WHITE
            drawer.strokeWeight = 2.0

            if (DEBUG) {
                (0 until points.size).forEachIndexed { ix, _ ->
                    (0 until points[ix].size).forEachIndexed { iy, _ ->
                        drawer.fill = if (points[ix][iy] < .5) ColorRGBa.GRAY else ColorRGBa.WHITE
                        drawer.circle(ix * res.toDouble(), iy * res.toDouble(), 4.0)
                    }
                }
            }

            marchingSquares.forEach {
                drawer.lineStrip(it.toList())
            }
        }
    }
}
