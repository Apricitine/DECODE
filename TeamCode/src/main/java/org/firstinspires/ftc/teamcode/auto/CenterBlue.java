//package org.firstinspires.ftc.teamcode.Autonomous; // make sure this aligns with class location
//
//import android.util.Size;
//
//import com.pedropathing.follower.Follower;
//import com.pedropathing.geometry.BezierLine;
//import com.pedropathing.geometry.Pose;
//import com.pedropathing.paths.Path;
//import com.pedropathing.paths.PathChain;
//import com.pedropathing.util.Timer;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
//import  com.qualcomm.robotcore.eventloop.opmode.OpMode;
//
//import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
//import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
//import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
//import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
//import org.firstinspires.ftc.teamcode.teleop.Inheritable;
//import org.firstinspires.ftc.teamcode.util.Button;
//import org.firstinspires.ftc.vision.VisionPortal;
//import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
//import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
//
//
//import java.util.ArrayList;
//import java.util.List;
//
//import kotlin.reflect.KClasses;
//
//
//@Autonomous(name = "CenterBlue", group = "Autonomous")
//@Disabled
//public class CenterBlue extends OpMode {
//
//    private Follower follower;
//    private Timer pathTimer, actionTimer, opmodeTimer;
//    private int pathState;
//
//    private final Pose startPose = new Pose(18.3, 125.6, Math.toRadians(315)); // Start Pose of our robot.
//    private final Pose camera = new Pose(34.2, 109.4, Math.toRadians(60));
//    private final Pose shootPosition = new Pose(48,96,Math.toRadians(120));
//    private final Pose firstStrike = new Pose(41.64,85.51,Math.toRadians(180));
//    private final Pose getFirstStrike = new Pose(21.70,85.51,Math.toRadians(180));
//
//    private Path scorePreload, getApriltag;
//
//    private PathChain shootBalls, positionToGetFirstStrike, intakeFirstStrike, shootStrikes, getTag;
//    private PathChain returnFromFirstStrikAndShoot;
//    private static final boolean USE_WEBCAM = true;  // true for webcam, false for phone camera
//    private AprilTagProcessor aprilTag;
//    private VisionPortal visionPortal;
//    private List<AprilTagDetection>  detectedTags = new ArrayList<>();
//    private int detectedAprilTagId;
//
//    private KotlinHelper kh = new KotlinHelper();
//    private Button trueButton;
//    private Button falseButton = new Button();
//
//    public void buildPaths() {
//        /* Take picture of Alphatag. We are using a BezierLine, which is a straight line. */
//        getTag = follower.pathBuilder()
//                .addPath(new BezierLine(startPose, camera))
//                .setLinearHeadingInterpolation(startPose.getHeading(), camera.getHeading())
//                .build();
//
//        /* This is our take a picture of the alphatag.  */
//        /*start the shooter wheel.*/
//        shootBalls = follower.pathBuilder()
//                .addPath(new BezierLine(camera, shootPosition))
//                .setLinearHeadingInterpolation(camera.getHeading(), firstStrike.getHeading())
//                 .build();
//
//        /* shoot in the correct order */
//
//        positionToGetFirstStrike  = follower.pathBuilder()
//                .addPath(new BezierLine(shootPosition, firstStrike))
//                .setLinearHeadingInterpolation(shootPosition.getHeading(), firstStrike.getHeading())
//                .build();
//
//        intakeFirstStrike = follower.pathBuilder()
//                .addPath(new BezierLine(firstStrike, getFirstStrike))
//                //we may have to lower our speed
//                .build();
//
//        shootStrikes = follower.pathBuilder()
//                .addPath(new BezierLine(getFirstStrike, shootPosition))
//                .setLinearHeadingInterpolation(getFirstStrike.getHeading(), shootPosition.getHeading())
//                .build();
//
//    }
//    public void updateAprilTags(){
//            detectedTags = aprilTag.getDetections();
//    }
//
//    public void autonomousPathUpdate() {
//
//        switch (pathState) {
//
//            case 0: //Get Apriltag
//                follower.followPath(getTag);
//                setPathState(-1);
//                break;
//
//            case 1:   //visionPortal.setProcessorEnabled(aprilTag, true);
//                if(!follower.isBusy()) {
//                    if (detectedTags != null) {
//                        updateAprilTags();
//                        detectedAprilTagId = detectedTags.get(0).id;
//                        if (pathTimer.getElapsedTimeSeconds() > 5) {
//                            follower.followPath(shootBalls, true);
//                            setPathState(2);
//                        }
//                    }
//                }
//                break;
//
//            case 2: //Shoot preload
//                 if(!follower.isBusy()) {
//                    //Shoot all three preload balls using the sequence we got from the Apriltag
//                     kh.flywheel(trueButton);
//                     switch(detectedAprilTagId){
//                         case 21: //shoot GPP
//                             kh.quickShot(trueButton, falseButton);
//                             kh.quickShot(falseButton, trueButton);
//                             kh.quickShot(falseButton, trueButton);
//                             kh.flywheel(falseButton);
//                             break;
//                         case 22: // Shoot PGP
//                             kh.quickShot(falseButton, trueButton);
//                             kh.quickShot(trueButton, falseButton);
//                             kh.quickShot(falseButton, trueButton);
//                             kh.flywheel(falseButton);
//                             break;
//                         case 23: // Shoot PPG
//                             kh.quickShot(falseButton, trueButton);
//                             kh.quickShot(falseButton, trueButton);
//                             kh.quickShot(trueButton,  falseButton);
//                             kh.flywheel(falseButton);
//                             break;
//                         default:
//                             break;
//                     }
//                    if(pathTimer.getElapsedTimeSeconds() > 5) {
//                        //Shoot balls
//                        follower.followPath(positionToGetFirstStrike, true);//Shoot preload
//                        setPathState(3);
//                    }
//                }
//                break;
//
//            case 3:
//                //Position to get first Strike
//                if(!follower.isBusy()) {
//                    //Start intake
//                    follower.followPath(positionToGetFirstStrike,true);
//                    setPathState(4);
//                }
//                break;
//
//            case 4:
//                //Intake fiist stike's balls
//                if(!follower.isBusy()) {
//                    kh.intake(trueButton, falseButton);
//                    follower.followPath(intakeFirstStrike, true);
//                    setPathState(5);
//                }
//                break;
//
//            case 5:
//                 if(!follower.isBusy()) {
//                     kh.intake(falseButton, trueButton);//Turn off intake
//                     kh.flywheel(trueButton);//Start shooter
//                    /* Go to shooting position */
//                    follower.followPath(shootStrikes,true);
//                    setPathState(6);
//                }
//                break;
//
//            case 6:
//                if(!follower.isBusy()) {
//                    /* Shoot balls */
//                    switch(detectedAprilTagId) {
//                        case 21: //shoot GPP
//                            kh.quickShot(trueButton, falseButton);
//                            kh.quickShot(falseButton, trueButton);
//                            kh.quickShot(falseButton, trueButton);
//                            kh.flywheel(falseButton);
//                            break;
//                        case 22: // Shoot PGP
//                            kh.quickShot(falseButton, trueButton);
//                            kh.quickShot(trueButton, falseButton);
//                            kh.quickShot(falseButton, trueButton);
//                            kh.flywheel(falseButton);
//                            break;
//                        case 23: // Shoot PPG
//                            kh.quickShot(falseButton, trueButton);
//                            kh.quickShot(falseButton, trueButton);
//                            kh.quickShot(trueButton, falseButton);
//                            kh.flywheel(falseButton);
//                            break;
//                        default:
//                            break;
//                    }
//                    if (pathTimer.getElapsedTimeSeconds() > 5) {
//                        setPathState(-1);
//                    }
//                }
//
//            case 7:
//                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the pickup3Pose's position */
//                if(!follower.isBusy()) {
//                    /* Grab Sample */
//
//                    /* Since this is a pathChain, we can have Pedro hold the end point while we are scoring the sample */
//                    follower.followPath(shootBalls, true);
//                    setPathState(8);
//                }
//                break;
//
//            case 8:
//                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
//                if(!follower.isBusy()) {
//                    /* Set the state to a Case we won't use or define, so it just stops running an new paths */
//                    setPathState(-1);
//                }
//                break;
//
//            default:
//                break;
//        }
//    }
//
//    /** These change the states of the paths and actions. It will also reset the timers of the individual switches **/
//    public void setPathState(int pState) {
//        pathState = pState;
//        pathTimer.resetTimer();
//    }
//
//
//    /** This is the main loop of the OpMode, it will run repeatedly after clicking "Play". **/
//    @Override
//    public void loop() {
//
//        // These loop the movements of the robot, these must be called continuously in order to work
//        follower.update();
//        autonomousPathUpdate();
//
//        // Feedback to Driver Hub for debugging
//        telemetry.addData("path state", pathState);
//        telemetry.addData("x", follower.getPose().getX());
//        telemetry.addData("y", follower.getPose().getY());
//        telemetry.addData("heading", follower.getPose().getHeading());
//
//        //April Tag info
//        detectedTags = aprilTag.getDetections();
//        if(visionPortal != null) {
//            for (AprilTagDetection detection : detectedTags) {
//                if (detection.metadata != null){
//                    telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
//                    telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
//                    telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
//                    telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
//                } else {
//                    telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
//                    telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
//                }
//            }   // end for() loop
//        }
//        telemetry.update();
//    }
//
//    /** This method is called once at the init of the OpMode. **/
//    @Override
//    public void init() {
//        pathTimer = new Timer();
//        opmodeTimer = new Timer();
//        opmodeTimer.resetTimer();
//        initAprilTag();
//
//        follower = Constants.createFollower(hardwareMap);
//        buildPaths();
//        follower.setStartingPose(startPose);
//        pathState =0;
//
//    }
//
//
//
//    /** This method is called continuously after Init while waiting for "play". **/
//    @Override
//    public void init_loop() {}
//
//    /** This method is called once at the start of the OpMode.
//     * It runs all the setup actions, including building paths and starting the path system **/
//    @Override
//    public void start() {
//        opmodeTimer.resetTimer();
//        setPathState(0);
//    }
//
//    /** We do not use this because everything should automatically disable **/
//    @Override
//    public void stop() {}
//
//    private void initAprilTag() {
//
//        // Create the AprilTag processor.
//        aprilTag = new AprilTagProcessor.Builder()
//
//                // The following default settings are available to un-comment and edit as needed.
//                //.setDrawAxes(false)
//                //.setDrawCubeProjection(false)
//                //.setDrawTagOutline(true)
//                //.setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
//                //.setTagLibrary(AprilTagGameDatabase.getCenterStageTagLibrary())
//                .setOutputUnits(DistanceUnit.INCH.INCH, AngleUnit.DEGREES.DEGREES)
//
//                // == CAMERA CALIBRATION ==
//                // If you do not manually specify calibration parameters, the SDK will attempt
//                // to load a predefined calibration for your camera.
//                //.setLensIntrinsics(578.272, 578.272, 402.145, 221.506)
//                // ... these parameters are fx, fy, cx, cy.
//
//                .build();
//
//        // Adjust Image Decimation to trade-off detection-range for detection-rate.
//        // eg: Some typical detection data using a Logitech C920 WebCam
//        // Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
//        // Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
//        // Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second (default)
//        // Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second (default)
//        // Note: Decimation can be changed on-the-fly to adapt during a match.
//        //aprilTag.setDecimation(3);
//
//        // Create the vision portal by using a builder.
//        VisionPortal.Builder builder = new VisionPortal.Builder();
//
//        // Set the camera (webcam vs. built-in RC phone camera).
//        if (USE_WEBCAM) {
//            builder.setCamera(hardwareMap.get(WebcamName.class, "webcam"));
//        } else {
//            builder.setCamera(BuiltinCameraDirection.BACK);
//        }
//
//        // Choose a camera resolution. Not all cameras support all resolutions.
//        builder.setCameraResolution(new Size(640, 480));
//
//        // Enable the RC preview (LiveView).  Set "false" to omit camera monitoring.
//        //builder.enableLiveView(true);
//
//        // Set the stream format; MJPEG uses less bandwidth than default YUY2.
//        //builder.setStreamFormat(VisionPortal.StreamFormat.YUY2);
//
//        // Choose whether or not LiveView stops if no processors are enabled.
//        // If set "true", monitor shows solid orange screen if no processors enabled.
//        // If set "false", monitor shows camera view without annotations.
//        //builder.setAutoStopLiveView(false);
//
//        // Set and enable the processor.
//        builder.addProcessor(aprilTag);
//
//        // Build the Vision Portal, using the above settings.
//        visionPortal = builder.build();
//
//        // Disable or re-enable the aprilTag processor at any time.
//        //visionPortal.setProcessorEnabled(aprilTag, true);
//
//    }   // end method initAprilTag()
//
//
//}