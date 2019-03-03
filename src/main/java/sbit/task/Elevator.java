package sbit.task;

public interface Elevator {
    void run();
    void requestUp(int floorFrom, int floorTo);
    void requestDown(int floorFrom, int floorTo);
    void requestStop();
}
