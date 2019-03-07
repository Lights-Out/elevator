package sbit.task;

import java.util.Arrays;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class SequentialElevatorSystem {

    private final SequentialElevator[] elevators;

    public SequentialElevatorSystem(int elevatorsCount) {
        elevators = new SequentialElevator[elevatorsCount];
    }

    public SequentialElevatorSystem(SequentialElevator[] elevators) {
        this.elevators = elevators;
    }

    public void run() {
        for (SequentialElevator elevator : elevators) {
            elevator.run();
        }
    }

    /**
     * @param floorFrom Current caller's floor
     * @param floorTo Destination floor
     */
    public void requestMoving(int floorFrom, int floorTo) {
        SequentialElevator chosenOne = chooseElevator(floorFrom, floorTo);
        chosenOne.requestMoving(floorFrom, floorTo);
    }

    private SequentialElevator chooseElevator(int floorFrom, int floorTo) {
        int minFloor = min(floorFrom, floorTo);
        int maxFloor = max(floorFrom, floorTo);

        return Arrays.stream(elevators)
                .reduce((first, second) -> {
                    int firstElevatorFloorsDifference = floorsDifference(first, minFloor, maxFloor);
                    int secondElevatorFloorsDifference = floorsDifference(second, minFloor, maxFloor);

                    return secondElevatorFloorsDifference < firstElevatorFloorsDifference ? second : first;
                })
                .orElseThrow(
                        () -> new IllegalStateException("No elevators")
                );
    }

    private int floorsDifference(SequentialElevator elevator, int minFloor, int maxFloor) {

        int highest = elevator.highestFloor()
                .map(value -> abs(value - maxFloor))
                .orElse(0);

        int lowest = elevator.lowestFloor()
                .map(value -> abs(value - minFloor))
                .orElse(0);

        return highest + lowest;
    }
}
