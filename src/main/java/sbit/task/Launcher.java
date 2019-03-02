package sbit.task;

public class Launcher {
    public static void main(String[] args) {
        Elevator elevator = new ElevatorImpl();
        elevator.requestUp(5);
        elevator.requestUp(3);
        elevator.requestUp(7);
        elevator.requestDown(4);
        elevator.requestDown(2);
        elevator.run();
    }
}