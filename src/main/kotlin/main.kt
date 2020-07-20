import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.noise.random
import org.openrndr.extra.noise.simplex
import org.openrndr.math.Vector2
import kotlin.math.abs
import kotlin.math.min

const val w = 900
const val h = 600

data class Triplet(val x: Int, val y: Int, val value: Double)

fun toto(a: Triplet, b: Triplet, c: Triplet, d: Triplet): List<Pair<Vector2, Vector2>> {
    val one = Vector2((a.x + b.x) / 2.0, (a.y + b.y) / 2.0)
    val two = Vector2((b.x + d.x) / 2.0, (b.y + d.y) / 2.0)
    val three = Vector2((c.x + d.x) / 2.0, (c.y + d.y) / 2.0)
    val four = Vector2((a.x + c.x) / 2.0, (a.y + c.y) / 2.0)

    return when (listOf(a.value, b.value, c.value, d.value).map { if (it < .5) 0 else 1 }.joinToString("").toInt(2)) {
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
    val res = 10
    val scale = .003
    val points = mutableListOf<Triplet>()
    val marchingSquares = mutableListOf<Pair<Vector2, Vector2>>()
    val rnd = random().toInt()

    (0..w step res).forEach { x ->
        (0..h step res).forEach { y ->
            points.add(Triplet(x, y, abs(simplex(rnd, x * res * scale, y * res * scale))))
        }
    }

    val chunkedPoints = points.chunked(min(w / res, h / res) + 1)

    (0 until chunkedPoints.size - 1).forEach { x ->
        (0 until chunkedPoints[x].size - 1).forEach { y ->
            marchingSquares.addAll(
                toto(
                    chunkedPoints[x][y],
                    chunkedPoints[x + 1][y],
                    chunkedPoints[x][y + 1],
                    chunkedPoints[x + 1][y + 1]
                )
            )
        }
    }

    configure {
        width = w
        height = h
    }

    program {
        extend {
            drawer.stroke = ColorRGBa.WHITE
            drawer.strokeWeight = 1.0

            /*points.forEach { p ->
                drawer.fill = if (p.value < .5) ColorRGBa.GRAY else ColorRGBa.WHITE
                drawer.circle(p.x.toDouble(), p.y.toDouble(), 5.0)
            }*/

            marchingSquares.forEach {
                drawer.lineStrip(it.toList())
            }
        }
    }
}
