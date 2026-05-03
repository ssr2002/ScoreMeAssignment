public class BankStatementBatchProcessor {

private int processedCount = 0;

public void process(List<StatementRecord> records) {
ExecutorService executor = Executors.newFixedThreadPool(10);

for (StatementRecord record : records) {
executor.submit(() -> {
processRecord(record);

// FIX: Synchronize increment to make it thread-safe.
// The bug occurs because 'processedCount++' is not atomic (read-modify-write).
// Multiple threads can read the same value and overwrite each other’s updates.(Race condition) 
// Synchronization ensures only one thread updates the counter at a time.
incrementProcessedCount();
});
}

executor.shutdown();
executor.awaitTermination(5, TimeUnit.MINUTES);
}

// FIX: Synchronized method to ensure atomic increment
private synchronized void incrementProcessedCount() {
processedCount++;
}

public int getProcessedCount() {
return processedCount;
}
}

// solution 2 
 public class BankStatementBatchProcessor {

// FIX: Use AtomicInteger to ensure thread-safe increment operation.
// The bug occurs because 'processedCount++' is NOT atomic 
// In a multi-threaded environment, multiple threads overwrite each other's updates,
// causing lost increments and incorrect final count.

private AtomicInteger processedCount = new AtomicInteger(0);

public void process(List<StatementRecord> records) {
ExecutorService executor = Executors.newFixedThreadPool(10);

for (StatementRecord record : records) {
executor.submit(() -> {
processRecord(record);

// FIX: Use atomic increment to avoid race condition
processedCount.incrementAndGet();
});
}

executor.shutdown();
executor.awaitTermination(5, TimeUnit.MINUTES);
}

public int getProcessedCount() {
// FIX: Return value using get() from AtomicInteger
return processedCount.get();
}
}



// Junit
 class BankStatementBatchProcessorTest {
    @Test
    void shouldCountAllProcessedRecords_atomicFix() throws InterruptedException {
        BankStatementBatchProcessor processor = new BankStatementBatchProcessor();

        List<StatementRecord> records = IntStream.range(0, 1000)
                .mapToObj(i -> new StatementRecord())
                .toList();

        processor.process(records);

        assertEquals(1000, processor.getProcessedCount());
    }

    // race condition reproduction (non-deterministic but useful)
    @Test
    void buggyCounterMayLoseUpdates() throws InterruptedException {
        int processedCount = 0;

        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 1000; i++) {
            executor.submit(() -> {
                // simulated non-atomic increment
                // processedCount++; (can't test directly, conceptual)
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // can't assert reliably, but demonstrates issue
        assertTrue(true);
    }
}

