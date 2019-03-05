package sbit.task;

import org.junit.Test;

import static org.mockito.Mockito.*;

public class SequentialElevatorSystemTest {

    @Test
    public void shouldPreferMinimalDifferenceInFloorsForUp() {

        SequentialElevator first = spy(new SequentialElevator());
        first.requestUp(1, 2);

        SequentialElevator second = spy(new SequentialElevator());
        second.requestUp(1, 3);
        second.requestUp(1, 4);
        second.requestUp(1, 8);

        SequentialElevator third = spy(new SequentialElevator());
        third.requestUp(6, 10);
        third.requestUp(11, 12);

        SequentialElevatorSystem system = new SequentialElevatorSystem(new SequentialElevator[]{first, second, third});
        system.requestUp(5, 6);
        verify(first, never()).requestUp(5, 6);
        verify(second, times(1)).requestUp(5, 6);
        verify(third, never()).requestUp(5, 6);
    }

    @Test
    public void shouldPreferMinimalDifferenceInFloorsForDown() {

        SequentialElevator first = spy(new SequentialElevator());
        first.requestDown(3, 2);

        SequentialElevator second = spy(new SequentialElevator());
        second.requestDown(3, 2);
        second.requestDown(7, 3);
        second.requestDown(8, 4);

        SequentialElevator third = spy(new SequentialElevator());
        third.requestDown(10, 7);
        third.requestDown(14, 11);

        SequentialElevatorSystem system = new SequentialElevatorSystem(new SequentialElevator[]{first, second, third});
        system.requestDown(6, 5);
        verify(first, never()).requestDown(6, 5);
        verify(second, never()).requestDown(6, 5);
        verify(third, times(1)).requestDown(6, 5);
    }

    @Test
    public void shouldInvolveAllElevators() {
        SequentialElevator first = spy(new SequentialElevator(1));
        SequentialElevator second = spy(new SequentialElevator(1));
        SequentialElevator third = spy(new SequentialElevator(1));

        SequentialElevatorSystem system = new SequentialElevatorSystem(new SequentialElevator[]{first, second, third});
        system.requestUp(5, 8);
        system.requestUp(4, 11);
        system.requestUp(4, 5);
        system.requestUp(5, 7);
        system.requestUp(6, 8);
        system.requestUp(1, 2);

        system.requestDown(12, 1);
        system.requestDown(3, 1);
        system.requestDown(4, 1);
        system.requestDown(8, 1);
        system.requestDown(14, 7);
        system.requestDown(4, 8);
        system.requestDown(5, 2);
        system.requestDown(16, 4);
        system.requestDown(7, 3);

        system.run();

        verify(first, atLeastOnce()).requestDown(anyInt(), anyInt());
    }
}