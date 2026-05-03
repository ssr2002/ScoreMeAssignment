/* 

1. What is the exact cause of ConcurrentModificationException in Java? - ConcurrentModificationException occurs when a collection is structurally modified while it is being iterated, except through the iterator’s own safe methods.


2. What code pattern at line 142 most likely triggered this error? - 
The error shows:
ArrayList$Itr.next() → iteration in progress
filterTransactions() → modification happening during iteration
for (Transaction txn : transactions) {
    if (condition) {
        transactions.remove(txn); // ConcurrentModificationException
    }
}


3. Provide the minimal code change (one or two lines) that resolves this safely.- Use the iterator’s remove() method instead of modifying the list directly.

Iterator<Transaction> it = transactions.iterator();
while (it.hasNext()) {
    if (condition) it.remove(); // safe removal
}

*/

class StatementProcessorServiceTest {
    @Test
    void shouldThrowConcurrentModificationException_inBuggyCode() {
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3));
        assertThrows(ConcurrentModificationException.class, () -> {
            for (Integer i : list) {
                list.remove(i); // buggy
            }
        });
    }

    @Test
    void shouldNotThrow_whenUsingIteratorRemove() {
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3));
        assertDoesNotThrow(() -> {
            Iterator<Integer> it = list.iterator();
            while (it.hasNext()) {
                it.next();
                it.remove(); // fixed
            }
        });
        assertTrue(list.isEmpty());
    }
}


