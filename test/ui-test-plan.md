# UI test plan

## Test case: Add and list all task types

**Aim:** Verifies that to-dos, deadlines, and events are stored and displayed with their type, status, and date/time text.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/*.java && java -cp /private/tmp/tony-ui-classes Tony'`

**Inputs:**
```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
mark 1
list
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
What can I do for you?
________________________________________________
________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
________________________________________________
________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
________________________________________________
________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
________________________________________________
________________________________________________
Nice! I've marked this task as done:
  [T][X] borrow book
________________________________________________
________________________________________________
Here are the tasks in your list:
1.[T][X] borrow book
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
________________________________________________
________________________________________________
Bye. Hope to see you again soon!
________________________________________________
```

## Test case: Reject malformed task commands and unknown commands

**Aim:** Verifies that missing task details and unknown commands show specific error messages without adding or changing tasks; valid commands between the errors still work.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/*.java && java -cp /private/tmp/tony-ui-classes Tony'`

**Inputs:**
```text
todo
todo write report
deadline
list
deadline submit form /by Friday
event meeting /from 2pm
event standup /from 9am /to 10am
blah
list
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
What can I do for you?
________________________________________________
________________________________________________
Oops: A to-do needs a description. For example: todo read chapter 3
________________________________________________
________________________________________________
Got it. I've added this task:
  [T][ ] write report
Now you have 1 task in the list.
________________________________________________
________________________________________________
Oops: A deadline needs a description and a due time. Use: deadline <task> /by <time>
________________________________________________
________________________________________________
Here are the tasks in your list:
1.[T][ ] write report
________________________________________________
________________________________________________
Got it. I've added this task:
  [D][ ] submit form (by: Friday)
Now you have 2 tasks in the list.
________________________________________________
________________________________________________
Oops: An event needs a description, start, and end. Use: event <task> /from <start> /to <end>
________________________________________________
________________________________________________
Got it. I've added this task:
  [E][ ] standup (from: 9am to: 10am)
Now you have 3 tasks in the list.
________________________________________________
________________________________________________
Oops: I don't recognize that command. Try todo, deadline, event, list, mark, unmark, delete, or bye.
________________________________________________
________________________________________________
Here are the tasks in your list:
1.[T][ ] write report
2.[D][ ] submit form (by: Friday)
3.[E][ ] standup (from: 9am to: 10am)
________________________________________________
________________________________________________
Bye. Hope to see you again soon!
________________________________________________
```

## Test case: Delete a task and renumber the remaining tasks

**Aim:** Verifies that `delete` removes the requested task, displays its details and the updated task count, and leaves the remaining tasks consecutively numbered.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/*.java && java -cp /private/tmp/tony-ui-classes Tony'`

**Inputs:**
```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
todo borrow book
delete 3
list
delete 8
delete three
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
What can I do for you?
________________________________________________
________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
________________________________________________
________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: June 6th)
Now you have 2 tasks in the list.
________________________________________________
________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 3 tasks in the list.
________________________________________________
________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 4 tasks in the list.
________________________________________________
________________________________________________
Noted. I've removed this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 3 tasks in the list.
________________________________________________
________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: June 6th)
3.[T][ ] borrow book
________________________________________________
________________________________________________
Oops: That task number is not in your list.
________________________________________________
________________________________________________
Oops: Please provide a whole-number task number to delete.
________________________________________________
________________________________________________
Bye. Hope to see you again soon!
________________________________________________
```

## Test case: Reject deletion from an empty list and invalid delete commands

**Aim:** Verifies that an empty list can be displayed, deletion from it is rejected, and missing, out-of-range, and non-numeric delete inputs do not change an existing task.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/*.java && java -cp /private/tmp/tony-ui-classes Tony'`

**Inputs:**
```text
list
delete 1
todo revise notes
delete
list
delete 2
list
delete one
list
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
What can I do for you?
________________________________________________
________________________________________________
Here are the tasks in your list:
________________________________________________
________________________________________________
Oops: That task number is not in your list.
________________________________________________
________________________________________________
Got it. I've added this task:
  [T][ ] revise notes
Now you have 1 task in the list.
________________________________________________
________________________________________________
Oops: Please provide a task number to delete.
________________________________________________
________________________________________________
Here are the tasks in your list:
1.[T][ ] revise notes
________________________________________________
________________________________________________
Oops: That task number is not in your list.
________________________________________________
________________________________________________
Here are the tasks in your list:
1.[T][ ] revise notes
________________________________________________
________________________________________________
Oops: Please provide a whole-number task number to delete.
________________________________________________
________________________________________________
Here are the tasks in your list:
1.[T][ ] revise notes
________________________________________________
________________________________________________
Bye. Hope to see you again soon!
________________________________________________
```

## Test case: Delete completed first and last tasks

**Aim:** Verifies that deletion works at both list boundaries and that its confirmation retains the removed task's completion status.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/*.java && java -cp /private/tmp/tony-ui-classes Tony'`

**Inputs:**
```text
todo read book
deadline return book /by June 6th
mark 1
delete 1
list
delete 1
list
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
What can I do for you?
________________________________________________
________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
________________________________________________
________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: June 6th)
Now you have 2 tasks in the list.
________________________________________________
________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
________________________________________________
________________________________________________
Noted. I've removed this task:
  [T][X] read book
Now you have 1 task in the list.
________________________________________________
________________________________________________
Here are the tasks in your list:
1.[D][ ] return book (by: June 6th)
________________________________________________
________________________________________________
Noted. I've removed this task:
  [D][ ] return book (by: June 6th)
Now you have 0 tasks in the list.
________________________________________________
________________________________________________
Here are the tasks in your list:
________________________________________________
________________________________________________
Bye. Hope to see you again soon!
________________________________________________
```

## Test case: Reject invalid mark commands without changing task state

**Aim:** Verifies that invalid task numbers and non-numeric `mark` inputs are rejected, and that each subsequent list still shows the original incomplete task until a valid mark command succeeds.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/*.java && java -cp /private/tmp/tony-ui-classes Tony'`

**Inputs:**
```text
todo review notes
mark 0
list
mark one
list
mark 2
list
mark 1
list
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
What can I do for you?
________________________________________________
________________________________________________
Got it. I've added this task:
  [T][ ] review notes
Now you have 1 task in the list.
________________________________________________
________________________________________________
Oops: That task number is not in your list.
________________________________________________
________________________________________________
Here are the tasks in your list:
1.[T][ ] review notes
________________________________________________
________________________________________________
Oops: Please provide a whole-number task number to mark.
________________________________________________
________________________________________________
Here are the tasks in your list:
1.[T][ ] review notes
________________________________________________
________________________________________________
Oops: That task number is not in your list.
________________________________________________
________________________________________________
Here are the tasks in your list:
1.[T][ ] review notes
________________________________________________
________________________________________________
Nice! I've marked this task as done:
  [T][X] review notes
________________________________________________
________________________________________________
Here are the tasks in your list:
1.[T][X] review notes
________________________________________________
________________________________________________
Bye. Hope to see you again soon!
________________________________________________
```

## Test case: Reject invalid unmark commands without changing task state

**Aim:** Verifies that malformed and out-of-range `unmark` commands do not undo a completed task, while a later valid command does.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/*.java && java -cp /private/tmp/tony-ui-classes Tony'`

**Inputs:**
```text
todo submit assignment
mark 1
unmark -1
list
unmark soon
list
unmark 2
list
unmark 1
list
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
What can I do for you?
________________________________________________
________________________________________________
Got it. I've added this task:
  [T][ ] submit assignment
Now you have 1 task in the list.
________________________________________________
________________________________________________
Nice! I've marked this task as done:
  [T][X] submit assignment
________________________________________________
________________________________________________
Oops: That task number is not in your list.
________________________________________________
________________________________________________
Here are the tasks in your list:
1.[T][X] submit assignment
________________________________________________
________________________________________________
Oops: Please provide a whole-number task number to unmark.
________________________________________________
________________________________________________
Here are the tasks in your list:
1.[T][X] submit assignment
________________________________________________
________________________________________________
Oops: That task number is not in your list.
________________________________________________
________________________________________________
Here are the tasks in your list:
1.[T][X] submit assignment
________________________________________________
________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] submit assignment
________________________________________________
________________________________________________
Here are the tasks in your list:
1.[T][ ] submit assignment
________________________________________________
________________________________________________
Bye. Hope to see you again soon!
________________________________________________
```

## Test case: Save the latest task list after every change

**Aim:** Verifies that adding each task type, marking, unmarking, and deleting result in a data file that represents the latest task list.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/*.java && java -cp /private/tmp/tony-ui-classes Tony && printf "Saved file:\\n" && sed -n "1,20p" data/tony.txt'`

**Inputs:**
```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
mark 1
unmark 1
delete 2
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
What can I do for you?
________________________________________________
________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
________________________________________________
________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: June 6th)
Now you have 2 tasks in the list.
________________________________________________
________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 3 tasks in the list.
________________________________________________
________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
________________________________________________
________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] read book
________________________________________________
________________________________________________
Noted. I've removed this task:
  [D][ ] return book (by: June 6th)
Now you have 2 tasks in the list.
________________________________________________
________________________________________________
Bye. Hope to see you again soon!
________________________________________________
Saved file:
T | 0 | read book
E | 0 | project meeting | Aug 6th 2pm | 4pm
```

## Test case: Load saved tasks when the chatbot starts

**Aim:** Verifies that a saved to-do, deadline, and event are reconstructed with their descriptions, date/time fields, and completion states on startup.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && printf "T | 1 | read book\\nD | 0 | return book | June 6th\\nE | 1 | project meeting | Aug 6th 2pm | 4pm\\n" > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/*.java && java -cp /private/tmp/tony-ui-classes Tony'`

**Inputs:**
```text
list
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
What can I do for you?
________________________________________________
________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: June 6th)
3.[E][X] project meeting (from: Aug 6th 2pm to: 4pm)
________________________________________________
________________________________________________
Bye. Hope to see you again soon!
________________________________________________
```

## Test case: Start normally when the data file is missing

**Aim:** Verifies that first-time startup treats a missing data file as an empty task list without displaying an error.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && rm -f data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/*.java && java -cp /private/tmp/tony-ui-classes Tony'`

**Inputs:**
```text
list
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
What can I do for you?
________________________________________________
________________________________________________
Here are the tasks in your list:
________________________________________________
________________________________________________
Bye. Hope to see you again soon!
________________________________________________
```

## Test case: Recover valid tasks from a partly corrupted data file

**Aim:** Verifies that blank lines are ignored, malformed records are skipped with one warning, and valid records still load.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && printf "%s\\n" "T | 1 | valid task" "" "T | 2 | bad status" "X | 0 | unknown type" "D | 0 | missing due time" "E | 0 | too many | start | end | extra" "T | 0 | " "D | 0 | return book | Friday" > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/*.java && java -cp /private/tmp/tony-ui-classes Tony'`

**Inputs:**
```text
list
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
What can I do for you?
________________________________________________
Warning: I skipped 5 lines in the data file because they were invalid.
________________________________________________
Here are the tasks in your list:
1.[T][X] valid task
2.[D][ ] return book (by: Friday)
________________________________________________
________________________________________________
Bye. Hope to see you again soon!
________________________________________________
```

## Test case: Continue after data file read and write errors

**Aim:** Verifies that an unreadable storage target starts with an empty list and that a later save failure keeps the chatbot session usable.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && rm -f data/tony.txt && mkdir data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/*.java && java -cp /private/tmp/tony-ui-classes Tony && rmdir data/tony.txt'`

**Inputs:**
```text
todo session-only task
list
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
What can I do for you?
________________________________________________
Warning: I couldn't read the data file. Starting with an empty task list.
________________________________________________
Got it. I've added this task:
  [T][ ] session-only task
Now you have 1 task in the list.
Warning: I couldn't save your tasks. Your latest changes are only in this session.
________________________________________________
________________________________________________
Here are the tasks in your list:
1.[T][ ] session-only task
________________________________________________
________________________________________________
Bye. Hope to see you again soon!
________________________________________________
```

## Test case: Escape storage separator and backslash characters

**Aim:** Verifies that pipe and backslash characters in task fields are escaped when saved instead of corrupting the storage format.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/*.java && java -cp /private/tmp/tony-ui-classes Tony && printf "Saved file:\\n" && sed -n "1,20p" data/tony.txt'`

**Inputs:**
```text
todo compare A | B
deadline review C:\notes /by Fri | Sat
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
What can I do for you?
________________________________________________
________________________________________________
Got it. I've added this task:
  [T][ ] compare A | B
Now you have 1 task in the list.
________________________________________________
________________________________________________
Got it. I've added this task:
  [D][ ] review C:\notes (by: Fri | Sat)
Now you have 2 tasks in the list.
________________________________________________
________________________________________________
Bye. Hope to see you again soon!
________________________________________________
Saved file:
T | 0 | compare A \| B
D | 0 | review C:\\notes | Fri \| Sat
```

## Test case: Load escaped storage fields

**Aim:** Verifies that escaped pipe and backslash characters are decoded when saved tasks are loaded.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && printf "%s\\n" "T | 1 | compare A \\| B" "D | 0 | review C:\\\\notes | Fri \\| Sat" > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/*.java && java -cp /private/tmp/tony-ui-classes Tony'`

**Inputs:**
```text
list
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
What can I do for you?
________________________________________________
________________________________________________
Here are the tasks in your list:
1.[T][X] compare A | B
2.[D][ ] review C:\notes (by: Fri | Sat)
________________________________________________
________________________________________________
Bye. Hope to see you again soon!
________________________________________________
```
