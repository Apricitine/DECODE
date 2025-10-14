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

        log("position", follower!!.pose)
        log("velocity", follower!!.velocity)
        log("automatedDrive", automatedDrive)
    }
}