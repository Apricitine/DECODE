package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "Drive", group = "Main")
class Drive : Inheritable() {
    override fun loop() {
        super.loop()

        drive(1.0)
        intake(rightBumper, leftBumper)
        carousel(y, b, x)
        colorShot(left, right)

        lift(leftStick)
        plunger(a)
        flywheel(rightTrigger)
        quickShot(up)

        log("position", follower.pose)
        log("velocity", follower.velocity)
        log("carousel angle", carousel.position)
        log("plunger angle", plunger.position)

        log("front color:", frontColor)
        log("left color:", leftColor)
        log("right color:", rightColor)
    }
}