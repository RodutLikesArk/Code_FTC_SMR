
//initializarea librariilor pe care le foloseste acest cod
package org.firstinspires.ftc.teamcode;

import static androidx.core.math.MathUtils.clamp;

import androidx.core.math.MathUtils;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.hardware.AnalogInput;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

//definirea modului de operare a controlorului
@TeleOp(name="Basic: Iterative OpMode", group="Iterative OpMode")

public class BicaCode extends OpMode
{
    //initializare variabile
    GoBildaPinpointDriver odo;
    AnalogInput distanceSensor;
    Servo TSTServo;
    double voltage;
    double distancemm;
    double SVpos = 0.5;
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;
    double leftPower;
    double rightPower;
    double drive;
    double turn;

    //functie de initializare
    @Override
    public void init() {

        //definirea pieselor conectate cu numele corespunzatoare introduse in driver hub
        distanceSensor = hardwareMap.get(AnalogInput.class, "distSens");
        odo = hardwareMap.get(GoBildaPinpointDriver.class, "OdometryC");
        leftDrive  = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        TSTServo = hardwareMap.get(Servo.class, "S1");

        //initializarea calculatorului pentru odometrie
        odo.setOffsets(-84.0, -168.0, DistanceUnit.MM);
        odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);

        //adaugarea pozitiei de start a odometrului
        odo.resetPosAndIMU();
        Pose2D startingPosition = new Pose2D(DistanceUnit.MM, -923.925, 1601.47, AngleUnit.RADIANS, 0);
        odo.setPosition(startingPosition);

        //setarea directiei motoarelor
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        rightDrive.setDirection(DcMotor.Direction.FORWARD);

        //printarea de status pe driver hub
        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void start()
    {

    }

    //functie repetitiva
    @Override
    public void loop() {
        //calcularea distantei
        voltage = distanceSensor.getVoltage();
        distancemm = (voltage / 3.3) * 1000.0;

        //actualizarea calculatorului de odometrie
        odo.update();
        //actualizarea unei variabile de pozitie
        Pose2D pos = odo.getPosition();

        //calcularea puterii pe fiecare motor
        drive = -gamepad1.left_stick_y;
        turn  =  gamepad1.right_stick_x;
        leftPower = Range.clip(drive + turn, -1.0, 1.0) ;
        rightPower = Range.clip(drive - turn, -1.0, 1.0) ;

        //simplu test de servo. Foloseste D-pad stanga si dreapta pentru a controla servo-ul
        SVpos = SVpos - 0.005 * (gamepad1.dpad_left ? 1.0 : 0.0) + 0.005 * (gamepad1.dpad_right ? 1.0 : 0.0);
        SVpos = clamp(SVpos, 0.0, 1.0);
        TSTServo.setPosition(SVpos);

        //adaugarea acelei puteri
        leftDrive.setPower(leftPower);
        rightDrive.setPower(rightPower);

        //adaugarea in timp real a informatilor relevante pentru driver pe driver hub
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "left (%.2f), right (%.2f)", leftPower, rightPower);
        telemetry.addData("Version", odo.getDeviceVersion());
        telemetry.addData("X", pos.getX(DistanceUnit.MM));
        telemetry.addData("Y", pos.getY(DistanceUnit.MM));
        telemetry.addData("Dist", distancemm);
        updateTelemetry(telemetry);
    }
}
