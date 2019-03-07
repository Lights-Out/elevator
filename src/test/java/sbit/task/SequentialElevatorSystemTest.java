package sbit.task;

import org.junit.Test;

import static org.mockito.Mockito.*;

public class SequentialElevatorSystemTest {

    @Test
    public void shouldPreferMinimalDifferenceInFloorsForUp() {

        SequentialElevator first = spy(new SequentialElevator());
        first.requestMoving(1, 2);

        SequentialElevator second = spy(new SequentialElevator());
        second.requestMoving(1, 3);
        second.requestMoving(1, 4);
        second.requestMoving(1, 8);

        SequentialElevator third = spy(new SequentialElevator());
        third.requestMoving(6, 10);
        third.requestMoving(11, 12);

        SequentialElevatorSystem system = new SequentialElevatorSystem(new SequentialElevator[]{first, second, third});
        system.requestMoving(5, 6);
        verify(first, never()).requestMoving(5, 6);
        verify(second, times(1)).requestMoving(5, 6);
        verify(third, never()).requestMoving(5, 6);
    }

    @Test
    public void shouldPreferMinimalDifferenceInFloorsForDown() {

        SequentialElevator first = spy(new SequentialElevator());
        first.requestMoving(3, 2);

        SequentialElevator second = spy(new SequentialElevator());
        second.requestMoving(3, 2);
        second.requestMoving(7, 3);
        second.requestMoving(8, 4);

        SequentialElevator third = spy(new SequentialElevator());
        third.requestMoving(10, 7);
        third.requestMoving(14, 11);

        SequentialElevatorSystem system = new SequentialElevatorSystem(new SequentialElevator[]{first, second, third});
        system.requestMoving(6, 5);
        verify(first, never()).requestMoving(6, 5);
        verify(second, never()).requestMoving(6, 5);
        verify(third, times(1)).requestMoving(6, 5);
    }

    @Test
    public void shouldInvolveAllElevators() {
        SequentialElevator first = spy(new SequentialElevator(1));
        SequentialElevator second = spy(new SequentialElevator(1));
        SequentialElevator third = spy(new SequentialElevator(1));

        SequentialElevatorSystem system = new SequentialElevatorSystem(new SequentialElevator[]{first, second, third});
        system.requestMoving(5, 8);
        system.requestMoving(4, 11);
        system.requestMoving(4, 5);
        system.requestMoving(5, 7);
        system.requestMoving(6, 8);
        system.requestMoving(1, 2);

        system.requestMoving(12, 1);
        system.requestMoving(3, 1);
        system.requestMoving(4, 1);
        system.requestMoving(8, 1);
        system.requestMoving(14, 7);
        system.requestMoving(4, 8);
        system.requestMoving(5, 2);
        system.requestMoving(16, 4);
        system.requestMoving(7, 3);

        system.run();

        verify(first, atLeastOnce()).requestMoving(anyInt(), anyInt());
    }
}