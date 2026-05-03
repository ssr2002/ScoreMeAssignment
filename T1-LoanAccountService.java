public List<LoanAccount> getOverdueLoans(List<LoanAccount> accounts) {
    // FIX: Initialize result list to avoid NullPointerException
    List<LoanAccount> result = new ArrayList<>();

    // FIX: Check if accounts list itself is null
    if (accounts == null) {
        return result;
    }

    Date today = new Date();

    for (LoanAccount account : accounts) {
        // FIX: Skip null account objects
        if (account == null) {
            continue;
        }

        // FIX: Handle null dueDate (avoid NullPointerException)
        if (account.getDueDate() != null && account.getDueDate().before(today)) {

            // FIX: Ensure outstanding balance is strictly greater than zero
            if (account.getOutstandingBalance() > 0) {
                result.add(account);
            }
        }
    }
    return result;
}


class LoanAccountServiceTest {
    private LoanAccountService service = new LoanAccountService();

    @Test
    void shouldReturnEmptyListWhenAccountsNull() {
        List<LoanAccount> result = service.getOverdueLoans(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldIgnoreAccountsWithNullDueDate() {
        LoanAccount acc = new LoanAccount(null, 100.0, "A1");
        List<LoanAccount> result = service.getOverdueLoans(List.of(acc));
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnOnlyOverdueWithPositiveBalance() {
        LoanAccount overdue = new LoanAccount(
                new Date(System.currentTimeMillis() - 10000), 100.0, "A1"
        );
        LoanAccount zeroBalance = new LoanAccount(
                new Date(System.currentTimeMillis() - 10000), 0.0, "A2"
        );
        List<LoanAccount> result = service.getOverdueLoans(List.of(overdue, zeroBalance));
        assertEquals(1, result.size());
        assertEquals("A1", result.get(0).getAccountId());
    }

    @Test
    void shouldNotThrowNPE_whenDueDateNull() {
        LoanAccount acc = new LoanAccount(null, 100.0, "A1");
        assertDoesNotThrow(() -> service.getOverdueLoans(List.of(acc)));
    }
}


