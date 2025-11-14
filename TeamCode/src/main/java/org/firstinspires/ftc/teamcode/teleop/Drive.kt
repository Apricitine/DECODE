package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "Drive", group = "Main")
class Drive : Inheritable() {
    override fun loop() {
        updateButtons()
        follower!!.update()
        panelsTelemetry.update()

        intake(rightBumper, leftBumper)
        carousel(y, b, x)
        plunger(a)
        lift() //hi i am luke and i stink. im a poo poo head
        flywheel(rightTrigger)
        hood(leftTrigger)
        fullCycle(up)
        obeliskTag()
        drive()

        log("position", follower!!.pose)
        log("velocity", follower!!.velocity)
        log("carousel angle", carousel.position)
        log("plunger angle", plunger.position)
    }
}