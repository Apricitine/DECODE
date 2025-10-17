package org.firstinspires.ftc.teamcode.util


class Button {
    enum class States {
        TAP,  // moment press down
        DOUBLE_TAP,  // pressed down in quick succession
        HELD,  // continued press down
        UP,  // moment of release
        OFF,  // continued release
        NOT_INITIALIZED
    }

    var state: States
        private set
    private var lastTapped: Long = -1

    init {
        state = States.NOT_INITIALIZED
    }

    private fun doubleTapIntervalNotSet(): Boolean {
        return doubleTapIntervalMs == -1
    }

    //## Safety - this method assumes that the buttonPressed parameter always
    // refers to the same button on the gamepad. There is no checking. The
    // update method is designed to be called once for each cycle of the main
    // loop in a TeleOp OpMode.
    //?? For a true toggle you could use a double-tap to turn an operation on,
    // for example displaying a telemetry message on the Driver Station, and
    // a second double-tap to turn the operation off. Or, if you want a single-
    // tap toggle you could supply an enum to the constructor for SINGLE_TAP_TOGGLE
    // or DOUBLE_TAP_TOGGLE.
    fun update(buttonPressed: Boolean): States {
        if (buttonPressed) {
            if (state == States.OFF || state == States.UP || state == States.NOT_INITIALIZED) {
                if (System.currentTimeMillis() - lastTapped < doubleTapIntervalMs) state =
                    States.DOUBLE_TAP
                else {
                    lastTapped = System.currentTimeMillis()
                    state = States.TAP
                }
            } else {
                state = States.HELD
            }
        } else {
            state = if (state == States.HELD || state == States.TAP || state == States.DOUBLE_TAP) {
                States.UP
            } else {
                States.OFF
            }
        }
        return state
    }

    fun `is`(state: States): Boolean {
        return this.state == state
    }

    companion object {
        private const val doubleTapIntervalMs = 500
    }
}