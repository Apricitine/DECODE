package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "PathingDrive", group = "Main")
class Test : Inheritable() {
    override fun loop() {
        follower!!.update()
        panelsTelemetry.update()
        intake()
        carousel()

        log("position", follower!!.pose)
        log("velocity", follower!!.velocity)
        log("automatedDrive", automatedDrive)
        log("carousel state", carouselState)
    }
}