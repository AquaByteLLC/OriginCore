package animation.math

import commons.interpolation.Interpolation
import commons.math.MathUtils


public fun <T : Comparable<T>> T.coerceAtMost(maximumValue: T): T {
    return if (this > maximumValue) maximumValue else this
}

public fun Float.coerceAtMost(maximumValue: Float): Float {
    return if (this > maximumValue) maximumValue else this
}

fun ClosedRange<Float>.randomTriangular() = MathUtils.randomTriangular(start, endInclusive)

fun ClosedRange<Float>.randomTriangular(normalizedMode: Float): Float =
    MathUtils.randomTriangular(
        start, endInclusive,
        normalizedMode * (endInclusive - start) + start
    )

fun ClosedRange<Float>.lerp(progress: Float): Float =
    progress * (endInclusive - start) + start

fun ClosedRange<Float>.interpolate(progress: Float, interpolation: Interpolation): Float =
    interpolation.apply(progress) * (endInclusive - start) + start
