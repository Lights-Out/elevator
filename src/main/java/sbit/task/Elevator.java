package sbit.task;

public interface Elevator {
    void run();

    /**
     * @param floorFrom Current caller's floor
     * @param floorTo Destination floor
     */
    void requestUp(int floorFrom, int floorTo);
    /**
     * @param floorFrom Current caller's floor
     * @param floorTo Destination floor
     */
    void requestDown(int floorFrom, int floorTo);

    void requestStop();
}
