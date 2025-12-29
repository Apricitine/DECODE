package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "Test", group = "Main")
class Test : Inheritable() {
    override fun loop() {
        super.loop()

        quickShot(up)

        log("averaged all left", ((leftSensor.normalizedColors.red + leftSensor.normalizedColors.green + leftSensor.normalizedColors.blue) / 3))
        log("normalized left", ((leftSensor.normalizedColors.red + leftSensor.normalizedColors.green + leftSensor.normalizedColors.blue)))

        log("front color:", frontColor)
        log("left color:", leftColor)
        log("right color:", rightColor)


    }
}