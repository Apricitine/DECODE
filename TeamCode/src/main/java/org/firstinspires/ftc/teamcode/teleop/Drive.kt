package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "Drive", group = "Main")
class Drive : Inheritable() {
    override fun loop() {
        super.loop()

        drive(1.0)
        intake(rightBumper, leftBumper)
        carousel(y, b, x)
        plunger(a)
        flywheel(rightTrigger)

        resetShot(down)
        colorShot(left, right)
        quickShot(up)

        log("subsystem states", "")
        log("| carousel state", carouselState)
        log("| plunger state", plungerBusy)
        log("| lift state", liftState)

        log("\ncolor states (last updated ${kotlin.math.truncate(timeSinceLastColorUpdate.seconds())}s ago)", "")
        log("| front", frontColor)
        log("| left", leftColor)
        log("| right", rightColor)

        log("\nflywheel state", "")
        log("| latest target velocity", targetVelocity)
        log("| current velocity", flywheel.velocity)
        log("| can we shoot?", if (canShoot) "YES" else "NO")

        log("\nlifting?", lifting)

    }
}