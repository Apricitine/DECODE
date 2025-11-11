package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "Test", group = "Main")
class Test : Inheritable() {
    override fun loop() {
        updateButtons()
        follower!!.update()
        panelsTelemetry.update()
        intake(rightBumper)
        carousel(y, b, x)
        plunger(a)
        lift()
        flywheel(rightTrigger)
        hood(leftBumper)

        log("y", gamepad1.yWasReleased())
        log("position", follower!!.pose)
        log("velocity", follower!!.velocity)
        log("automatedDrive", automatedDrive)
        log("carousel state", carouselState)
        log("carousel angle", carousel.position)
        log("plunger angle", plunger.position)
        log("left lift amount", leftLift.currentPosition)
        log("right lift amount", rightLift.currentPosition)
        log("flywheel ", rightLift.currentPosition)
        log("hood", hood.position)
    }
}