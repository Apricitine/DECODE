package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "Test", group = "Main")
class Test : Inheritable() {
    override fun loop() {
        updateButtons()
        follower.update()
        panelsTelemetry.update()

        updateColors()
        quickShot(left, right)

        log("front color:", frontColor)
        log("left color:", leftColor)
        log("right color:", rightColor)

    }
}