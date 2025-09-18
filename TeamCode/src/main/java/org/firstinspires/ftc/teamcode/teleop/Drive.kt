package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "Drive", group = "Main")
class Drive : Inheritable() {
    override fun loop() {
        follower!!.update()
        telemetryM!!.update()
        if (!automatedDrive) drive()
        automatedDrive()

        telemetryM!!.debug("position", follower!!.pose)
        telemetryM!!.debug("velocity", follower!!.velocity)
        telemetryM!!.debug("automatedDrive", automatedDrive)
    }
}