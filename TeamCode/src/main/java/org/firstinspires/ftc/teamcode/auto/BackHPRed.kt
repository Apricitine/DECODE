package org.firstinspires.ftc.teamcode.auto

import com.qualcomm.robotcore.eventloop.opmode.Autonomous

@Autonomous(name="Back Human Player Red", group="main")
class BackHPRed : BackHPBlue() { override val poses = super.poses.reflectOverX() }