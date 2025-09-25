package org.firstinspires.ftc.teamcode.teleop

import com.bylazar.configurables.annotations.Configurable
import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.pedropathing.follower.Follower
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.HeadingInterpolator
import com.pedropathing.paths.Path
import com.pedropathing.paths.PathChain
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.pedroPathing.Constants.Companion.createFollower
import java.util.function.Supplier

@Configurable
@TeleOp
abstract class Inheritable : OpMode() {
    protected var follower: Follower? = null
    protected var automatedDrive = false
    private var pathChain: Supplier<PathChain>? = null
    protected var telemetryM: TelemetryManager? = null
    private var slowMode = false
    private var slowModeMultiplier = 0.5

    override fun init() {
        follower = createFollower(hardwareMap)
        follower!!.setStartingPose(if (startingPose == null) Pose() else startingPose)
        follower!!.update()
        telemetryM = PanelsTelemetry.telemetry
        pathChain = Supplier {
            follower!!.pathBuilder()
                .addPath(Path(BezierLine({ follower!!.pose }, Pose(45.0, 98.0))))
                .setHeadingInterpolation(
                    HeadingInterpolator.linearFromPoint(
                        { follower!!.heading },
                        Math.toRadians(45.0),
                        0.8
                    )
                )
                .build()
        }
    }

    override fun start() {
        follower!!.startTeleopDrive(true)
    }

    companion object {
        var startingPose: Pose? = null
    }

    fun drive() {
        if (!slowMode) follower!!.setTeleOpDrive(
            -gamepad1.left_stick_y.toDouble(),
            -gamepad1.left_stick_x.toDouble(),
            -gamepad1.right_stick_x.toDouble(),
            true
        )
        else follower!!.setTeleOpDrive(
            -gamepad1.left_stick_y * slowModeMultiplier,
            -gamepad1.left_stick_x * slowModeMultiplier,
            -gamepad1.right_stick_x * slowModeMultiplier,
            true
        )
    }

    fun automatedDrive() {
        if (gamepad1.aWasPressed()) {
            follower!!.followPath(pathChain!!.get())
            automatedDrive = true
        }

        if (automatedDrive && (gamepad1.bWasPressed() || !follower!!.isBusy)) {
            follower!!.startTeleopDrive()
            automatedDrive = false
        }
    }
}