package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import kotlin.math.truncate

@TeleOp(name = "Blue Drive Single <3", group = "Main")
class BlueDriveSinglePlayer : Inheritable() {
    override fun loop() {
        super.loop()

        updateTagPoseAlliance(20)

        drive(1.0)
        intake(leftBumper1, leftTrigger1)
        carousel(y1, b1, x1)
        plunger(a1)
        flywheel(rightBumper1)

        resetShot(down1)
        colorShot(left1, right1)
        quickShot(up1)

        log("subsystem states", "")
        log("| carousel state", carouselState)
        log("| plunger state", plungerBusy)
        log("| lift state", liftState)

        log("\ncolor states (last updated ${truncate(timeSinceLastColorUpdate.seconds())}s ago)", "")
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