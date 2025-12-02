package org.firstinspires.ftc.teamcode.pedroPathing

import com.pedropathing.follower.Follower
import com.pedropathing.follower.FollowerConstants
import com.pedropathing.ftc.FollowerBuilder
import com.pedropathing.ftc.drivetrains.MecanumConstants
import com.pedropathing.ftc.localization.Encoder
import com.pedropathing.ftc.localization.constants.ThreeWheelIMUConstants
import com.pedropathing.paths.PathConstraints
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap

class Constants {
    companion object {
        private var followerConstants: FollowerConstants = FollowerConstants().mass(13.51)
        private var pathConstraints: PathConstraints = PathConstraints(0.99, 100.0, 1.0, 1.0)
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
        private var localizerConstants: ThreeWheelIMUConstants =
            ThreeWheelIMUConstants()
                /* these need to be determined */
                .forwardTicksToInches(.001989436789)
                .strafeTicksToInches(.001989436789)
                .turnTicksToInches(.001989436789)
                /* these are solid */
                .leftPodY(3.125)
                .rightPodY(-3.125)
                .strafePodX(-4.25)
                .leftEncoder_HardwareMapName("leftFront")
                .rightEncoder_HardwareMapName("rightFront")
                .strafeEncoder_HardwareMapName("rightRear")
                /* these need to be determined */
                .leftEncoderDirection(Encoder.FORWARD)
                .rightEncoderDirection(Encoder.FORWARD)
                .strafeEncoderDirection(Encoder.FORWARD)
                /* these are solid */
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