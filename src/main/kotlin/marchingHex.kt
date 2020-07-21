import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.math.Vector2
import kotlin.math.cos
import kotlin.math.sin

const val rez = 50

val hexHeight = sin(Math.toRadians(30.0)) * rez;
val hexRadius = cos(Math.toRadians(30.0)) * rez;
val hexRectangleHeight = 2 * hexHeight + rez;
val hexRectangleWidth = 2 * hexRadius;

fun hexPoints(x: Double, y: Double) = listOf(
    Vector2(x + hexRadius, y),
    Vector2(x + hexRectangleWidth, y + hexHeight),
    Vector2(x + hexRectangleWidth, y + hexHeight + rez),
    Vector2(x + hexRadius, y + hexRectangleHeight),
    Vector2(x, y + hexHeight + rez),
    Vector2(x, y + hexHeight)
)

fun main() = application {
    configure {
        width = 900
        height = 600
    }

    program {
        var once = true

        extend {
            drawer.stroke = ColorRGBa.WHITE
            drawer.strokeWeight = 2.0

            if (once) {
                (0 until width / rez)
                    .map { x -> List(height / rez) { x }.zip(0 until height / rez) }
                    .flatten()
                    .map { hexPoints(it.first.toDouble(), it.second.toDouble()) }
                    .onEach { println(it) }
                    .flatten()
                    .forEach {
                        drawer.circle(it.x, it.y, 4.0)
                    }

                once = false
            }
        }
    }
}