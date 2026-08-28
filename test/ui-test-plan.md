# UI test plan

## Test case: Add and list all task types

**Aim:** Verifies that to-dos, deadlines, and events are stored and displayed with their type, status, and formatted dates.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/*.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

**Inputs:**
```text
todo borrow book
deadline return book /by 2019-10-15
event project meeting /from 2019-10-16 /to 2019-10-17
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
  [D][ ] return book (by: Oct 15 2019)
Now you have 2 tasks in the list.
________________________________________________
________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Oct 16 2019 to: Oct 17 2019)
Now you have 3 tasks in the list.
________________________________________________
________________________________________________
Nice! I've marked this task as done:
  [T][X] borrow book
________________________________________________
________________________________________________
Here are the tasks in your list:
1.[T][X] borrow book
2.[D][ ] return book (by: Oct 15 2019)
3.[E][ ] project meeting (from: Oct 16 2019 to: Oct 17 2019)
________________________________________________
________________________________________________
Bye. Hope to see you again soon!
________________________________________________
```

## Test case: Reject malformed task commands and unknown commands

**Aim:** Verifies that missing task details and unknown commands show specific error messages without adding or changing tasks; valid commands between the errors still work.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/*.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

**Inputs:**
```text
todo
todo write report
deadline
list
deadline submit form /by 2019-10-18
event meeting /from 2pm
event standup /from 2019-10-19 /to 2019-10-20
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
Oops: A deadline needs a description and a due date. Use: deadline <task> /by <yyyy-MM-dd>
________________________________________________
________________________________________________
Here are the tasks in your list:
1.[T][ ] write report
________________________________________________
________________________________________________
Got it. I've added this task:
  [D][ ] submit form (by: Oct 18 2019)
Now you have 2 tasks in the list.
________________________________________________
________________________________________________
Oops: An event needs a description, start date, and end date. Use: event <task> /from <yyyy-MM-dd> /to <yyyy-MM-dd>
________________________________________________
________________________________________________
Got it. I've added this task:
  [E][ ] standup (from: Oct 19 2019 to: Oct 20 2019)
Now you have 3 tasks in the list.
________________________________________________
________________________________________________
Oops: I don't recognize that command. Try todo, deadline, event, list, find, mark, unmark, delete, or bye.
________________________________________________
________________________________________________
Here are the tasks in your list:
1.[T][ ] write report
2.[D][ ] submit form (by: Oct 18 2019)
3.[E][ ] standup (from: Oct 19 2019 to: Oct 20 2019)
________________________________________________
________________________________________________
Bye. Hope to see you again soon!
________________________________________________
```

## Test case: Find tasks by description keyword

**Aim:** Verifies that `find` searches descriptions without regard to letter case, preserves match order and status, excludes non-matches, handles no results, and rejects a missing keyword.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/*.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

**Inputs:**
```text
todo Read Book
deadline return book /by 2019-06-06
todo write report
mark 1
mark 2
find book
find REPORT
find calendar
find
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
  [T][ ] Read Book
Now you have 1 task in the list.
________________________________________________
________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
________________________________________________
________________________________________________
Got it. I've added this task:
  [T][ ] write report
Now you have 3 tasks in the list.
________________________________________________
________________________________________________
Nice! I've marked this task as done:
  [T][X] Read Book
________________________________________________
________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: Jun 06 2019)
________________________________________________
________________________________________________
Here are the matching tasks in your list:
1.[T][X] Read Book
2.[D][X] return book (by: Jun 06 2019)
________________________________________________
________________________________________________
Here are the matching tasks in your list:
1.[T][ ] write report
________________________________________________
________________________________________________
Here are the matching tasks in your list:
________________________________________________
________________________________________________
Oops: Please provide a keyword to find.
________________________________________________
________________________________________________
Bye. Hope to see you again soon!
________________________________________________
```

## Test case: Delete a task and renumber the remaining tasks

**Aim:** Verifies that `delete` removes the requested task, displays its details and the updated task count, and leaves the remaining tasks consecutively numbered.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/*.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

**Inputs:**
```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-07
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
  [D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
________________________________________________
________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
Now you have 3 tasks in the list.
________________________________________________
________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 4 tasks in the list.
________________________________________________
________________________________________________
Noted. I've removed this task:
  [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
Now you have 3 tasks in the list.
________________________________________________
________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 06 2019)
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

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/*.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

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

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/*.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

**Inputs:**
```text
todo read book
deadline return book /by 2019-06-06
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
  [D][ ] return book (by: Jun 06 2019)
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
1.[D][ ] return book (by: Jun 06 2019)
________________________________________________
________________________________________________
Noted. I've removed this task:
  [D][ ] return book (by: Jun 06 2019)
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

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/*.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

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

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/*.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

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

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/*.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony && printf "Saved file:\\n" && sed -n "1,20p" data/tony.txt'`

**Inputs:**
```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-07
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
  [D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
________________________________________________
________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
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
  [D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
________________________________________________
________________________________________________
Bye. Hope to see you again soon!
________________________________________________
Saved file:
T | 0 | read book
E | 0 | project meeting | 2019-08-06 | 2019-08-07
```

## Test case: Load saved tasks when the chatbot starts

**Aim:** Verifies that a saved to-do, deadline, and event are reconstructed with their descriptions, date fields, and completion states on startup.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && printf "T | 1 | read book\\nD | 0 | return book | 2019-06-06\\nE | 1 | project meeting | 2019-08-06 | 2019-08-07\\n" > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/*.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

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
2.[D][ ] return book (by: Jun 06 2019)
3.[E][X] project meeting (from: Aug 06 2019 to: Aug 07 2019)
________________________________________________
________________________________________________
Bye. Hope to see you again soon!
________________________________________________
```

## Test case: Start normally when the data file is missing

**Aim:** Verifies that first-time startup treats a missing data file as an empty task list without displaying an error.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && rm -f data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/*.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

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

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && printf "%s\\n" "T | 1 | valid task" "" "T | 2 | bad status" "X | 0 | unknown type" "D | 0 | missing due date" "E | 0 | too many | start | end | extra" "T | 0 | " "D | 0 | impossible date | 2019-02-30" "E | 0 | backwards | 2019-10-20 | 2019-10-19" "D | 0 | return book | 2019-10-18" > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/*.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

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
Warning: I skipped 7 lines in the data file because they were invalid.
________________________________________________
Here are the tasks in your list:
1.[T][X] valid task
2.[D][ ] return book (by: Oct 18 2019)
________________________________________________
________________________________________________
Bye. Hope to see you again soon!
________________________________________________
```

## Test case: Parse valid dates and reject invalid dates

**Aim:** Verifies strict `yyyy-MM-dd` parsing, leap-day support, formatted date display, and rejection of event ranges whose end precedes their start.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/*.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

**Inputs:**
```text
deadline invalid leap day /by 2019-02-29
deadline wrong format /by 15-10-2019
event impossible date /from 2019-02-28 /to 2019-02-30
event backwards /from 2019-10-20 /to 2019-10-19
deadline valid leap day /by 2020-02-29
event one-day workshop /from 2019-10-15 /to 2019-10-15
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
Oops: Please enter dates as yyyy-MM-dd (for example, 2019-10-15).
________________________________________________
________________________________________________
Oops: Please enter dates as yyyy-MM-dd (for example, 2019-10-15).
________________________________________________
________________________________________________
Oops: Please enter dates as yyyy-MM-dd (for example, 2019-10-15).
________________________________________________
________________________________________________
Oops: An event's end date cannot be before its start date.
________________________________________________
________________________________________________
Got it. I've added this task:
  [D][ ] valid leap day (by: Feb 29 2020)
Now you have 1 task in the list.
________________________________________________
________________________________________________
Got it. I've added this task:
  [E][ ] one-day workshop (from: Oct 15 2019 to: Oct 15 2019)
Now you have 2 tasks in the list.
________________________________________________
________________________________________________
Here are the tasks in your list:
1.[D][ ] valid leap day (by: Feb 29 2020)
2.[E][ ] one-day workshop (from: Oct 15 2019 to: Oct 15 2019)
________________________________________________
________________________________________________
Bye. Hope to see you again soon!
________________________________________________
```

## Test case: Continue after data file read and write errors

**Aim:** Verifies that an unreadable storage target starts with an empty list and that a later save failure keeps the chatbot session usable.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && rm -f data/tony.txt && mkdir data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/*.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony && rmdir data/tony.txt'`

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

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/*.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony && printf "Saved file:\\n" && sed -n "1,20p" data/tony.txt'`

**Inputs:**
```text
todo compare A | B
deadline review C:\notes | archive /by 2019-10-18
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
  [D][ ] review C:\notes | archive (by: Oct 18 2019)
Now you have 2 tasks in the list.
________________________________________________
________________________________________________
Bye. Hope to see you again soon!
________________________________________________
Saved file:
T | 0 | compare A \| B
D | 0 | review C:\\notes \| archive | 2019-10-18
```

## Test case: Load escaped storage fields

**Aim:** Verifies that escaped pipe and backslash characters are decoded when saved tasks are loaded.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && printf "%s\\n" "T | 1 | compare A \\| B" "D | 0 | review C:\\\\notes \\| archive | 2019-10-18" > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/*.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

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
2.[D][ ] review C:\notes | archive (by: Oct 18 2019)
________________________________________________
________________________________________________
Bye. Hope to see you again soon!
________________________________________________
```
