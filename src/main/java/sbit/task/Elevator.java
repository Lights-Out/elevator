package sbit.task;

public interface Elevator {
    void run();
    void requestUp(int floor);
    void requestDown(int floor);
}
