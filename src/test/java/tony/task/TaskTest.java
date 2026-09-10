package tony.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests assumptions shared by every task type.
 */
public class TaskTest {
    /** Verifies that validated task creation never supplies a null description. */
    @Test
    public void constructor_nullDescription_throwsAssertionError() {
        AssertionError error = assertThrows(AssertionError.class, () -> new Todo(null));

        assertEquals("A task description must not be null", error.getMessage());
    }

    /** Verifies that validated task creation never supplies a blank description. */
    @Test
    public void constructor_blankDescription_throwsAssertionError() {
        AssertionError error = assertThrows(AssertionError.class, () -> new Todo("  "));

        assertEquals("A task description must not be blank", error.getMessage());
    }
}
