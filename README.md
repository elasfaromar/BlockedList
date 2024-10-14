# BlockedList Project

## Overview

The BlockedList project implements a custom data structure called `BlockedList`, which utilizes an underlying `ArrayDeque` structure to manage its elements in blocks. This approach provides efficient storage and manipulation of elements, particularly for operations that involve adding, removing, or accessing elements at arbitrary positions.

## Features

- **Dynamic Sizing**: Automatically adjusts the size of blocks as elements are added or removed.
- **Efficient Access**: Provides O(1) time complexity for accessing elements by index.
- **Flexible Block Size**: Allows the user to specify the size of blocks for optimized storage.

## Components

### 1. BlockedList.java

This class implements a blocked list using an abstract list. Key methods include:
- `size()`: Returns the number of elements in the list.
- `get(int i)`: Retrieves the element at the specified index.
- `set(int i, T x)`: Replaces the element at the specified index with the provided value.
- `add(int i, T x)`: Inserts the specified element at the given index.
- `remove(int i)`: Removes the element at the specified index.

### 2. ArrayDeque.java

This class provides a double-ended queue implementation. It supports adding and removing elements from both ends of the deque efficiently. The `ArrayDeque` is used as the underlying storage mechanism for the `BlockedList`.

### 3. Factory.java

The `Factory` class is responsible for creating instances of objects stored in the `BlockedList`. This generic class helps maintain type safety and ensures that the appropriate type of object is created based on user specifications.

## Usage

To use the `BlockedList`, you can create an instance of it with the desired element type and block size. Here’s an example:

```java
BlockedList<Integer> blockedList = new BlockedList<>(Integer.class, 5);
blockedList.add(0, 10);
blockedList.add(1, 20);
System.out.println(blockedList.get(0)); // Outputs: 10
```

## Installation

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd <repository-directory>
   ```

2. Compile the Java files:
   ```bash
   javac *.java
   ```

3. Run your tests or main application as needed.

## Contributing

Contributions are welcome! If you find any issues or want to suggest improvements, feel free to open an issue or submit a pull request.

## License

This project is licensed under the MIT License. See the LICENSE file for more details.
