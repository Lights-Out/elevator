package sbit.task;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Queue;
import java.util.TreeSet;

public class SequentialElevator implements Elevator {

    private static final int IDLE_INTERVAL = 1000;
    private static final int FLOOR_HEIGHT = 4;
    private static final int DEFAULT_VELOCITY_MILLIS = 1000;
    private static final int STOP_COMMAND_CODE = -1;

    private final long floorReachTime;

    private int currentFloor = 1;
    private State state = State.IDLE;

    private TreeSet<Integer> upFloors = new TreeSet<>();
    private TreeSet<Integer> downFloors = new TreeSet<>(Comparator.reverseOrder());
    /**
     * Request queue that contains ordered floor and stop requests
     */
    Queue<Integer> orderedRequests = new ArrayDeque<>();

    public SequentialElevator() {
        this(DEFAULT_VELOCITY_MILLIS);
    }

    public SequentialElevator(int velocityMillis) {
        this.floorReachTime = FLOOR_HEIGHT * velocityMillis;
    }

    public void requestUp(int floorFrom, int floorTo) {
        if (upFloors.add(floorFrom)) {
            orderedRequests.add(floorFrom);
        }
        if (upFloors.add(floorTo)) {
            orderedRequests.add(floorTo);
        }
    }

    public void requestDown(int floorFrom, int floorTo) {
        if (downFloors.add(floorFrom)) {
            orderedRequests.add(floorFrom);
        }
        if (downFloors.add(floorTo)) {
            orderedRequests.add(floorTo);
        }
    }

    public void requestStop() {
        orderedRequests.add(STOP_COMMAND_CODE);
    }

    public void run() {
        try {
            while (hasWork()) {

                if (!upFloors.isEmpty()) {
                    Integer request = pollRequest(upFloors);
                    serviceRequest(request);
                }
                if (!downFloors.isEmpty()) {
                    Integer request = pollRequest(downFloors);
                    serviceRequest(request);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean hasWork() {
        return !upFloors.isEmpty() || !downFloors.isEmpty();
    }

    private void serviceRequest(Integer floor) throws InterruptedException {

        System.out.println("[" + currentFloor + "] Door opened");
        while (currentFloor != floor) {
            checkState();

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

        System.out.println("Arrived to the requested floor " + currentFloor);
        System.out.println("[" + currentFloor + "] Door closed");
    }

    private void tryPickUp() throws InterruptedException {
        TreeSet<Integer> requests = state == State.MOVING_UP ? upFloors : downFloors;

        if (!requests.isEmpty()) {
            Integer request = requests.first();
            if (currentFloor == request) {
                pollRequest(requests);

                checkState();
                System.out.println("[" + currentFloor + "] Door opened");
                System.out.println("Request completed");
                System.out.println("[" + currentFloor + "] Door closed");
            }
        }
    }

    private void checkState() throws InterruptedException {
        while (state == State.STOPPED) {
            Thread.sleep(IDLE_INTERVAL);
        }
    }

    private Integer pollRequest(TreeSet<Integer> set) {
        checkQueueForStop();
        return set.pollFirst();
    }

    /**
     * Since requests sets do not contain stop request, we have to retrieve at least one element from the queue
     * to synchronize sets and queue.
     * All consecutive stop requests will be served at once.
     */
    private void checkQueueForStop() {

        while (true) {

            Integer request = orderedRequests.poll();
            if (request != null && request == STOP_COMMAND_CODE) {
                stop();
            } else {
                break;
            }
        }
    }

    private void stop() {
        if (state != State.STOPPED) {
            state = State.STOPPED;
            System.out.println("[" + currentFloor + "] STOPPED");
        } else {
            state = State.IDLE;
            System.out.println("[" + currentFloor + "] RESUMED");
        }
    }

    private enum State {
        IDLE,
        MOVING_UP,
        MOVING_DOWN,
        STOPPED
    }
}
