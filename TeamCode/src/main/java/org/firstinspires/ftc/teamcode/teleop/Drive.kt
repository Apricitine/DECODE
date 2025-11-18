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
        drive()


        log("position", follower.pose)
        log("velocity", follower.velocity)
        log("carousel angle", carousel.position)
        log("plunger angle", plunger.position)
        log("right sensor", identifyColor(rightSensor))
    }
}