package org.firstinspires.ftc.teamcode.auto

import com.qualcomm.robotcore.eventloop.opmode.Autonomous

@Autonomous(name="Goal Complete Red", group="main")
class GoalCompleteRed : GoalCompleteBlue() { override val poses = super.poses.reflectOverX() }