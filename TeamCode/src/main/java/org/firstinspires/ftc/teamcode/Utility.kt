package org.firstinspires.ftc.teamcode

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

    fun artifactTrajectory() {
        atan(Constants.ARTIFACT_EXIT_VELOCITY)
    }

    class Constants {
        companion object {
            const val SINGLE_ROTATION_CAROUSEL: Double = 0.4
            const val DOUBLE_ROTATION_CAROUSEL: Double = 0.76

            val GOAL_HEIGHT: Double = 38.75 / 39.37
            val ARTIFACT_EXIT_VELOCITY: Double = 10.0
        }
    }
}