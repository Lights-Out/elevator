package sbit.task;

public interface Elevator {

    void run();

    /**
     * @param floorFrom Current caller's floor
     * @param floorTo Destination floor
     */
    void requestMoving(int floorFrom, int floorTo);

    void requestStop();
}
