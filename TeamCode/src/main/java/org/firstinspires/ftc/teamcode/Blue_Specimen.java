package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

// RR-specific imports
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Trajectory;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;

// Non-RR imports
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.MecanumDrive;
@Config
@Autonomous(name = "Blue Specimen", group = "Autonomous")
public class Blue_Specimen extends LinearOpMode {
    ElapsedTime time = new ElapsedTime();


    //Intake components


    //Linear Slide components
    public class Lift {
        private DcMotorEx sL;
        private DcMotorEx sR;

        public Lift(HardwareMap hardwareMap) {

            sL = hardwareMap.get(DcMotorEx.class, "slideL");
            sR = hardwareMap.get(DcMotorEx.class, "slideR");

            //RUN Encoders
            sL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            sR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            //STOP reset
            sL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            sR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            //BRAKE Behavior
            sR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            sL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            //Set Direction
            sL.setDirection(DcMotorSimple.Direction.FORWARD);
            sR.setDirection(DcMotorSimple.Direction.REVERSE);

            sL.setTargetPosition(0);
            sR.setTargetPosition(0);

        }

        public class LiftAlignHighRung implements Action {

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {

                sL.setPower(0.8);
                sR.setPower(0.8);
                sL.setTargetPosition(500);
                sR.setTargetPosition(500);
                sL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                return false;
            }
        }
        public Action liftAlignHighRung() {
            return new LiftAlignHighRung();
        }
        public class LiftScoreSpecimenHighRung implements Action {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                sL.setPower(0.8);
                sR.setPower(0.8);
                sL.setTargetPosition(845);
                sR.setTargetPosition(845);
                sL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                return false;
            }
        }
        public Action liftScoreSpecimenHighRung() {
            return new LiftScoreSpecimenHighRung();
        }
    }

    public class LiftRotate {
        private DcMotorEx rotR;
        private DcMotorEx rotL;

        public LiftRotate(HardwareMap hardwareMap) {
            rotR = hardwareMap.get(DcMotorEx.class, "rotateR");
            rotL = hardwareMap.get(DcMotorEx.class, "rotateL");

            //RUN Encoders
            rotR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rotL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            //BRAKE Behavior
            rotR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            rotL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            //Set Direction
            rotR.setDirection(DcMotorSimple.Direction.REVERSE);
            rotL.setDirection(DcMotorSimple.Direction.FORWARD);
        }

        public class LiftRotateSpecimenPickup implements Action {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    rotR.setPower(0.4);
                    rotL.setPower(0.4);
                    initialized = true;
                }

                double posL = rotR.getCurrentPosition();
                double posR = rotL.getCurrentPosition();
                packet.put("Lift Rot L", posL);
                packet.put("lift Rot R", posR);

                // Check the motor
                if (posL < 205 || posR < 205) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        public Action liftRotateSpecimenPickup() {
            return new LiftRotateSpecimenPickup();
        }

        public class LiftRotateSpecimenScore implements Action {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    rotR.setPower(-0.4);
                    rotL.setPower(-0.4);
                    initialized = true;
                }

                double posL = rotR.getCurrentPosition();
                double posR = rotL.getCurrentPosition();
                packet.put("Lift L", posL);
                packet.put("lift R", posR);

                // Check the left motor
                if (posL > 2 || posR > 2) {
                    return true;
                } else {
                    rotL.setPower(0);
                    return false;
                }
            }
        }


        public Action liftRotateSpecimenScore() {
            return new LiftRotateSpecimenScore();
        }
    }

    public class Claw {
        private Servo scC;
        public Claw(HardwareMap hardwareMap) {
            scC = hardwareMap.get(Servo.class, "scClaw"); //0.27 close | 0.8 open
        }

        public class CloseClaw implements Action {
            @Override
            public boolean run (@NonNull TelemetryPacket packet) {
                scC.setPosition(0.24);
                return false;
            }
        }
        public Action closeClaw() {
            return new CloseClaw();
        }

        public class OpenClaw implements Action {
            @Override
            public boolean run (@NonNull TelemetryPacket packet) {
                scC.setPosition(0.8);
                return false;
            }
        }
        public Action openClaw() {
            return new OpenClaw();
        }

    }

    public class Scoring {
        private Servo scL;
        private Servo scR;
        private Servo scUD;
        private Servo scC;
        private DcMotorEx sL;
        private DcMotorEx sR;
        private DcMotorEx rotR;
        private DcMotorEx rotL;
        public Scoring(HardwareMap hardwareMap) {
            scL = hardwareMap.get(Servo.class, "scArmL"); //0.95 goes toward intake, 0 goes outward from robot
            scR = hardwareMap.get(Servo.class, "scArmR"); //0.95 goes toward intake, 0 goes outward from robot
            scUD = hardwareMap.get(Servo.class, "scUD"); //1 is the position for depositing an element, 0.8 for intake, <0.8 to keep it up
            scUD.setDirection(Servo.Direction.REVERSE);
            scC = hardwareMap.get(Servo.class, "scClaw"); //0.27 close | 0.8 open

            sL = hardwareMap.get(DcMotorEx.class, "slideL");
            sR = hardwareMap.get(DcMotorEx.class, "slideR");

            rotR = hardwareMap.get(DcMotorEx.class, "rotateR");
            rotL = hardwareMap.get(DcMotorEx.class, "rotateL");

            //RUN Encoders
            rotR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rotL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            sL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            sR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            //BRAKE Behavior
            rotR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            rotL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            //Set Direction
            rotR.setDirection(DcMotorSimple.Direction.REVERSE);
            rotL.setDirection(DcMotorSimple.Direction.FORWARD);

            //RUN Encoders

            sL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            sR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            rotL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            rotR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            //BRAKE Behavior
            sR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            sL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            //Set Direction
            sL.setDirection(DcMotorSimple.Direction.FORWARD);
            sR.setDirection(DcMotorSimple.Direction.REVERSE);


        }

        public class ScoringArmIntake implements Action {
            @Override
            public boolean run (@NonNull TelemetryPacket packet) {

                scL.setPosition(0.12); //verify
                scR.setPosition(0.12);
                scUD.setPosition(1); //verify
                scC.setPosition(0.8);

                sL.setPower(0.2);
                sR.setPower(0.2);
                sL.setTargetPosition(15);
                sR.setTargetPosition(15);
                sL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                rotL.setPower(0.4);
                rotR.setPower(0.4);
                rotL.setTargetPosition(208);
                rotR.setTargetPosition(208);
                rotL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rotR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                return false;
            }
        }
        public Action scoringArmIntake() {
            return new ScoringArmIntake();
        }

        public class ScoringArmScore implements Action {
            @Override
            public boolean run (@NonNull TelemetryPacket packet) {

                scL.setPosition(0.6); //verify
                scR.setPosition(0.6);
                scUD.setPosition(1); //verify
                scC.setPosition(0.25);

                sL.setPower(0.8);
                sR.setPower(0.8);
                sL.setTargetPosition(845);
                sR.setTargetPosition(845);
                sL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                while (sL.isBusy() && sR.isBusy()) {
                    telemetry.addData("Slide L", sL.getCurrentPosition());
                    telemetry.addData("Slide R", sR.getCurrentPosition());
                }

                rotL.setPower(0.4);
                rotR.setPower(0.4);
                rotL.setTargetPosition(50);
                rotR.setTargetPosition(50);
                rotL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rotR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                return false;
            }
        }

        public Action scoringArmScore() {
            return new ScoringArmScore();
        }
    }


    public class IntakeHold {
        private CRServo inR;
        private CRServo inL;
        private Servo inUD;
        private Servo inArmR;
        private Servo inArmL;
        private Servo inTwist;

        public IntakeHold(HardwareMap hardwareMap) {
            inR = hardwareMap.get(CRServo.class, "inRight");
            inL = hardwareMap.get(CRServo.class, "inLeft");
            inUD = hardwareMap.get(Servo.class, "inUD");
            inArmL = hardwareMap.get(Servo.class, "inArmL");
            inArmR = hardwareMap.get(Servo.class, "inArmR");
            inTwist = hardwareMap.get(Servo.class, "inTwist");

        }

        public class IntakeHoldBase implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                inR.setPower(0);
                inL.setPower(0);
                inUD.setPosition(0.3);
                inArmL.setPosition(0.14);
                inArmR.setPosition(0.14);
                inTwist.setPosition(0.3);
                return false;
            }
        }
        public Action intakeHoldBase() {
            return new IntakeHoldBase();
        }
    }


    @Override
    public void runOpMode() {
        Pose2d initialPose = new Pose2d(0, 59, Math.toRadians(90));
        Pose2d scoringPose = new Pose2d(0,34.5, Math.toRadians(90));
        Pose2d intakePose = new Pose2d(-48, 46, Math.toRadians(90));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);
        Claw claw = new Claw(hardwareMap);
        IntakeHold intakeHold = new IntakeHold(hardwareMap);
        Scoring scoring = new Scoring(hardwareMap);
        LiftRotate liftRotate = new LiftRotate(hardwareMap);
        Lift lift = new Lift(hardwareMap);




        TrajectoryActionBuilder tab1 = drive.actionBuilder(initialPose)
                .lineToYConstantHeading(34.5)
                .waitSeconds(0.5);

        // Build the trajectory
//
        TrajectoryActionBuilder tab2 = drive.actionBuilder(scoringPose)
                .splineToConstantHeading(new Vector2d(-37.3, 41.7), Math.toRadians(270))
                .strafeTo(new Vector2d(-47, 6.5))
                .strafeTo(new Vector2d(-57, 6.5))
                .strafeTo(new Vector2d(-57, 46))
                .splineToConstantHeading(new Vector2d(-71, 6.5), Math.toRadians(180))
                .strafeTo(new Vector2d(-71, 46))
                .splineToConstantHeading(new Vector2d(-85, 6.5), Math.toRadians(180))
                .strafeTo(new Vector2d(-85, 46))
                .splineToConstantHeading(new Vector2d(-48, 46), Math.toRadians(90))
                .waitSeconds(0.8);

        TrajectoryActionBuilder tab3 = drive.actionBuilder(intakePose)
                .splineToConstantHeading(new Vector2d(0, 34.5), Math.toRadians(180))
                .waitSeconds(2);


        TrajectoryActionBuilder tab4 = drive.actionBuilder(scoringPose)
                .splineToConstantHeading(new Vector2d(-48, 46), Math.toRadians(180))
                .waitSeconds(1.2);


//
        Action TrajectoryActionCloseOut = tab1.endTrajectory().fresh()
                .strafeTo(new Vector2d(-41.9, 52))
                .build();



        Actions.runBlocking(claw.closeClaw());
        Actions.runBlocking(intakeHold.intakeHoldBase());
        /***
         while (!isStopRequested() && !opModeIsActive()) {
         telemetry.update();
         }
         ***/
        // Wait for the start signal
        waitForStart();
        if (isStopRequested()) return;


        Action initToScoreTrajectory;
        Action scoreToPushTrajectory;
        Action pickUpToScoreTrajectory;
        Action scoreToPickUpTrajectory;

        initToScoreTrajectory = tab1.build();
        scoreToPushTrajectory = tab2.build();
        pickUpToScoreTrajectory = tab3.build();
        scoreToPickUpTrajectory = tab4.build();

        Actions.runBlocking(
                new SequentialAction(
                        //scoring.scoringArmScore(),
                        initToScoreTrajectory,
                        claw.openClaw(),
                        scoreToPushTrajectory,
                        scoring.scoringArmIntake(),
                        claw.closeClaw(),
                        pickUpToScoreTrajectory,
                        //scoring.scoringArmScore(),
                        scoreToPickUpTrajectory,
                        scoring.scoringArmIntake(),
                        pickUpToScoreTrajectory,
                        claw.closeClaw(),
                        //scoring.scoringArmScore(),
                        scoreToPickUpTrajectory,
                        scoring.scoringArmIntake(),
                        claw.closeClaw(),
                        pickUpToScoreTrajectory,
                        //scoring.scoringArmScore(),
                        TrajectoryActionCloseOut



                )
        );


    }
}
