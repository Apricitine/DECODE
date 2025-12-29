package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "Reset", group = "Main")
class Reset : Inheritable() {
    override fun loop() {
        super.loop()

        drive(driveSpeed(rightBumper))

        log("y", gamepad1.yWasReleased())
        log("position", follower.pose)
        log("velocity", follower.velocity)
        log("carousel state", carouselState)
        log("carousel angle", carousel.position)
        log("plunger angle", plunger.position)
        log("left lift amount", leftLift.currentPosition)
        log("right lift amount", rightLift.currentPosition)
        log("flywheel ", rightLift.currentPosition)
        log("hood", hood.position)
    }
}