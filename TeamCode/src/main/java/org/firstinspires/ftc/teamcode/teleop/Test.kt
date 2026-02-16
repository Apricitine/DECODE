package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.util.Button
import kotlin.math.atan2

@TeleOp(name = "Test", group = "Test")
class Test : Inheritable() {
    override fun loop() {
        super.loop()

        updateTagPoseAlliance(20)
        val tag = goalTagPose ?: return
        val headingError = atan2(tag.x, tag.y)
        log("heading error", headingError)
        log("stick", gamepad1.right_stick_x)

        robot.setTeleOpDrive(
            0.0,
            0.0,
            -headingError,
            true
        )
    }
}