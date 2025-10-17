package org.firstinspires.ftc.teamcode

import java.lang.reflect.Field

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

    class Constants {

        companion object {
            const val SINGLE_ROTATION_CAROUSEL: Double = 0.3733
        }
    }
}