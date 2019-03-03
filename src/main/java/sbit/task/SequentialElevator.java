package sbit.task;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Queue;
import java.util.TreeSet;

public class SequentialElevator implements Elevator {

    private static final int IDLE_INTERVAL = 1000;
    private static final int FLOOR_HEIGHT = 4;
    private static final int DEFAULT_VELOCITY_MILLIS = 1000;

    private final long floorReachTime;

    private int currentFloor = 1;
    private State state = State.IDLE;

    private TreeSet<Request> upFloors = new TreeSet<>();
    private TreeSet<Request> downFloors = new TreeSet<>(Comparator.reverseOrder());
    /**
     * Request queue that contains ordered floor and stop requests
     */
    Queue<Request> orderedRequests = new ArrayDeque<>();

    public SequentialElevator() {
        this(DEFAULT_VELOCITY_MILLIS);
    }

    public SequentialElevator(int velocityMillis) {
        this.floorReachTime = FLOOR_HEIGHT * velocityMillis;
    }

    public void requestUp(int floorFrom, int floorTo) {
        putRequest(floorFrom, upFloors);
        putRequest(floorTo, upFloors);
    }

    public void requestDown(int floorFrom, int floorTo) {
        putRequest(floorFrom, downFloors);
        putRequest(floorTo, downFloors);
    }

    public void requestStop() {
        orderedRequests.add(Request.stop());
    }

    public void run() {
        try {
            while (hasWork()) {

                if (!upFloors.isEmpty()) {
                    Request request = pollRequest(upFloors);
                    serviceRequest(request);
                }
                if (!downFloors.isEmpty()) {
                    Request request = pollRequest(downFloors);
                    serviceRequest(request);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Puts request to both set and queue without duplicates.
     * @param floor requested floor
     * @param target requests set (up/down)
     */
    private void putRequest(int floor, TreeSet<Request> target) {
        Request request = Request.move(floor);
        if (target.add(request)) {
            orderedRequests.add(request);
        }
    }

    private boolean hasWork() {
        return !upFloors.isEmpty() || !downFloors.isEmpty();
    }

    private void serviceRequest(Request request) throws InterruptedException {

        int floor = request.getFloor();
        while (currentFloor != floor) {
            checkStoppedState();

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

        notifyAboutCompletedRequest();
    }

    private void notifyAboutCompletedRequest() {
        System.out.println("[" + currentFloor + "] Door opened");
        System.out.println("Request to floor [" + currentFloor + "] completed");
        System.out.println("[" + currentFloor + "] Door closed");
    }

    private void tryPickUp() throws InterruptedException {
        TreeSet<Request> requests = state == State.MOVING_UP ? upFloors : downFloors;

        if (!requests.isEmpty()) {
            Request request = requests.first();
            if (currentFloor == request.getFloor()) {
                pollRequest(requests);

                checkStoppedState();
                notifyAboutCompletedRequest();
            }
        }
    }

    private void checkStoppedState() throws InterruptedException {
        while (state == State.STOPPED) {
            Thread.sleep(IDLE_INTERVAL);
        }
    }

    private Request pollRequest(TreeSet<Request> set) {
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

            Request request = orderedRequests.poll();
            if (request != null && request.getType() == Request.Type.STOP) {
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

    private static class Request implements Comparable<Request> {
        private int floor;
        private Type type;

        private Request(int floor, Type type) {
            this.floor = floor;
            this.type = type;
        }

        private Request(Type type) {
            this.type = type;
        }

        static Request move(int floor) {
            return new Request(floor, Type.MOVE);
        }

        static Request stop() {
            return new Request(Type.STOP);
        }

        @Override
        public int compareTo(Request o) {
            if (type == Type.STOP) {
                return type.compareTo(o.getType());
            }
            return Integer.compare(floor, o.floor);
        }

        int getFloor() {
            return floor;
        }

        Type getType() {
            return type;
        }

        private enum Type {
            MOVE, STOP
        }
    }
}