package tony.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests the validation, date matching, and formatting behavior of {@link Deadline}.
 */
public class DeadlineTest {
    private static final LocalDate DUE_DATE = LocalDate.of(2026, 8, 12);

    @Test
    public void constructor_nullDueDate_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Deadline("Submit report", null));

        assertEquals("A deadline date cannot be null", exception.getMessage());
    }

    @Test
    public void isOnDate_dateOnDeadline_returnsTrue() {
        Deadline deadline = new Deadline("Submit report", DUE_DATE);

        assertTrue(deadline.isOnDate(DUE_DATE));
    }

    @Test
    public void isOnDate_dateNotOnDeadline_returnsFalse() {
        Deadline deadline = new Deadline("Submit report", DUE_DATE);

        assertFalse(deadline.isOnDate(DUE_DATE.minusDays(1)));
    }

    @Test
    public void isOnDate_nullDate_returnsFalse() {
        Deadline deadline = new Deadline("Submit report", DUE_DATE);

        assertFalse(deadline.isOnDate(null));
    }

    @Test
    public void toDataString_incompleteDeadline_returnsStorageFormat() {
        Deadline deadline = new Deadline("Submit report", DUE_DATE);

        assertEquals("D | 0 | Submit report | 2026-08-12", deadline.toDataString());
    }

    @Test
    public void toDataString_completedDeadlineWithSpecialCharacters_returnsEscapedStorageFormat() {
        Deadline deadline = new Deadline("Review A | B at C:\\notes", DUE_DATE);
        deadline.markAsDone();

        assertEquals("D | 1 | Review A \\| B at C:\\\\notes | 2026-08-12",
                deadline.toDataString());
    }

    @Test
    public void toString_incompleteDeadline_returnsDisplayFormat() {
        Deadline deadline = new Deadline("Submit report", DUE_DATE);

        assertEquals("[D][ ] Submit report (by: Aug 12 2026)", deadline.toString());
    }

    @Test
    public void toString_completedDeadline_returnsCompletedStatus() {
        Deadline deadline = new Deadline("Submit report", DUE_DATE);
        deadline.markAsDone();

        assertEquals("[D][X] Submit report (by: Aug 12 2026)", deadline.toString());
    }
}
