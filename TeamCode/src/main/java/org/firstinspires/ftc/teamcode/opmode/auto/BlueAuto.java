package org.firstinspires.ftc.teamcode.opmode.auto;

import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.ParallelCommandGroup;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;
import com.arcrobotics.ftclib.command.WaitUntilCommand;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.common.centerstage.ClawSide;
import org.firstinspires.ftc.teamcode.common.centerstage.PropPipeline;
import org.firstinspires.ftc.teamcode.common.centerstage.Side;
import org.firstinspires.ftc.teamcode.common.commandbase.drivecommand.PositionCommand;
import org.firstinspires.ftc.teamcode.common.commandbase.subsytemcommand.ClawCommand;
import org.firstinspires.ftc.teamcode.common.drive.drivetrain.Drivetrain;
import org.firstinspires.ftc.teamcode.common.drive.drivetrain.MecanumDrivetrain;
import org.firstinspires.ftc.teamcode.common.drive.localizer.ThreeWheelLocalizer;
import org.firstinspires.ftc.teamcode.common.drive.pathing.geometry.Pose;
import org.firstinspires.ftc.teamcode.common.hardware.Globals;
import org.firstinspires.ftc.teamcode.common.hardware.RobotHardware;
import org.firstinspires.ftc.teamcode.common.subsystem.ExtensionSubsystem;
import org.firstinspires.ftc.teamcode.common.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.common.util.wrappers.WSubsystem;
import org.firstinspires.ftc.vision.VisionPortal;

@Config
@Autonomous(name = "Blue Auto PRELOAD")
public class BlueAuto extends CommandOpMode {

    private final RobotHardware robot = RobotHardware.getInstance();
    private WSubsystem drivetrain;
    private ThreeWheelLocalizer localizer;
    private ExtensionSubsystem extension;
    private IntakeSubsystem intake;

    private PropPipeline propPipeline;
    private VisionPortal portal;


    private double loopTime = 0.0;

    @Override
    public void initialize() {
        CommandScheduler.getInstance().reset();

        Globals.IS_AUTO = true;
        Globals.IS_USING_IMU = false;
        Globals.USING_DASHBOARD = true;
        Globals.COLOR = Side.BLUE;

        robot.init(hardwareMap, telemetry);
        robot.enabled = true;
        drivetrain = new MecanumDrivetrain();
        localizer = new ThreeWheelLocalizer();
        extension = new ExtensionSubsystem();
        intake = new IntakeSubsystem();

        propPipeline = new PropPipeline();
        portal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam"))
                .setCameraResolution(new Size(1920, 1080))
                .setCamera(BuiltinCameraDirection.BACK)
                .addProcessor(propPipeline)
//                .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                .enableLiveView(true)
                .setAutoStopLiveView(true)
                .build();

        telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry());

        robot.addSubsystem(drivetrain, extension, intake);
        intake.updateState(IntakeSubsystem.ClawState.CLOSED, ClawSide.BOTH);

        robot.read();
        while (!isStarted()) {
            telemetry.addLine("auto in init");
            telemetry.addData("POS", propPipeline.getLocation());
            telemetry.update();
        }

        localizer.setPoseEstimate(new Pose2d(0, 0, 0));

//        Side side = propPipeline.getLocation();
        Side side = Side.RIGHT;
        portal.close();

        Pose yellowScorePos = new Pose();
        Pose purpleScorePos = new Pose();
        Pose parkPos = new Pose();


        // 0.3, 300

        switch (side) {
            case LEFT:
                yellowScorePos = new Pose(21.5, -22.25, 1.52);
                purpleScorePos = new Pose(36.5, -24, 1.52);
                parkPos = new Pose(6, -31, 3 * Math.PI / 2);
                break;
            case CENTER:
                yellowScorePos = new Pose(28, -22.25, 1.52);
                purpleScorePos = new Pose(36, -18, 1.52);
                parkPos = new Pose(5, -31, 3 * Math.PI / 2);
                break;
            case RIGHT:
                yellowScorePos = new Pose(34, -22.2, 1.52);
                purpleScorePos = new Pose(26.5, -4.5, 1.52);
                parkPos = new Pose(2, -31, 3 * Math.PI / 2);
                break;
            default:
                // your mom
                break;

        }

        CommandScheduler.getInstance().schedule(
                new SequentialCommandGroup(
                        // scoring pos
                        new PositionCommand((Drivetrain) drivetrain, localizer, yellowScorePos),

                        // extend
                        new InstantCommand(() -> extension.setScoring(true)),
                        new InstantCommand(() -> extension.setFlip(false)),
                        new InstantCommand(() -> intake.updateState(IntakeSubsystem.PivotState.SCORING)),
                        new InstantCommand(() -> robot.pitchActuator.setMotionProfileTargetPosition(0.172)),
                        new InstantCommand(() -> robot.extensionActuator.setMotionProfileTargetPosition(435)),
                        new WaitCommand(750),
//                        // open claw boi
                        new InstantCommand(() -> intake.updateState(IntakeSubsystem.ClawState.INTERMEDIATE, ClawSide.RIGHT)),
                        new WaitCommand(200),
//
//                        // retract
                        new InstantCommand(() -> robot.extensionActuator.setMotionProfileTargetPosition(0)),
                        new WaitCommand(50),
                        new InstantCommand(() -> extension.setScoring(false)),
                        new InstantCommand(() -> extension.setFlip(false)),
                        new InstantCommand(() -> robot.intakePivotActuator.setTargetPosition(0.0475)),
                        new InstantCommand(() -> intake.updateState(IntakeSubsystem.ClawState.CLOSED, ClawSide.RIGHT)),
                        new InstantCommand(() -> robot.pitchActuator.setMotionProfileTargetPosition(Math.PI)),
//
                        new ParallelCommandGroup(
                                new PositionCommand((Drivetrain) drivetrain, localizer, purpleScorePos),
                                new SequentialCommandGroup(
                                        new WaitCommand(250),
                                        new InstantCommand(() -> intake.updateState(IntakeSubsystem.PivotState.FLAT)),
                                        new InstantCommand(() -> robot.intakePivotActuator.setTargetPosition(0.515))
                                )
                        ),

                        new WaitUntilCommand(() -> robot.pitchActuator.hasReached()),
                        new WaitCommand(200),
                        new InstantCommand(() -> intake.updateState(IntakeSubsystem.ClawState.INTERMEDIATE, ClawSide.LEFT)),
                        new WaitCommand(500),

                        new InstantCommand(() -> robot.pitchActuator.setMotionProfileTargetPosition(0.0)),
                        new InstantCommand(() -> robot.extensionActuator.setMotionProfileTargetPosition(0)),
                        new ClawCommand(intake, IntakeSubsystem.ClawState.CLOSED, ClawSide.LEFT),
                        new InstantCommand(() -> intake.updateState(IntakeSubsystem.PivotState.FLAT))
//
//                        new PositionCommand((Drivetrain) drivetrain, localizer, parkPos)
//                                .alongWith(new WaitCommand(400).andThen(new InstantCommand(() -> robot.intakePivotActuator.setTargetPosition(0.0475))))
                )
        );
    }

    @Override
    public void run() {

        robot.read();

        super.run();
        robot.periodic();
        localizer.periodic();

        double loop = System.nanoTime();
        telemetry.addData("hz ", 1000000000 / (loop - loopTime));
        telemetry.addData("voltage", robot.getVoltage());
        loopTime = loop;
        telemetry.update();

        robot.write();
        robot.clearBulkCache();
    }
}