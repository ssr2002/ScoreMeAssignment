public class ReportDAO {
    private DataSource dataSource;

    public List<ReportEntry> fetchMonthlyReport(String accountId, int month, int year) throws SQLException {
        // FIX: Use try-with-resources to automatically close Connection, PreparedStatement, and ResultSet.
        // The bug is a connection leak — resources were never closed, causing pool exhaustion over time.
        // try-with-resources ensures proper closure in reverse order: ResultSet → PreparedStatement → Connection.

        List<ReportEntry> entries = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM report_entries " +
                "WHERE account_id = ? AND MONTH(entry_date) = ? " +
                "AND YEAR(entry_date) = ?"
             )) {

            ps.setString(1, accountId);
            ps.setInt(2, month);
            ps.setInt(3, year);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    entries.add(mapRow(rs));
                }
            }
        }
        return entries;
    }
}
