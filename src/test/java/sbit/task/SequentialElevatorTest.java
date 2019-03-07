package sbit.task;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

public class SequentialElevatorTest {

    private static final int DELAY_TIME = 200;

    @Test
    public void shouldHandlePairedRequest() throws InterruptedException {
        SequentialElevator elevator = new SequentialElevator(1);
        elevator.requestMoving(1, 4);
        elevator.requestMoving(2, 5);
        elevator.requestMoving(3, 9);
        elevator.requestMoving(2, 5);
        elevator.requestMoving(4, 2);
        elevator.requestMoving(7, 1);
        elevator.run();
        Thread.sleep(DELAY_TIME);
        assertThat(elevator.orderedRequests, empty());
    }

    @Test
    public void shouldInterruptMovingOnStopCommand() throws InterruptedException {

        SequentialElevator elevator = new SequentialElevator(1);
        elevator.requestMoving(1, 4);
        elevator.requestMoving(2, 3);
        elevator.requestMoving(2, 3);
        elevator.requestMoving(2, 3);
        elevator.requestStop();
        elevator.requestStop();
        elevator.requestStop();
        elevator.requestMoving(4, 2);
        elevator.requestMoving(7, 1);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(elevator::run);

        Thread.sleep(DELAY_TIME);
        executor.shutdown();
        assertThat(elevator.orderedRequests, not(empty()));
    }

    @Test
    public void shouldProceedOnSecondStopCommand() throws InterruptedException {
        SequentialElevator elevator = new SequentialElevator(1);
        elevator.requestMoving(1, 4);
        elevator.requestMoving(2, 5);
        elevator.requestMoving(3, 9);
        elevator.requestStop();
        elevator.requestStop();
        elevator.requestStop();
        elevator.requestStop();
        elevator.requestMoving(4, 2);
        elevator.requestMoving(7, 1);
        elevator.run();
        Thread.sleep(DELAY_TIME);
        assertThat(elevator.orderedRequests, empty());
    }

    @Test
    public void shouldProceedOnAlternatingStopAndFloorCommands() throws InterruptedException {

        SequentialElevator elevator = new SequentialElevator(1);
        elevator.requestMoving(1, 4);
        elevator.requestMoving(2, 3);
        elevator.requestStop();
        elevator.requestStop();
        elevator.requestMoving(2, 3);
        elevator.requestMoving(2, 3);
        elevator.requestStop();
        elevator.requestStop();
        elevator.requestMoving(4, 2);
        elevator.requestMoving(7, 1);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(elevator::run);

        Thread.sleep(DELAY_TIME);
        executor.shutdown();
        assertThat(elevator.orderedRequests, empty());
    }

    @Test
    public void shouldInterruptMovingOnStopGoStopCommand() throws InterruptedException {

        SequentialElevator elevator = new SequentialElevator(1);
        elevator.requestMoving(1, 4);
        elevator.requestMoving(2, 3);
        elevator.requestStop();
        elevator.requestMoving(3, 5);
        elevator.requestStop();
        elevator.requestMoving(4, 2);
        elevator.requestMoving(7, 1);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(elevator::run);
        Thread.sleep(DELAY_TIME);
        executor.shutdown();
        assertThat(elevator.orderedRequests, not(empty()));
    }
}