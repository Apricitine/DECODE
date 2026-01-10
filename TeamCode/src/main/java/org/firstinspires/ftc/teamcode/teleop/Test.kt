package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.util.Button

@TeleOp(name = "Test", group = "Main")
class Test : Inheritable() {
    override fun loop() {
        super.loop()

        robot.turn(20.0, false)
    }
}