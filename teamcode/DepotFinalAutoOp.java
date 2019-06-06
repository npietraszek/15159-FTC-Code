/*
DRIVE SPEED IS A MEASURE OF HOW FAST THE ROBOT GOES
INCHES IS HOW FAR THE WHEELS MOVE IN ENCODER DRIVE
THE TIMEOUT STOPS THE ROBOT AT THAT TIME IF IT DOESN'T REACH THE DISTANCE IN THE ALLOWED TIME
Can turn robot by setting motor power of one motor or two
Can also turn robot by using encoder drive with same signs on left and right values
Can go forwards or backwards by setting motors to opposite powers or encoder drive
The monster wheel diameter is 5 inches.
A variable "Selection Direction" is used to determine where the gold mineral is before autonomous
actually starts when the robot is still latched.
The phone camera will detect and determine which direction to move during initialization.
The robot will then unlatch, sample the gold, place the marker inside the depot, and then park
next to the crater.
There are some reused functions at the bottom of the code to standardize the code
 */
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;


/**
 * This 2018-2019 OpMode illustrates the basics of using the TensorFlow Object Detection API to
 * determine the position of the gold and silver minerals.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list.
 *
 * IMPORTANT: In order to use this OpMode, you need to obtain your own Vuforia license key as
 * is explained below.
 */



@Autonomous
public class DepotFinalAutoOp extends LinearOpMode {
    //public static class thread extends Thread{

    //}
    // Declare OpMode members.
    private DcMotor motorLeft1;
    private DcMotor motorRight1;
    private DcMotor latch;
    private DcMotor sweeper;
    //    private CRServo wrist;
//    private CRServo fingers;
//    private DcMotor elbow;
//    private DcMotor extension;
    private ElapsedTime runtime = new ElapsedTime();
    static final double COUNTS_PER_MOTOR_REV = 1440;    // eg: TETRIX Motor Encoder
    static final double DRIVE_GEAR_REDUCTION = 2.0;     // This is < 1.0 if geared UP
    static final double WHEEL_DIAMETER_INCHES = 5.0;     // For figuring circumference.
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double DRIVE_SPEED = 0.4;
    static final double TURN_SPEED = 0.5;
    static final double ARM_SPEED = 1.0;
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";
    private TFObjectDetector tfod;
    private VuforiaLocalizer vuforia;
    final int pauseMS = 1000;
    final int movingPauseMS = 300;
    final double turningConstant = 0.5;
    private static final String VUFORIA_KEY = "AQqx7Hv/////AAABmeHmysAgekSTg8A/T+sJO28b/TqN3z6xf6Dx" +
            "rsqx/fV0NyN91JIaf1/r4s9SAhIFBarof770EinO7ACBxPgaRv8luUQcQWyyh1UUaC0EDVzgCDbZDLPAoj7+gch" +
            "3RnNLYfRWt/mcyVs2oKHF/5hArN1Rk7ubxrjnUSOCnOLd7ncXo5W+O9XGnaVgEhD+lmuR0jjWFcSuRgYOuA7X6" +
            "dXaeVdzumrQu7AH42+VtN4TIuSsEfTvH/wGKcGGehiX5MvWIvm7P+uMj256blkXPJLol4hksCfybiJpN2Zr7h4" +
            "q0CALaHsdaBD5cSJBAadYdQuh6KxhXKQGwFybQty8ee0on4Dfmt6hq2i/aCXIGslJqol+";
    private static int SelectionDirection = 2;
    @Override
    public void runOpMode() {

        // Initialize the drive system variables.
        latch = hardwareMap.get(DcMotor.class, "latch");
        sweeper = hardwareMap.get(DcMotor.class, "sweeper");
        motorLeft1 = hardwareMap.get(DcMotor.class, "motorLeft1");
        motorRight1 = hardwareMap.get(DcMotor.class, "motorRight1");
//        elbow=hardwareMap.get(DcMotor.class, "elbow");
//        extension = hardwareMap.get(DcMotor.class, "extension");
//        wrist = hardwareMap.get(CRServo.class, "wrist");
//        fingers = hardwareMap.get(CRServo.class, "fingers");
        // Send telemetry message to signify robot waiting.
        telemetry.addData("Status", "Resetting Encoders");
        telemetry.update();

        motorLeft1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRight1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        latch.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLeft1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRight1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        latch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Send telemetry message to indicate successful Encoder reset.
        telemetry.addData("Path0", "Starting at %7d :%7d",
                motorLeft1.getCurrentPosition(),
                motorRight1.getCurrentPosition(),
                latch.getCurrentPosition());
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        /*
         * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
         * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
         * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
         * web site at https://developer.vuforia.com/license-manager.
         *
         * Vuforia license keys are always 380 characters long, and look as if they contain mostly
         * random data. As an example, here is a example of a fragment of a valid key:
         *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
         * Once you've obtained a license key, copy the string from the Vuforia web site
         * and paste it in to your code on the next line, between the double quotes.
         */


        /**
         * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
         * localization engine.
         */


        /**
         * {@link #tfod} is the variable we will use to store our instance of the Tensor Flow Object
         * Detection engine.
         */


        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.

        initVuforia();
        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }
        telemetry.addData("Status:", "Initializing...");
        telemetry.update();
        sleep(pauseMS);

        /** Wait for the game to begin */
        //telemetry.addData(">", "Press Play to start tracking");
        //telemetry.update();


        /** Activate Tensor Flow Object Detection. */

        if (tfod != null) {
            tfod.activate();
            telemetry.addData("Status:", "Tensor Flow Activated...");
            telemetry.update();
            sleep(pauseMS);
        }


        if (tfod != null) {
            telemetry.addData("Status:", "Detecting objects...");
            telemetry.update();
            sleep(pauseMS);
            int objectsDetected = 0;
            telemetry.addData("ObjectsDetected:", objectsDetected);
            telemetry.update();
            sleep(pauseMS);
            telemetry.addData("opMode:", opModeIsActive());
            telemetry.update();
            sleep(pauseMS);
            while (!opModeIsActive()) {
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                // getUpdatedRecognitions() will return null if no new information is available since
                // the last time that call was made.

                if (updatedRecognitions != null) {
                    objectsDetected = updatedRecognitions.size();
                    telemetry.addData("inside loop Object Detected", objectsDetected);
                    telemetry.update();
                    sleep(pauseMS);
                    if (updatedRecognitions.size() == 3) {
                        int goldMineralX = -1;
                        int silverMineral1X = -1;
                        int silverMineral2X = -1;
                        for (Recognition recognition : updatedRecognitions) {
                            if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                goldMineralX = (int) recognition.getLeft();
                            } else if (silverMineral1X == -1) {
                                silverMineral1X = (int) recognition.getLeft();
                            } else {
                                silverMineral2X = (int) recognition.getLeft();
                            }
                        }
                        if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
                            if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {
                                telemetry.addData("Gold Mineral Position", "Left");
                                SelectionDirection = 0;
                                telemetry.update();
                                sleep(pauseMS);

                            } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
                                telemetry.addData("Gold Mineral Position", "Right");
                                SelectionDirection = 1;
                                telemetry.update();
                                sleep(pauseMS);

                            } else {
                                telemetry.addData("Gold Mineral Position", "Center");
                                SelectionDirection = 2;
                                telemetry.update();
                                sleep(pauseMS);


                            }

                        }

                    }

                }

            }
            telemetry.addData("Status:", "Waiting for start...");
            telemetry.update();

            //waitForStart();
            if (!opModeIsActive()) return;
            unlatch();
            //lock the sweeper
            sweeper.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            sweeper.setTargetPosition(sweeper.getCurrentPosition());
            sweeper.setPower(1.0);
            //end lock the sweeper
            sleep(movingPauseMS);
            if (SelectionDirection == 0) {
                if (!opModeIsActive()) return;
                //Left mineral crater.
                hitLeft();

            } else if (SelectionDirection == 1) {
                if (!opModeIsActive()) return;
                //Right mineral crater
                hitRight();
            } else {
                if (!opModeIsActive()) return;
                //Center mineral crater
                hitCenter();
            }
            sleep(movingPauseMS);
            if (!opModeIsActive()) return;
            driveFromCratertoDepot();
            sleep(movingPauseMS);
            if (!opModeIsActive()) return;
            if (SelectionDirection == 0) {
                if (!opModeIsActive()) return;
                //Left mineral crater.
                LeftDriveToCrater();

            } else if (SelectionDirection == 1) {
                if (!opModeIsActive()) return;
                //Right mineral crater
                RightDriveToCrater();
            } else {
                if (!opModeIsActive()) return;
                //Center mineral crater
                CenterDriveToCrater();
            }
            if (!opModeIsActive()) return;
        }

    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
    }

    /**
     * Initialize the Tensor Flow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
        tfodParameters.minimumConfidence = 0.125;
    }


    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS) {
        int newLeftTarget;
        int newRightTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = motorLeft1.getCurrentPosition() + (int) (leftInches * COUNTS_PER_INCH);
            newRightTarget = motorRight1.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);
            motorLeft1.setTargetPosition(newLeftTarget);
            motorRight1.setTargetPosition(newRightTarget);

            // Turn On RUN_TO_POSITION
            motorLeft1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motorRight1.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            motorLeft1.setPower(Math.abs(speed));
            motorRight1.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (motorLeft1.isBusy() && motorRight1.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1", "Running to %7d :%7d", newLeftTarget, newRightTarget);
                telemetry.addData("Path2", "Running at %7d :%7d",
                        motorLeft1.getCurrentPosition(),
                        motorRight1.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            motorLeft1.setPower(0);
            motorRight1.setPower(0);

            // Turn off RUN_TO_POSITION
            motorLeft1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motorRight1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        }
    }
    public void driveFromCratertoDepot() {
        //sweeper Dropoff

        if (!opModeIsActive()) return;
        runtime.reset();
        //unlock the sweeper
        sweeper.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //end unlock the sweeper
        sweeper.setPower(-0.9);
        while (opModeIsActive() && !isStopRequested() && runtime.seconds() < 0.1) {
        }
        if (!opModeIsActive()) return;
        runtime.reset();
        sweeper.setPower(0);
        while (opModeIsActive() && !isStopRequested() && runtime.seconds() < 0.5) {
        }
        runtime.reset();
        sweeper.setPower(0.9);
        while (opModeIsActive() && !isStopRequested() && runtime.seconds() < 0.1) {
        }
        sweeper.setPower(0);
        encoderDrive(DRIVE_SPEED, -0.5, 0.5, 20.0);
    }
    public void LeftDriveToCrater() {
        encoderDrive(DRIVE_SPEED*turningConstant, 6, 6, 20.0);
        encoderDrive(DRIVE_SPEED, 6.75, -6.75, 20.0);
        encoderDrive(DRIVE_SPEED*turningConstant, 1.25, 1.25, 20.0);
        encoderDrive(DRIVE_SPEED/2, 11.75, -11.75, 20.0);
        encoderDrive(DRIVE_SPEED/4, 3, -3.75, 20.0);
        telemetry.addData("Path", "Complete");
        telemetry.update();
    }
    public void CenterDriveToCrater() {
        encoderDrive(DRIVE_SPEED*turningConstant, 4.67, 4.67, 20.0);
        encoderDrive(DRIVE_SPEED, 3.75, -3.75, 20.0);
        encoderDrive(DRIVE_SPEED*turningConstant, 1.0, 1.0, 20.0);
        encoderDrive(DRIVE_SPEED, 7.75, -7.75, 20.0);
        encoderDrive(DRIVE_SPEED*turningConstant, 0.66, 0.66, 20.0);
        encoderDrive(DRIVE_SPEED/2, 10.75, -10.75, 20.0);
        telemetry.addData("Path", "Complete");
        telemetry.update();
    }
    public void RightDriveToCrater() {
        encoderDrive(DRIVE_SPEED*turningConstant, 3.33, 3.33, 20.0);
        encoderDrive(DRIVE_SPEED, 3.75, -3.75, 20.0);
        encoderDrive(DRIVE_SPEED*turningConstant, 1.25, 1.25, 20.0);
        encoderDrive(DRIVE_SPEED/2, 20.00, -20.00, 20.0);
        telemetry.addData("Path", "Complete");
        telemetry.update();
    }
    public void unlatch() {
        // Unlatch. Currently commented out so testing can continue without it.

        runtime.reset();
        if(!opModeIsActive()) return;
        latch.setPower(-1);
        while (opModeIsActive() && runtime.seconds() < 5.0) {
        }
        latch.setPower(0);
        if (!opModeIsActive()) return;
        runtime.reset();

        encoderDrive(DRIVE_SPEED/2, 3.5, -3.5, 5.0);
    }
    public void hitCenter(){
        if (!opModeIsActive()) return;
        // First forward move to move towards the minerals.

        encoderDrive(DRIVE_SPEED, 15.5, -15.5, 5.0);

        // Move back after nudging the mineral

        if (!opModeIsActive()) return;

    }
    public void hitLeft(){
        if (!opModeIsActive()) return;
        //Turn left to point towards mineral
        encoderDrive(DRIVE_SPEED*turningConstant, 1.25, 1.25, 5.0);
        // First forward move to move towards the minerals.

        encoderDrive(DRIVE_SPEED/2, 9, -9, 5.0);

        // Move back after nudging the mineral

        if (!opModeIsActive()) return;
        encoderDrive(DRIVE_SPEED, -2.5, -2.5, 8.0);
        //Moving at right angle
        encoderDrive(DRIVE_SPEED, 6, -6, 8.0);
    }
    public void hitRight(){
        if (!opModeIsActive()) return;
        //Turn right to point towards mineral
        encoderDrive(DRIVE_SPEED*turningConstant, -1.25, -1.25, 5.0);
        // First forward move to move towards the minerals.

        encoderDrive(DRIVE_SPEED/2, 10.5, -10.5, 10.0);

        // Move back after nudging the mineral
        if (!opModeIsActive()) return;
        encoderDrive(DRIVE_SPEED*turningConstant, 2.5, 2.5, 5.0);
        encoderDrive(DRIVE_SPEED/2, 9, -9, 10.0);

    }
}
