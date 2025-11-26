package org.firstinspires.ftc.teamcode.pedroPathing

import com.pedropathing.control.FilteredPIDFCoefficients
import com.pedropathing.follower.Follower
import com.pedropathing.follower.FollowerConstants
import com.pedropathing.ftc.FollowerBuilder
import com.pedropathing.ftc.drivetrains.MecanumConstants
import com.pedropathing.ftc.localization.Encoder
import com.pedropathing.ftc.localization.constants.ThreeWheelConstants
import com.pedropathing.ftc.localization.constants.ThreeWheelIMUConstants
import com.pedropathing.ftc.localization.constants.TwoWheelConstants
import com.pedropathing.paths.PathConstraints
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.PIDFCoefficients
import java.util.logging.Filter

class Constants {
    companion object {
        private var followerConstants: FollowerConstants =
            FollowerConstants()
                .mass(13.51)
                .forwardZeroPowerAcceleration(-26.27)
                .lateralZeroPowerAcceleration(-65.62)
                .translationalPIDFCoefficients(
                    com
                        .pedropathing
                        .control
                        .PIDFCoefficients(0.1, 0.0, 0.0, 0.0)
                )
                .headingPIDFCoefficients(
                    com
                        .pedropathing
                        .control
                        .PIDFCoefficients(1.0, 0.0, 0.0, 0.01)
                )
                .drivePIDFCoefficients(
                    FilteredPIDFCoefficients(
                        0.025,
                        0.0,
                        0.00001,
                        0.6,
                        0.01
                    )
                )
                .centripetalScaling(0.0005)
        private var pathConstraints: PathConstraints = PathConstraints(
            0.99,
            100.0,
            2.0,
            1.0
        )
        private var mecanumConstants: MecanumConstants =
            MecanumConstants()
                .maxPower(1.0)
                .leftFrontMotorName("leftFront")
                .leftRearMotorName("leftRear")
                .rightFrontMotorName("rightFront")
                .rightRearMotorName("rightRear")
                .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
                .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
                .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
                .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
                .xVelocity(69.60)
                .yVelocity(58.31)
        var localizerConstants: ThreeWheelIMUConstants =
            ThreeWheelIMUConstants()
                .forwardTicksToInches(.001989436789)
                .strafeTicksToInches(.001989436789)
                .turnTicksToInches(.001989436789)
                .leftPodY(4.6875)
                .rightPodY(-4.6875)
                .strafePodX(-3.5)
                .leftEncoder_HardwareMapName("flywheel")
                .rightEncoder_HardwareMapName("rightRear")
                .strafeEncoder_HardwareMapName("rightFront")
                .leftEncoderDirection(Encoder.FORWARD)
                .rightEncoderDirection(Encoder.FORWARD)
                .strafeEncoderDirection(Encoder.FORWARD)
                .IMU_HardwareMapName("imu")
                .IMU_Orientation(
                    RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP
                    )
                )
        @JvmStatic
        fun createFollower(hardwareMap: HardwareMap?): Follower {
            return FollowerBuilder(followerConstants, hardwareMap)
                .threeWheelIMULocalizer(localizerConstants)
                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(mecanumConstants)
                .build()
        }
    }
}