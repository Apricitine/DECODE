package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "Test", group = "Main")
class Test : Inheritable() {
    override fun loop() {
        super.loop()

        align(b)



        logGoalTagDistance()
    }
}