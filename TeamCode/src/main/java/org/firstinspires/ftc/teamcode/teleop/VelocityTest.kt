package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.util.Button

@TeleOp(name = "Velocity Test", group = "main")
class VelocityTest : Inheritable() {
    var v = 1000.0

    override fun loop() {
        super.loop()

        plunger(a)

        if (up.`is`(Button.States.TAP)) v += 10.0
        if (down.`is`(Button.States.TAP)) v -= 10.0
        flywheel.velocity = v
        log("current velocity", flywheel.velocity)
        log("target velocity", v)
    }
}