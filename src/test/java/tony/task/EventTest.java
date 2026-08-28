package tony.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests the validation, date matching, and formatting behavior of {@link Event}.
 */
public class EventTest {
    private static final LocalDate START_DATE = LocalDate.of(2026, 8, 10);
    private static final LocalDate END_DATE = LocalDate.of(2026, 8, 12);

    @Test
    public void constructor_nullStartDate_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Event("Attend orientation", null, END_DATE));

        assertEquals("Event dates cannot be null", exception.getMessage());
    }

    @Test
    public void constructor_nullEndDate_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Event("Attend orientation", START_DATE, null));

        assertEquals("Event dates cannot be null", exception.getMessage());
    }

    @Test
    public void constructor_endDateBeforeStartDate_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Event("Attend orientation", START_DATE, START_DATE.minusDays(1)));

        assertEquals("An event's end date cannot be before its start date", exception.getMessage());
    }

    @Test
    public void isOnDate_dateBeforeStart_returnsFalse() {
        Event event = new Event("Attend orientation", START_DATE, END_DATE);

        assertFalse(event.isOnDate(START_DATE.minusDays(1)));
    }

    @Test
    public void isOnDate_dateOnStart_returnsTrue() {
        Event event = new Event("Attend orientation", START_DATE, END_DATE);

        assertTrue(event.isOnDate(START_DATE));
    }

    @Test
    public void isOnDate_dateBetweenStartAndEnd_returnsTrue() {
        Event event = new Event("Attend orientation", START_DATE, END_DATE);

        assertTrue(event.isOnDate(START_DATE.plusDays(1)));
    }

    @Test
    public void isOnDate_dateOnEnd_returnsTrue() {
        Event event = new Event("Attend orientation", START_DATE, END_DATE);

        assertTrue(event.isOnDate(END_DATE));
    }

    @Test
    public void isOnDate_dateAfterEnd_returnsFalse() {
        Event event = new Event("Attend orientation", START_DATE, END_DATE);

        assertFalse(event.isOnDate(END_DATE.plusDays(1)));
    }

    @Test
    public void isOnDate_singleDayEventOnEventDate_returnsTrue() {
        Event event = new Event("Submit project", START_DATE, START_DATE);

        assertTrue(event.isOnDate(START_DATE));
    }

    @Test
    public void isOnDate_nullDate_returnsFalse() {
        Event event = new Event("Attend orientation", START_DATE, END_DATE);

        assertFalse(event.isOnDate(null));
    }

    @Test
    public void toDataString_incompleteEvent_returnsStorageFormat() {
        Event event = new Event("Attend orientation", START_DATE, END_DATE);

        assertEquals("E | 0 | Attend orientation | 2026-08-10 | 2026-08-12",
                event.toDataString());
    }

    @Test
    public void toDataString_completedEvent_returnsCompletedStatus() {
        Event event = new Event("Attend orientation", START_DATE, END_DATE);
        event.markAsDone();

        assertEquals("E | 1 | Attend orientation | 2026-08-10 | 2026-08-12",
                event.toDataString());
    }

    @Test
    public void toDataString_descriptionWithSpecialCharacters_escapesDescription() {
        Event event = new Event("Compare A | B at C:\\notes", START_DATE, END_DATE);

        assertEquals("E | 0 | Compare A \\| B at C:\\\\notes | 2026-08-10 | 2026-08-12",
                event.toDataString());
    }

    @Test
    public void toString_incompleteEvent_returnsDisplayFormat() {
        Event event = new Event("Attend orientation", START_DATE, END_DATE);

        assertEquals("[E][ ] Attend orientation (from: Aug 10 2026 to: Aug 12 2026)",
                event.toString());
    }

    @Test
    public void toString_completedEvent_returnsCompletedStatus() {
        Event event = new Event("Attend orientation", START_DATE, END_DATE);
        event.markAsDone();

        assertEquals("[E][X] Attend orientation (from: Aug 10 2026 to: Aug 12 2026)",
                event.toString());
    }
}
