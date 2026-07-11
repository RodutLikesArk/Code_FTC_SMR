
//initializarea librariilor pe care le foloseste acest cod
package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

//definirea modului de operare a controlorului
@TeleOp(name="Basic: Iterative OpMode", group="Iterative OpMode")

public class BicaCode extends OpMode
{
    //initializare variabile
    GoBildaPinpointDriver odo;
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
        odo = hardwareMap.get(GoBildaPinpointDriver.class, "OdometryC");
        leftDrive  = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");

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
        //actualizarea calculatorului de odometrie
        odo.update();
        //actualizarea unei variabile de pozitie
        Pose2D pos = odo.getPosition();

        //calcularea puterii pe fiecare motor
        drive = -gamepad1.left_stick_y;
        turn  =  gamepad1.right_stick_x;
        leftPower = Range.clip(drive + turn, -1.0, 1.0) ;
        rightPower = Range.clip(drive - turn, -1.0, 1.0) ;

        //adaugarea acelei puteri
        leftDrive.setPower(leftPower);
        rightDrive.setPower(rightPower);

        //adaugarea in timp real a informatilor relevante pentru driver pe driver hub
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "left (%.2f), right (%.2f)", leftPower, rightPower);
        telemetry.addData("Version", odo.getDeviceVersion());
        telemetry.addData("X", pos.getX(DistanceUnit.MM));
        telemetry.addData("Y", pos.getY(DistanceUnit.MM));
        updateTelemetry(telemetry);
    }
}
