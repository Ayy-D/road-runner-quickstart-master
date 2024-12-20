package org.firstinspires.ftc.teamcode;
import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorRangeSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.concurrent.TimeUnit;


@TeleOp(name="Nucifera")

public class nucifera extends LinearOpMode{
    String placeholder = "----";

    //pid stuffs

    public static int target = 0;


    //Directionally named motors based on view from opposite of back odo pod side

    //Control Hub Motors
    private DcMotorEx rightFront = null;
    private DcMotorEx leftFront = null;
    private DcMotorEx rightBack = null;
    private DcMotorEx leftBack= null;

    //Control Hub Servos
    private CRServo inR = null;
    private CRServo inL = null;
    private Servo inTwi = null;
    private Servo inUD = null; // SPM
    private Servo inArmR = null; // SPM
    private Servo inArmL = null; // SPM


    //Expansion Hub Motors
    private DcMotorEx rotR = null;
    private DcMotorEx rotL = null;
    private DcMotorEx sL = null;
    private DcMotorEx sR = null;

    //Expansion Hub Servos
    private Servo scR = null; //SPM
    private Servo scL = null; //SPM
    private Servo scUD = null; //SPM
    private Servo scC = null;

    //Sensors & Cameras
    private ColorSensor color = null;
    int red;
    int blue;
    int green;

    ElapsedTime timer = new ElapsedTime();


    @Override
    public void runOpMode() throws InterruptedException {

        //drivetrain power vars
        double  drive = 0;        // Desired forward power/speed (-1 to +1)
        double  strafe = 0;        // Desired strafe power/speed (-1 to +1)
        double  turn = 0;        // Desired turning power/speed (-1 to +1)

        //drivetrain motors
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFront");
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftFront = hardwareMap.get(DcMotorEx.class, "leftFront");
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftFront.setDirection(DcMotorEx.Direction.REVERSE);

        rightBack = hardwareMap.get(DcMotorEx.class, "rightBack");
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftBack = hardwareMap.get(DcMotorEx.class, "leftBack");
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setDirection(DcMotorEx.Direction.REVERSE);


        //Linear Slide Rotation Motors
        rotR = hardwareMap.get(DcMotorEx.class, "rotateR");
        //rotR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rotR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rotR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rotR.setDirection(DcMotor.Direction.REVERSE);


        rotL = hardwareMap.get(DcMotorEx.class, "rotateL");

        //rotL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rotL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rotL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        //LS Motors
        sL = hardwareMap.get(DcMotorEx.class, "slideL");
        sL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        sL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        sR = hardwareMap.get(DcMotorEx.class, "slideR");
        sR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        sR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        sR.setDirection(DcMotor.Direction.REVERSE);


        //Control Hub Servos (Intake)
        inR = hardwareMap.get(CRServo.class, "inRight");
        inL = hardwareMap.get(CRServo.class, "inLeft");
        inTwi = hardwareMap.get(Servo.class, "inTwist");
        inUD = hardwareMap.get(Servo.class, "inUD");
        inArmR = hardwareMap.get(Servo.class, "inArmR");
        inArmL = hardwareMap.get(Servo.class, "inArmL");

        //Sensors + Cameras
        color = hardwareMap.get(ColorSensor.class, "color");
        red = color.red();
        blue = color.blue();
        green = color.green();

        //Expansion Hub Servos
        scR = hardwareMap.get(Servo.class, "scArmR"); //0.95 goes toward intake, 0 goes outward from robot
        scL = hardwareMap.get(Servo.class, "scArmL"); //same as above
        scUD = hardwareMap.get(Servo.class, "scUD"); //1 is the position for depositing an element, 0.8 for intake, <0.8 to keep it up
        scUD.setDirection(Servo.Direction.REVERSE);
        scC = hardwareMap.get(Servo.class, "scClaw"); //0.27 close, 0.8 open

        //Sample-Specimen Cycle
        int cycleCase = 0; // 0 - Sample, 1 - Specimen

        //Intake/Scoring Trigger Cycle Variables
        int inCurrCase = 0;
        boolean inLastButtonStateR = false;
        boolean inLastButtonStateL = false;

        // Linear Slide Cycle Variables
        int scCurrCase = 0;
        boolean scLastButtonState = false;

        //Scoring Arm Counter - SAMPLE AUTOMATION ONLY
        int triangleCounter = 0;

        waitForStart();
        telemetry.addData(">>", "Press start to continue");
        telemetry.update();
        if (isStopRequested()) return;

        scC.setPosition(0.3);
        resetSlideEncoders();


        while (opModeIsActive())
        {

            drive  = gamepad1.left_stick_y  / 1.5;  // Reduce drive rate to 66%.
            strafe = gamepad1.left_stick_x  / 1;  // Reduce strafe rate to 100%.
            turn   = -gamepad1.right_stick_x / 2.5;  // Reduce turn rate to 40%.
            moveRobot(drive, strafe, turn);

            //scC.setPosition(0.27); //close
            // scC.setPosition(0.8); //open

            boolean xButtonState = gamepad2.cross; // linear slides++
            boolean rightBumperState = gamepad1.right_bumper; // intaking++
            boolean leftBumperState = gamepad1.left_bumper; // intaking--

            if(gamepad2.right_trigger > 0.75){

               cycleCase = 1;
               inCurrCase = 0;
               scCurrCase = 0;


            }
            if(gamepad2.left_trigger > 0.75){
                cycleCase = 0;
                inCurrCase = 0;
                scCurrCase = 0;
            }

            if(rightBumperState && !inLastButtonStateR){
                inCurrCase = (inCurrCase + 1) % 6;
            }
            inLastButtonStateR = rightBumperState;

            if(leftBumperState && !inLastButtonStateL){
                inCurrCase = (inCurrCase - 1 + 6) % 6;
            }
            inLastButtonStateL = leftBumperState;

            //Sample Automation
            if(cycleCase == 0){
                switch(inCurrCase){
                    case 0: // full collapsed
                        inArmR.setPosition(0.14);
                        inArmL.setPosition(0.14);
                        inUD.setPosition(0.3);
                        inTwi.setPosition(0.3);
                        inR.setPower(0);
                        inL.setPower(0);
                        break;

                    case 1: // intaking halfway
                        inArmR.setPosition(0.25);
                        inArmL.setPosition(0.25);
                        inUD.setPosition(0.65);
                        inTwi.setPosition(0.35);
                        inR.setPower(0);
                        inL.setPower(0);
                        break;


                    case 2: // full out - intaking
                        inArmR.setPosition(0.33);
                        inArmL.setPosition(0.3);
                        inUD.setPosition(0.88);
                        inTwi.setPosition(0.56);
                        inL.setPower(1);
                        //         //runIntake(red, blue, green);
                        break;

                    case 3: // scoring halfway
                        inArmR.setPosition(0.25);
                        inArmL.setPosition(0.25);
                        inUD.setPosition(0.35);
                        inTwi.setPosition(0.35);
                        inR.setPower(0);
                        inL.setPower(0);

                        scUD.setPosition(0.85);
                        break;

                    case 4:// transfer collapse
                        inArmR.setPosition(0.12);
                        inArmL.setPosition(0.12);
                        inUD.setPosition(0.2);
                        inTwi.setPosition(0.35);
                        inR.setPower(0);
                        inL.setPower(0);
                        break;

                    case 5: // transfer
                        inArmR.setPosition(0.12);
                        inArmL.setPosition(0.12);
                        inUD.setPosition(0.2);
                        inTwi.setPosition(0.4);
                        inR.setPower(0.4);
                        inL.setPower(-0.4);
                        break;

                }

                if(inCurrCase == 0){
                    if(gamepad1.triangle && scCurrCase != 0){
                        scR.setPosition(0.48);
                        scL.setPosition(0.48);
                        scUD.setPosition(0.92);
                        triangleCounter = 1;
                    }
                    if(xButtonState && !scLastButtonState && triangleCounter != 1){
                        scCurrCase = (scCurrCase + 1) % 3;

                    }
                    if(xButtonState && !scLastButtonState && triangleCounter == 1){
                        scCurrCase = 0;
                    }

                    scLastButtonState = xButtonState;

                    switch(scCurrCase){
                        case 0:
                            //resetSlideEncoders();

                            rotL.setPower(0.4);
                            rotL.setTargetPosition(205);
                            rotL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                            rotR.setPower(0.4);
                            rotR.setTargetPosition(205);
                            rotR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                            sL.setPower(0.9);
                            sL.setTargetPosition(15);
                            sL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                            sR.setPower(0.9);
                            sR.setTargetPosition(15);
                            sR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                            scR.setPosition(0.98);
                            scL.setPosition(0.98);
                            scUD.setPosition(0.95);
                            triangleCounter = 0;
                            break;

                        case 1:
                            sL.setPower(0.9);
                            sL.setTargetPosition(900);
                            sL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                            sR.setPower(0.9);
                            sR.setTargetPosition(900);
                            sR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                            break;

                        case 2:
                            sL.setPower(0.9);
                            sL.setTargetPosition(1820);
                            sL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                            sR.setPower(0.9);
                            sR.setTargetPosition(1820);
                            sR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                            break;
                    }

                }
            }

            //Specimen Automation
            if(cycleCase == 1){
                switch(inCurrCase){
                    case 0: // full collapsed - grab from wall
                        inArmR.setPosition(0.14);
                        inArmL.setPosition(0.14);
                        inUD.setPosition(0.3);
                        inTwi.setPosition(0.3);
                        inR.setPower(0);
                        inL.setPower(0);
                        break;

                    case 1: // intaking halfway
                        inArmR.setPosition(0.25);
                        inArmL.setPosition(0.25);
                        inUD.setPosition(0.65);
                        inTwi.setPosition(0.35);
                        inR.setPower(0);
                        inL.setPower(0);
                        break;


                    case 2: // full out - intaking
                        inArmR.setPosition(0.33);
                        inArmL.setPosition(0.3);
                        inUD.setPosition(0.88);
                        inTwi.setPosition(0.56);
                        inR.setPower(-1);
                        inL.setPower(1);
                        if(gamepad1.triangle){
                            inR.setPower(1);
                            inL.setPower(-1);
                        }
                        //         //runIntake(red, blue, green);
                        break;

                    case 3: // scoring halfway
                        inArmR.setPosition(0.25);
                        inArmL.setPosition(0.25);
                        inUD.setPosition(0.35);
                        inTwi.setPosition(0.35);
                        inR.setPower(0);
                        inL.setPower(0);

                        scUD.setPosition(0.85);
                        break;

                    case 4: // human player collapse
                        inArmR.setPosition(0.12);
                        inArmL.setPosition(0.12);
                        inUD.setPosition(0.35);
                        inTwi.setPosition(0.35);
                        inR.setPower(0);
                        inL.setPower(0);
                        break;

                    case 5: // out to hp
                        inArmR.setPosition(0.12);
                        inArmL.setPosition(0.12);
                        inUD.setPosition(0.35);
                        inTwi.setPosition(0.35);
                        inR.setPower(0.4);
                        inL.setPower(-0.4);
                        break;

                }

                if(inCurrCase == 0){

                    if(xButtonState && !scLastButtonState && triangleCounter != 1){
                        scCurrCase = (scCurrCase + 1) % 4;

                    }
                    if(xButtonState && !scLastButtonState && triangleCounter == 1){
                        scCurrCase = 0;
                        triangleCounter = 0;
                    }

                    scLastButtonState = xButtonState;

                    switch(scCurrCase){
                        case 0: // rest out of the way

                            rotL.setPower(0.4);
                            rotL.setTargetPosition(200);
                            rotL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                            rotR.setPower(0.4);
                            rotR.setTargetPosition(200);
                            rotR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                            sL.setPower(0.9);
                            sL.setTargetPosition(15);
                            sL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                            sR.setPower(0.9);
                            sR.setTargetPosition(15);
                            sR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                            scR.setPosition(0.4);
                            scL.setPosition(0.4);
                            scUD.setPosition(0.8);
                            break;

                        case 1: // grab from wall
                            rotL.setPower(0.4);
                            rotL.setTargetPosition(208);
                            rotL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                            rotR.setPower(0.4);
                            rotR.setTargetPosition(208);
                            rotR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                            sL.setPower(0.2);
                            sL.setTargetPosition(15);
                            sL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                            sR.setPower(0.2);
                            sR.setTargetPosition(15);
                            sR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                            scR.setPosition(0.12);
                            scL.setPosition(0.12);
                            scUD.setPosition(1);
                            scC.setPosition(0.8);
                            break;

                        case 2: // out to score
                            timer.reset();
                            scC.setPosition(0.25);
                            scCurrCase = 3;
                            break;

                        case 3: // wait for delay
                            if (timer.milliseconds() > 500 && triangleCounter != 1) {
                                // Execute the motor commands
                                rotL.setPower(0.4);
                                rotL.setTargetPosition(40);
                                rotL.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                                rotR.setPower(0.4);
                                rotR.setTargetPosition(40);
                                rotR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                                sL.setPower(0.9);
                                sL.setTargetPosition(675);
                                sL.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                                sR.setPower(0.9);
                                sR.setTargetPosition(675);
                                sR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                                scR.setPosition(0.5);
                                scL.setPosition(0.5);
                            }

                            if (timer.milliseconds() > 500 && triangleCounter == 1) {
                                // Execute the motor commands
                                scC.setPosition(0.8);

                                rotL.setPower(0.4);
                                rotL.setTargetPosition(100);
                                rotL.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                                rotR.setPower(0.4);
                                rotR.setTargetPosition(100);
                                rotR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                                sL.setPower(0.3);
                                sL.setTargetPosition(200);
                                sL.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                                sR.setPower(0.3);
                                sR.setTargetPosition(200);
                                sR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                            }

                            if(gamepad2.triangle){ //push to clip
                                timer.reset();

                                sL.setPower(0.9);
                                sL.setTargetPosition(950);
                                sL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                                sR.setPower(0.9);
                                sR.setTargetPosition(950);
                                sR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                                //scUD.setPosition(0.8);

                                triangleCounter = 1;
                            }

                            break;
                    }

                }
            }


            telemetry.addData("rotR Position", rotR.getCurrentPosition());
            telemetry.addData("rotR Current Draw", rotR.getCurrent(CurrentUnit.AMPS));
            telemetry.addData("rotL Position", rotL.getCurrentPosition());
            telemetry.addData("rotL Current Draw", rotL.getCurrent(CurrentUnit.AMPS));


            telemetry.addData("-----", placeholder);

            telemetry.addData("sR Position", sR.getCurrentPosition());
            telemetry.addData("sR Current Draw", sR.getCurrent(CurrentUnit.AMPS));
            telemetry.addData("sL Position", sL.getCurrentPosition());
            telemetry.addData("sL Current Draw", sL.getCurrent(CurrentUnit.AMPS));

            telemetry.addData("-----", placeholder);

            if(cycleCase == 0){
                telemetry.addData("Cycle Case", "***SAMPLE***");
            }
            if(cycleCase == 1){
                telemetry.addData("Cycle Case", "^^^SPECIMEN^^^");
            }
            telemetry.update();

            /*
            keep in case of motor change, tick test
            if(gamepad1.dpad_up){
                sL.setPower(0.5);
                sL.setTargetPosition(sL.getCurrentPosition()+1000);
                sL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sR.setPower(0.5);
                sR.setTargetPosition(sR.getCurrentPosition()+1000);
                sR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
            if(gamepad1.dpad_down){
                sL.setPower(0.5);
                sL.setTargetPosition(sL.getCurrentPosition()-1000);
                sL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sR.setPower(0.5);
                sR.setTargetPosition(sR.getCurrentPosition()-1000);
                sR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
            if(gamepad1.dpad_left){
                sL.setPower(0.5);
                sL.setTargetPosition(sL.getCurrentPosition()-100);
                sL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sR.setPower(0.5);
                sR.setTargetPosition(sR.getCurrentPosition()-100);
                sR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
            if(gamepad1.dpad_right){
                sL.setPower(0.5);
                sL.setTargetPosition(sL.getCurrentPosition()+100);
                sL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sR.setPower(0.5);
                sR.setTargetPosition(sR.getCurrentPosition()+100);
                sR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
            */

        }
    }
    public void resetSlideEncoders() {
        telemetry.addData("Status", "Resetting encoders...");
        telemetry.update();

        sL.setTargetPosition(0);
        sR.setTargetPosition(0);

        sL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        sR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        sL.setPower(0.5);
        sR.setPower(0.5);

        long startTime = System.currentTimeMillis();
        boolean stalled = false;

        while (opModeIsActive() && !stalled) {
            if (!stalled && Math.abs(sL.getCurrentPosition() - sL.getTargetPosition()) < 5) {
                if (System.currentTimeMillis() - startTime > 100) {

                    sL.setPower(0);
                    sL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    sR.setPower(0);
                    sR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    telemetry.addData("Slide 1 Stalled", sL.getCurrentPosition());
                    telemetry.addData("Slide 2 Stalled", sR.getCurrentPosition());
                    break;
                }
            }



            telemetry.update();
        }

        // Stop motors after reset
        sL.setPower(0);
        sR.setPower(0);

        sL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        sR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }

//    public void resetArmEncoders() {
//        telemetry.addData("Status", "Resetting encoders...");
//        telemetry.update();
//
//        rotL.setTargetPosition(0);
//        rotR.setTargetPosition(0);
//
//        rotL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        rotR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//
//        rotL.setPower(0.5);
//        rotR.setPower(0.5);
//
//        long startTime = System.currentTimeMillis();
//        boolean stalled = false;
//
//        while (opModeIsActive() && !stalled) {
//            if (!stalled && Math.abs(rotL.getCurrentPosition() - rotL.getTargetPosition()) < 5) {
//                if (System.currentTimeMillis() - startTime > 100) {
//
//                    rotR.setPower(0);
//                    rotR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//                    rotL.setPower(0);
//                    rotL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//                    telemetry.addData("Arm Right Stalled", rotR.getCurrentPosition());
//                    telemetry.addData("Arm Left Stalled", rotL.getCurrentPosition());
//                    break;
//                }
//            }
//
//
//
//            telemetry.update();
//        }
//
//        // Stop motors after reset
//        rotL.setPower(0);
//        rotR.setPower(0);
//
//        rotL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        rotR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//
//        telemetry.addData("Status", "Arm Encoders reset complete");
//        telemetry.update();
//    }
    public void runIntake(int red, int blue, int green){
        telemetry.addData("Intake ON!", placeholder);
        telemetry.update();
        //Intake wheels
        // if(gamepad1.triangle || gamepad1.y){ //intaking
        //     inR.setPower(-1);
        //     inL.setPower(1);
        // }
        // if(gamepad1.circle || gamepad1.b){ //depositing
        //     inR.setPower(0.1);
        //     inL.setPower(-0.1);
        // }
        // if(gamepad1.cross ||gamepad1.a){ //rest

        // }
    }

    public void moveRobot(double x, double y, double yaw) {
        // Calculate wheel powers.
        double leftFrontPower =  x -y -yaw;
        double rightFrontPower =  x +y +yaw;
        double leftBackPower =  x +y -yaw;
        double rightBackPower =  x -y +yaw;

        // Normalize wheel powers to be less than 1.0
        double max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        // Send powers to the wheels.

        leftFront.setPower(leftFrontPower);
        rightFront.setPower(rightFrontPower);
        leftBack.setPower(leftBackPower);
        rightBack.setPower(rightBackPower);

    }
}
