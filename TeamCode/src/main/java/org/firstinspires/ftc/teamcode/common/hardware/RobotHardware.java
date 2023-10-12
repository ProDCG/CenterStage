package org.firstinspires.ftc.teamcode.common.hardware;

import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.outoftheboxrobotics.photoncore.hardware.PhotonLynxModule;
import com.outoftheboxrobotics.photoncore.hardware.i2c.imu.PhotonBNO055IMUNew;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.common.drive.pathing.geometry.profile.AsymmetricMotionProfile;
import org.firstinspires.ftc.teamcode.common.drive.pathing.geometry.profile.ProfileConstraints;
import org.firstinspires.ftc.teamcode.common.util.wrappers.Actuator;
import org.firstinspires.ftc.teamcode.common.util.wrappers.AnalogServo;

import java.util.List;

import javax.annotation.Nonnegative;

public class RobotHardware {

    public MotorEx extensionMotor;
    public MotorEx extensionPitchMotor;

    public DcMotorEx frontLeftMotor;
    public DcMotorEx frontRightMotor;
    public DcMotorEx backLeftMotor;
    public DcMotorEx backRightMotor;

    public AbsoluteAnalogEncoder extensionPitchEncoder;

    public Actuator actuator;

    // TODO: Configure hardware map
    // TODO: Configure invert positions
    public AnalogServo intakeClawLeftServo;
    public AnalogServo intakeClawRightServo;

    // TODO: Configure hardware map
    // TODO: Configure invert positions
    public Servo intakePivotLeftServo;
    public Servo intakePivotRightServo;
    public AbsoluteAnalogEncoder intakePivotEncoder;
    public Actuator intakePivotActuator;

    public DigitalChannel intakeClawLeftBottom, intakeClawLeftTop,
            intakeClawRightBottom, intakeClawRightTop;

    //TODO: Add 4x wall distance sensors
    //TODO: Add 2x Cameras

    /**
     * Odometry pod encoders.
     */
    public Motor.Encoder parallelPodLeft;
    public Motor.Encoder parallelPodRight;
    public Motor.Encoder perpindicularPod;

    /**
     * HardwareMap storage.
     */
    private HardwareMap hardwareMap;
    private Telemetry telemetry;

    /**
     * Voltage timer and voltage value.
     */
    private ElapsedTime voltageTimer;
    private double voltage = 0.0;

    /**
     * Singleton variables.
     */
    private static RobotHardware instance = null;
    public boolean enabled;

    private final PhotonBNO055IMUNew imu = hardwareMap.get(PhotonBNO055IMUNew.class, "imu");
    public List<PhotonLynxModule> modules;

    private double imuAngle;
    private double imuOffset;

    /**
     * Creating the singleton the first time, instantiating.
     */
    public static RobotHardware getInstance() {
        if (instance == null) {
            instance = new RobotHardware();
        }
        instance.enabled = true;
        return instance;
    }

    /**
     * Created at the start of every OpMode.
     *
     * @param hardwareMap The HardwareMap of the robot, storing all hardware devices
     * @param telemetry Saved for later in the event FTC Dashboard used
     */
    public void init(final HardwareMap hardwareMap, final Telemetry telemetry) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;

        // photon stuff
        for (PhotonLynxModule module : modules) {
            module.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        this.intakePivotActuator = new Actuator(intakePivotLeftServo, intakePivotRightServo, intakePivotEncoder)
                .setPIDController(new PIDController(1, 1, 1))
                .setMotionProfile(new AsymmetricMotionProfile(1, 1, new ProfileConstraints(1, 1, 1)))
                .setErrorTolerance(0.02);
    }

    public void read() {

    }

    public void write() {

    }

    public void loop() {
        if (voltageTimer.seconds() > 5) {
            voltageTimer.reset();
            voltage = hardwareMap.voltageSensor.iterator().next().getVoltage();
        }
    }

    public void reset() {

    }

    public void clearBulkCache() {
        for (PhotonLynxModule module : modules) {
            module.clearBulkCache();
        }
    }

    @Nonnegative
    public double getVoltage() {
        return voltage;
    }

    @Nonnegative
    public double getAngle() {
        return imuAngle - imuOffset;
    }
}
