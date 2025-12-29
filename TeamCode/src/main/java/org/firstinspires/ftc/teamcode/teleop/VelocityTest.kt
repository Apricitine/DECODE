package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.util.Button

@TeleOp(name = "Velocity Test", group = "main")
class VelocityTest : Inheritable() {
    override fun loop() {
        super.loop()

        if (up.`is`(Button.States.TAP)) flywheel.velocity += 10
        if (down.`is`(Button.States.TAP)) flywheel.velocity -= 10
        log("current velocity", flywheel.velocity)
    }
}