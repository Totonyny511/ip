package tony.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests assumptions shared by every task type.
 */
public class TaskTest {
    /** Verifies that a null description is rejected even when assertions are disabled. */
    @Test
    public void constructor_nullDescription_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Todo(null));

        assertEquals("A task description cannot be null", exception.getMessage());
    }

    /** Verifies that a blank description is rejected even when assertions are disabled. */
    @Test
    public void constructor_blankDescription_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Todo("  "));

        assertEquals("A task description cannot be blank", exception.getMessage());
    }

    /** Verifies that control characters cannot corrupt the line-based data format. */
    @Test
    public void constructor_descriptionWithControlCharacter_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> new Todo("read\tbook"));

        assertEquals("A task description cannot contain control characters", exception.getMessage());
    }
}
