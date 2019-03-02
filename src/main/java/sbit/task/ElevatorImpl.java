package sbit.task;

import java.util.Comparator;
import java.util.TreeSet;

public class ElevatorImpl implements Elevator {

    private static final int IDLE_INTERVAL = 1000;
    private static final int FLOOR_HEIGHT = 4;
    private static final int DEFAULT_VELOCITY_MILLIS = 1000;

    private int velocityMillis;
    private final long floorReachTime = FLOOR_HEIGHT * velocityMillis;

    private int currentFloor = 1;
    private State state = State.IDLE;

    TreeSet<Integer> upFloors = new TreeSet<>();
    TreeSet<Integer> downFloors = new TreeSet<>(Comparator.reverseOrder());

    public ElevatorImpl() {
        this(DEFAULT_VELOCITY_MILLIS);
    }

    public ElevatorImpl(int velocityMillis) {
        this.velocityMillis = velocityMillis;
    }

    public void requestUp(int floor) {
        upFloors.add(floor);
    }

    public void requestDown(int floor) {
        downFloors.add(floor);
    }

    public void run() {
        try {
            while (hasWork()) {

                if (!upFloors.isEmpty()) {
                    Integer request = upFloors.pollFirst();
                    moveToFloor(request);
                }
                if (!downFloors.isEmpty()) {
                    Integer request = downFloors.pollFirst();
                    moveToFloor(request);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean hasWork() {
        return !upFloors.isEmpty() || !downFloors.isEmpty();
    }

    private void moveToFloor(Integer floor) throws InterruptedException {

        while (currentFloor != floor) {
            System.out.println("start moving from " + currentFloor + " to " + floor);
            if (currentFloor > floor) {
                state = State.MOVING_DOWN;
                --currentFloor;
                tryPickUp();

            } else {
                state = State.MOVING_UP;
                ++currentFloor;
                tryPickUp();
            }

            Thread.sleep(floorReachTime);
            System.out.println("On floor " + currentFloor);
        }

        System.out.println("Arrived to the requested floor " + floor);
        System.out.println("Door closed");
    }

    private void tryPickUp() {
        TreeSet<Integer> requests = state == State.MOVING_UP ? upFloors : downFloors;

        if (!requests.isEmpty()) {
            Integer order = requests.first();
            if (currentFloor == order) {
                requests.pollFirst();
                System.out.println("Picked up on floor " + currentFloor);
                System.out.println("Door closed");
            }
        }
    }

    private enum State {
        IDLE,
        MOVING_UP,
        MOVING_DOWN,
    }
}
