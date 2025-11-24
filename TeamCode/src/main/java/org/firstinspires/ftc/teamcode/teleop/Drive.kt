package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "Drive", group = "Main")
class Drive : Inheritable() {
    override fun loop() {
        updateButtons()
        follower.update()
        panelsTelemetry.update()

        intake(rightBumper, leftBumper)
        carousel(y, b, x)
        plunger(a)
        lift()
        flywheel(rightTrigger)
        hood(leftTrigger)
        fullCycle(up)
        drive(1.0)
        quickShot(left, right)


        log("position", follower.pose)
        log("velocity", follower.velocity)
        log("carousel angle", carousel.position)
        log("plunger angle", plunger.position)

        log("front color:", frontColor)
        log("left color:", leftColor)
        log("right color:", rightColor)
    }
}