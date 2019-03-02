package sbit.task;

import org.junit.Test;

import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.*;

public class ElevatorImplTest {
    @Test
    public void shouldServiceAllRequests() throws InterruptedException {
        ElevatorImpl elevator = new ElevatorImpl(1);
        elevator.requestUp(5);
        elevator.requestUp(3);
        elevator.requestUp(7);
        elevator.requestDown(4);
        elevator.requestDown(2);
        elevator.run();
        Thread.sleep(50);
        assertThat(elevator.upFloors, empty());
        assertThat(elevator.downFloors, empty());
    }
}