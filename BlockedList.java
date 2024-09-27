package comp2402a2;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author morin
 *
 * @param <T> the type of objects stored in the List
 */
public class BlockedList<T> extends AbstractList<T> {
	/**
	 * keeps track of the class of objects we store
	 */
	Factory<T> f;

	/**
	 * The number of elements stored
	 */
	int n;

	/**
	 * The block size
	 */
	int b;

	ArrayDeque<ArrayDeque<T>> blocks;

	/**
	 * Constructor
	 * @param t the type of objects that are stored in this list
	 * @param b the block size
	 */
	public BlockedList(Class<T> t, int b) {
		f = new Factory<T>(t);
		n = 0;
		this.b = b;
		blocks = new ArrayDeque<ArrayDeque<T>>((Class<ArrayDeque<T>>)(new ArrayDeque<T>(t)).getClass());
		// TODO: Implement this
	}

	public int size() {
		return n;
	}

	public T get(int i) {
		// TODO: Implement this
		// Check if the index is out of bounds
		if (i < 0 || i > n - 1) throw new IndexOutOfBoundsException();

		// Checking if index is in the first ArrayDeque or in proceeding ArrayDeques
		if (i < blocks.get(0).size()) { // in first ArrayDeque
			// Get the element at the first ArrayDeque's i which is equivalent to the BlockList's i (if i is in first ArrayDeque)
			return blocks.get(0).get(i);
		}
		else { // in proceeding ArrayDeque
			// Return the element at the calculated position
			return getBlockFromItemIndex(i).get((i - blocks.get(0).size())%b);
		}
	}

	// Method to get the ArrayDeque holding the ith element
	private ArrayDeque<T> getBlockFromItemIndex(int i) {
		if (i < blocks.get(0).size())
			return blocks.get(0);
		else
			return blocks.get((i - blocks.get(0).size())/b+1);
	}

	public T set(int i, T x) {
		// TODO: Implement this
		if (i < 0 || i > n - 1) throw new IndexOutOfBoundsException();

		// Checking if index is in the first ArrayDeque or in proceeding ArrayDeques
		if (i < blocks.get(0).size()) { // in first ArrayDeque
			// Set the element at the first ArrayDeque's i which is equivalent to the BlockList's i (if i is in first ArrayDeque)
			return blocks.get(0).set(i, x);
		}
		else { // in proceeding ArrayDeque
			// Return the element at the calculated position before setting the new value
			return getBlockFromItemIndex(i).set((i - blocks.get(0).size())%b, x);
		}
	}

	public void add(int i, T x) {
		// TODO: Implement this
		if (i < 0 || i > n) throw new IndexOutOfBoundsException();

		// Check to see if there are no blocks in block list yet
		if (blocks.isEmpty()) {
			// Create a new block
			ArrayDeque<T> newDeque = new ArrayDeque<>(f.type());
			// Add x to new block
			newDeque.add(x);
			// Add new block to block list
			blocks.add(0,newDeque);

			// Updating n since all scenarios lead to the addition of an element
			n++;

			// End method
			return;
		}

		// Find current block index
		int currBlockIndex = itemIndexToBlockIndex(i);

		// Check to see if i is in the first block
		if (i < blocks.get(0).size()){ // i is in first block
			// Adding element in correct spot
			currBlockIndex = 0;
			blocks.get(0).add(i, x);
		}
		else { // i not in first block
			// Check to see if new block is needed
			if (currBlockIndex == blocks.size()) {
				// Adding new block to list
				blocks.add(blocks.size(),new ArrayDeque<>(f.type()));
			}
			// Adding element in correct spot
			blocks.get(currBlockIndex).add((i - blocks.get(0).size())%b, x);
		}

		// Updating n since all scenarios lead to the addition of an element
		n++;

		// Balancing list when needed
		if (blocks.get(currBlockIndex).size() > b) { // Current block has more than b elements
			if (i <= n/2) // Left end of list is closer to current block
				shiftLeft(currBlockIndex);
			else // right end of list is closer to current block
				shiftRight(currBlockIndex);
		}
	}

	private int itemIndexToBlockIndex(int i) {
		// Checking if index is in the first ArrayDeque or in proceeding ArrayDeques
		if (i < blocks.get(0).size()) { // in first ArrayDeque
			// returning 0 as element is in first array
			return 0;
		}
		else { // in proceeding ArrayDeque
			// Return the index of black containing i
			return (i - blocks.get(0).size())/b+1;
		}
	}

	private void shiftLeft(int blockIndex) {
		// Get current block
		ArrayDeque<T> curr = blocks.get(blockIndex);

		while(curr.size() > b) {
			// See if current block is the first block in list (i.e. no block to the left)
			if (blockIndex == 0) { // Current block is the first block in list
				// Create new block
				ArrayDeque<T> newDeque = new ArrayDeque<>(f.type());
				// Add element that will move to the left to the new block
				newDeque.add(curr.remove(0));
				// Add new block to the beginning of list
				blocks.add(0,newDeque);
			} else { // Current block is not the first block
				// Get block to the left
				ArrayDeque<T> prevBlock = blocks.get(blockIndex-1);
				// Add the element that will shift to the end of previous block
				prevBlock.add(prevBlock.size(), curr.remove(0));

				curr = prevBlock;
				blockIndex--;
			}
		}
	}

	private void shiftRight(int blockIndex) {
		// Get current block
		ArrayDeque<T> curr = blocks.get(blockIndex);

		while (curr.size() > b) {
			// Checking to see if current block is the last block (i.e. no block to the right)
			if (blockIndex == blocks.size()-1) { // Current block is the last block
				// Create new block
				ArrayDeque<T> newDeque = new ArrayDeque<>(f.type());
				// Add element that will move to the right to new block
				newDeque.add(curr.remove(curr.size()-1));
				// Add new block to the end of list
				blocks.add(blocks.size(), newDeque);
			} else { // Current block is not the last block
				// Getting block to the right of current block
				ArrayDeque<T> nextBlock = blocks.get(blockIndex+1);
				// Add the element that will shift to the right to the beginning of next block
				nextBlock.add(0, curr.remove(curr.size()-1));

				curr = nextBlock;
				blockIndex++;
			}
		}
	}

	public T remove(int i) {
		// TODO: Implement this
		if (i < 0 || i > n - 1) throw new IndexOutOfBoundsException();

		// If statement to check if index is in first block
		if (i < blocks.get(0).size()) { // index is in first block
			// Removing element and storing it
			T removed = blocks.get(0).remove(i);

			// Check to see if due to the removal of the element, the block is empty
			if (blocks.get(0).isEmpty()){
				// Remove empty block
				blocks.remove(0);
			}
			// Decrease n
			n--;

			// Return removed element
			return removed;
		}
		else { // index is not in first block
			// Get the index of block containing index i
			int currBlockIndex = itemIndexToBlockIndex(i);

			// Removing item and storing it
			T removed = blocks.get(currBlockIndex).remove((i-blocks.get(0).size())%b);

			// Decreasing n
			n--;

			// See which tail is closer to i
			if (i <= n/2) // Left end of list is closer to current block
				bringFromLeft(currBlockIndex);
			else // right end of list is closer to current block
				bringFromRight(currBlockIndex);

			// Check and see if current block is empty
			if (blocks.size() > currBlockIndex && blocks.get(currBlockIndex).isEmpty()){
				// Remove current block if it is empty
				blocks.remove(currBlockIndex);
			}

			// Return removed item
			return removed;
		}
	}

	private void bringFromLeft(int blockIndex) {
		// Get current block
		ArrayDeque<T> curr = blocks.get(blockIndex);

		// See if current block is the first block in list (i.e. no block to the left)
		while (blockIndex > 0) { // Current block is not the first block in list
			// Get block to the left
			ArrayDeque<T> prevBlock = blocks.get(blockIndex-1);
			// Add the element to current block from previous block
			curr.add(0, prevBlock.remove(prevBlock.size()-1));

			// See if previous block is the first block in list (i.e. no block to the left)
			if (blockIndex == 1) { // Previous block is the first
				// Since the previous block is the first, it may be empty, so check if it is empty
				if (prevBlock.isEmpty()) {
					// Remove previous block from blocks list
					blocks.remove(0);
				}
			}

			curr = prevBlock;
			blockIndex--;
		}
	}

	private void bringFromRight(int blockIndex) {
		// Get current block
		ArrayDeque<T> curr = blocks.get(blockIndex);

		// See if current block is the last block in list (i.e. no block to the right)
		while (blockIndex < blocks.size()-1) { // Current block is not the last block in list
			// Get block to the right
			ArrayDeque<T> nextBlock = blocks.get(blockIndex+1);
			// Add the element that will shift to the end of previous block
			curr.add(curr.size(),nextBlock.remove(0));

			// See if next block is the last block in list (i.e. no block to the right)
			if (blockIndex == blocks.size()-2) { // Previous block is the last
				// Since the next block is the last, it may be empty, so check if it is empty
				if (nextBlock.isEmpty()) {
					// Remove next block from blocks list
					blocks.remove(blockIndex+1);
				}
			}
			curr = nextBlock;
			blockIndex++;
		}
	}

	public String toString(){
		StringBuilder s = new StringBuilder();

		for (ArrayDeque<T> block:blocks) {
			s.append("{ ");

			Iterator<T> iterator = block.iterator();

			if (iterator.hasNext()) {
				s.append(iterator.next());
			}
			while (iterator.hasNext()) {
				s.append(" | ").append(iterator.next());
			}
			s.append(" }");
		}

		if (blocks.isEmpty()){
			return "{ }";
		}

		return s.toString();
	}
}
