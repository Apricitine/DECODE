package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "PathingDrive", group = "Main")
class PathingDrive : Inheritable() {
    override fun loop() {
        follower!!.update()
        panelsTelemetry.update()
        if (!automatedDrive) drive()
        automatedDrive()
        intake()

        panelsTelemetry.debug("position", follower!!.pose)
        panelsTelemetry.debug("velocity", follower!!.velocity)
        panelsTelemetry.debug("automatedDrive", automatedDrive)
    }
}