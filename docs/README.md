# Tony User Guide

// Update the title above to match the actual product name

// Product screenshot goes here

// Product intro goes here

## Adding deadlines

// Describe the action and its outcome.

// Give examples of usage

Example: `keyword (optional arguments)`

// A description of the expected outcome goes here

```
expected output
```

## Deleting multiple tasks

Use `delete` followed by one or more task numbers separated by spaces. Each number refers to the task's position
before the command is performed, so deleting several tasks does not cause later numbers in the same command to
shift.

For example:

`delete 2 4`

```text
Noted. I've removed these tasks:
  [D][X] submit report (by: Sep 20 2026)
  [T][ ] buy groceries
Now you have 2 tasks in the list.
```

The numbers may be entered in any order. Tony displays the removed tasks in their original list order. Every number
must identify an existing task, and a number cannot be repeated. If any number is invalid, Tony does not delete any
of the selected tasks.

Single-task deletion continues to use the existing syntax, such as `delete 2`.

## Feature ABC

// Feature details


## Feature XYZ

// Feature details
