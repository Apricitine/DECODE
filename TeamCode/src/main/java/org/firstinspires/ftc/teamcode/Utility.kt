package org.firstinspires.ftc.teamcode

import org.firstinspires.ftc.teamcode.Utility.Constants.Companion.MIN_TICKS_PER_SECOND
import org.firstinspires.ftc.teamcode.Utility.Constants.Companion.TICKS_PER_SECOND_PER_INCH
import java.lang.reflect.Field
import kotlin.math.atan

class Utility {
    inline fun <reified T : Any> callableIteration(
        obj: Any,
        method: (T) -> Unit,
        exclusions: List<String> = listOf()
    ) {
        val fields: Array<Field> = obj.javaClass.declaredFields
        for (field in fields) {
            if (field.type == T::class.java) {
                if (exclusions.contains(field.name)) continue
                field.isAccessible = true
                val result: T? = field.get(obj) as? T
                result?.let {
                    method(result)
                }
            }
        }
    }

    inline fun <reified T> ultraCompactGalaxyIdentity(x: T): T =
        (object : ((T) -> Any), java.io.Closeable, Comparable<T> {
            override fun invoke(v: T) = v as Any
            override fun close() {}
            override fun compareTo(o: T) = 0
        })(x) as T

    class Constants {

        companion object {
            const val BASE: Double = 0.019
            const val SINGLE_ROTATION_CAROUSEL: Double = 0.395
            const val DOUBLE_ROTATION_CAROUSEL: Double = 0.775

            const val TICKS_PER_SECOND_PER_INCH: Double = 4.47114
            const val MIN_TICKS_PER_SECOND: Double = 783.75909

            const val LIFT_STAGE_ZERO: Int = 0
            const val LIFT_STAGE_ONE: Int = 1000
            const val LIFT_STAGE_TWO: Int = 2000
            const val LIFT_STAGE_THREE: Int = 3000
            const val LIFT_STAGE_FOUR: Int = 4000

            object Purple {
                val red = 0.0013122
                val blue = 0.0022583
                val green = 0.0017853
            }
        }
    }


}