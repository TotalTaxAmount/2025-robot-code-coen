/* Black Knights Robotics (C) 2025 */
package frc.robot.commands;

import edu.wpi.first.networktables.BooleanEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.constants.ScoringConstants;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.utils.ConfigManager;
import frc.robot.utils.NetworkTablesUtils;
import java.util.function.Supplier;

/** Command to set the elevator and arm */
public class ElevatorArmCommand extends Command {
    private final ElevatorSubsystem elevatorSubsystem;
    private final ArmSubsystem armSubsystem;

    private final NetworkTablesUtils elevator = NetworkTablesUtils.getTable("Elevator");

    private final BooleanEntry isAtHold = // TODO: Why??
            NetworkTableInstance.getDefault()
                    .getTable("Elevator")
                    .getBooleanTopic("AtHoldPos")
                    .getEntry(true);

    private final Supplier<ScoringConstants.ScoringHeights> targetSupplier;

    /**
     * Create an instance of the command to place the arm
     *
     * @param elevatorSubsystem The instance of {@link ElevatorSubsystem}
     * @param armSubsystem The instance of {@link ArmSubsystem}
     * @param targetSupplier A {@link Supplier<ScoringConstants.ScoringHeights>} for the target
     *     height and angle of the arm for the elevator
     */
    public ElevatorArmCommand(
            ElevatorSubsystem elevatorSubsystem,
            ArmSubsystem armSubsystem,
            Supplier<ScoringConstants.ScoringHeights> targetSupplier) {
        this.elevatorSubsystem = elevatorSubsystem;
        this.armSubsystem = armSubsystem;
        this.targetSupplier = targetSupplier;

        addRequirements(elevatorSubsystem, armSubsystem);
    }

    @Override
    public void initialize() {
        elevatorSubsystem.resetPID();
        armSubsystem.resetPID();
    }

    @Override
    public void execute() {
        double elevatorPos =
                ConfigManager.getInstance()
                        .get(
                                String.format(
                                        "elevator_%s",
                                        targetSupplier.get().toString().toLowerCase()),
                                0.0);
        double armPos =
                ConfigManager.getInstance()
                        .get(
                                String.format(
                                        "arm_%s", targetSupplier.get().toString().toLowerCase()),
                                0.0);

        elevator.setEntry("Setpoint", elevatorPos);

        //        if ((armSubsystem.getPivotAngle() <= -Math.PI / 4 || armSubsystem.getPivotAngle()
        // >= 0)
        //                && elevatorSubsystem.getElevatorPosition() <= 0.24
        //                && elevatorPos != 0) {
        //            isAtHold.set(true);
        //            armSubsystem.setPivotAngle(0.0);
        //            elevatorSubsystem.holdPosition();
        //        } else {
        //            isAtHold.set(false);
        //            armSubsystem.setPivotAngle(armPos);
        //            elevatorSubsystem.setTargetPosition(elevatorPos);
        //        }
        armSubsystem.setPivotAngle(armPos);
        elevatorSubsystem.setTargetPosition(elevatorPos);
    }
}
