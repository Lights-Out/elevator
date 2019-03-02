package sbit.task;

import org.junit.Test;

import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.*;

public class ElevatorImplTest {
    @Test
    public void shouldServiceAllRequests() throws InterruptedException {
        ElevatorImpl elevator = new ElevatorImpl(1);
        elevator.requestUp(1);
        elevator.requestUp(2);
        elevator.requestUp(3);
        elevator.requestDown(3);
        elevator.requestDown(2);
        elevator.requestDown(4);
        elevator.requestDown(1);
        elevator.run();
        Thread.sleep(50);
        assertThat(elevator.upFloors, empty());
        assertThat(elevator.downFloors, empty());
    }
}