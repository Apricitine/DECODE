package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.util.Button

@TeleOp(name = "Test", group = "Test")
class Test : Inheritable() {
    override fun loop() {
        drive(0.25)
        updateTagPoseAlliance(20)
        logGoalTagDistance()
    }
}