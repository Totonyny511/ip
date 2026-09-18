# Tony User Guide

Tony is a desktop task manager that helps you organize to-dos, deadlines, and events through simple text commands.
Your tasks are saved automatically between sessions.

## Quick start

1. Open the project folder in a terminal.
2. Run `./gradlew run`.
3. Type a command in the input box.
4. Press <kbd>Enter</kbd> or select **Send**.

Use the **List**, **Help**, and **Bye** buttons for quick access to common actions.

## Command format

> [!NOTE]
>
> - Words in `UPPER_CASE` are values you provide. For example, replace `DESCRIPTION` with `read a book`.
> - Items in square brackets are optional.
> - Enter dates in `yyyy-MM-dd` format, such as `2026-09-20`.
> - Command words should be entered in lowercase.

## Adding tasks

| Task type | Command format | Example |
| --- | --- | --- |
| To-do | `todo DESCRIPTION` | `todo read chapter 3` |
| Deadline | `deadline DESCRIPTION /by DATE` | `deadline submit report /by 2026-09-20` |
| Event | `event DESCRIPTION /from START_DATE /to END_DATE` | `event orientation /from 2026-09-21 /to 2026-09-22` |

For events, the end date must be later than the start date. Tony will not add a duplicate task with the same type and
details.

## Viewing and finding tasks

- Show every task: `list`
- Find tasks whose descriptions contain a keyword: `find KEYWORD`

For example, enter `find report` to find tasks containing "report". Searches are not case-sensitive.

## Updating tasks

Task numbers refer to the positions shown by `list`.

| Action | Command format | Example |
| --- | --- | --- |
| Mark as complete | `mark TASK_NUMBER` | `mark 2` |
| Mark as incomplete | `unmark TASK_NUMBER` | `unmark 2` |
| Delete tasks | `delete TASK_NUMBER [TASK_NUMBER ...]` | `delete 2 4` |

When deleting several tasks, the numbers may be entered in any order. If a number is invalid or repeated, Tony will
not delete any of the selected tasks.

## Understanding the task list

- `[T]` — To-do
- `[D]` — Deadline
- `[E]` — Event
- `[ ]` — Incomplete
- `[X]` — Complete

## Ending the session

Enter `bye` or select **Bye** to end the conversation. Your latest task changes will already have been saved.
