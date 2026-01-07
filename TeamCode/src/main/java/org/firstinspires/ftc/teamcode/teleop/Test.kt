package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.util.Button

@TeleOp(name = "Test", group = "Main")
class Test : Inheritable() {
    override fun loop() {
        super.loop()

        if (gamepad1.left_stick_y > 0.1) {
            rightLift.power = 0.4
            leftLift.power = 0.4
        } else {
            rightLift.power = 0.0
            leftLift.power = 0.0
        }

        log("pos", rightLift.currentPosition)
    }
}