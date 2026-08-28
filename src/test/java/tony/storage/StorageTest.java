package tony.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tony.task.Deadline;
import tony.task.Event;
import tony.task.Task;
import tony.task.TaskList;
import tony.task.Todo;

/**
 * Tests loading and saving tasks through {@link Storage}.
 */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void load_missingFile_returnsEmptyResult() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing/tasks.txt"));

        Storage.LoadResult result = storage.load();

        assertEquals(List.of(), result.getTasks());
        assertEquals(0, result.getSkippedLineCount());
    }

    @Test
    public void load_validTaskTypesAndEscapedDescriptions_reconstructsTasks() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(dataFile, List.of(
                "T | 1 | compare A \\| B",
                "D | 0 | review C:\\\\notes | 2026-08-12",
                "E | 1 | orientation | 2026-08-10 | 2026-08-12"));
        Storage storage = new Storage(dataFile);

        Storage.LoadResult result = storage.load();

        assertEquals(List.of(
                "T | 1 | compare A \\| B",
                "D | 0 | review C:\\\\notes | 2026-08-12",
                "E | 1 | orientation | 2026-08-10 | 2026-08-12"),
                result.getTasks().stream().map(Task::toDataString).toList());
        assertEquals(0, result.getSkippedLineCount());
    }

    @Test
    public void load_legacyUnescapedBackslash_preservesBackslash() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(dataFile, List.of("T | 0 | review C:\\notes"));
        Storage storage = new Storage(dataFile);

        Storage.LoadResult result = storage.load();

        assertEquals("[T][ ] review C:\\notes", result.getTasks().get(0).toString());
        assertEquals(0, result.getSkippedLineCount());
    }

    @Test
    public void load_blankAndMalformedLines_skipsOnlyMalformedNonBlankLines() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(dataFile, List.of(
                "T | 0 | valid task",
                "",
                "   ",
                "X | 0 | unknown type",
                "T | 2 | bad status",
                "T | 0 | ",
                "D | 0 | missing date",
                "D | 0 | impossible date | 2026-02-30",
                "E | 0 | backwards | 2026-08-12 | 2026-08-10",
                "E | 0 | too | many | fields | here"));
        Storage storage = new Storage(dataFile);

        Storage.LoadResult result = storage.load();

        assertEquals(List.of("T | 0 | valid task"),
                result.getTasks().stream().map(Task::toDataString).toList());
        assertEquals(7, result.getSkippedLineCount());
    }

    @Test
    public void load_fileIsDirectory_throwsIOException() {
        Storage storage = new Storage(temporaryDirectory);

        assertThrows(IOException.class, storage::load);
    }

    @Test
    public void save_taskList_createsParentDirectoryAndWritesAllTasks() throws IOException {
        Path dataFile = temporaryDirectory.resolve("nested/data/tasks.txt");
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 8, 12));
        Event event = new Event("orientation", LocalDate.of(2026, 8, 10),
                LocalDate.of(2026, 8, 11));
        deadline.markAsDone();
        Storage storage = new Storage(dataFile);

        storage.save(new TaskList(List.of(todo, deadline, event)));

        assertEquals(List.of(
                "T | 0 | read book",
                "D | 1 | submit report | 2026-08-12",
                "E | 0 | orientation | 2026-08-10 | 2026-08-11"),
                Files.readAllLines(dataFile));
    }

    @Test
    public void save_existingFile_replacesOldContents() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(dataFile, List.of("T | 0 | obsolete task", "T | 0 | another old task"));
        Storage storage = new Storage(dataFile);

        storage.save(new TaskList(List.of(new Todo("current task"))));

        assertEquals(List.of("T | 0 | current task"), Files.readAllLines(dataFile));
    }

    @Test
    public void save_parentPathIsFile_throwsIOException() throws IOException {
        Path parentFile = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(parentFile, "content");
        Storage storage = new Storage(parentFile.resolve("tasks.txt"));

        assertThrows(IOException.class, () -> storage.save(new TaskList()));
    }
}
