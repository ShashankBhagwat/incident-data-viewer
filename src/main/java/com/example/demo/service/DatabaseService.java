package com.example.demo.service;

import com.example.demo.model.DynamicTableData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataAccessException;

import java.util.*;

@Service
public class DatabaseService {

    private final JdbcTemplate jdbc;

    /**
     * Active profile (e.g. "local" or "aws").
     * We use this to choose correct information_schema query for H2 vs Postgres.
     */
    @Value("${spring.profiles.active:}")
    private String activeProfile;

    public DatabaseService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * List tables. Uses the Postgres information_schema when running in AWS,
     * and the H2-style INFORMATION_SCHEMA when running locally.
     *
     * This method is defensive: if the primary query fails it will try the alternate form.
     */
    public List<String> listTables() {
        // Postgres query (works when connected to Postgres RDS)
        final String pgQuery = "SELECT table_name FROM information_schema.tables " +
                "WHERE table_schema = 'public' " +
                "AND table_type = 'BASE TABLE' " +
                "AND table_name NOT LIKE 'pg_%' " +
                "ORDER BY table_name";

        // H2 query (for local H2 profile)
        final String h2Query = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                "WHERE TABLE_SCHEMA = 'PUBLIC' ORDER BY TABLE_NAME";

        // If explicit local profile, prefer H2 query
        if ("local".equalsIgnoreCase(activeProfile)) {
            try {
                return jdbc.queryForList(h2Query, String.class);
            } catch (DataAccessException e) {
                // fallback to postgres form if H2 query somehow fails
                try {
                    return jdbc.queryForList(pgQuery, String.class);
                } catch (DataAccessException ex) {
                    // final fallback: empty list
                    System.err.println("listTables: both H2 and Postgres queries failed.");
                    ex.printStackTrace();
                    return Collections.emptyList();
                }
            }
        }

        // Default: try Postgres first, if it fails, try H2
        try {
            return jdbc.queryForList(pgQuery, String.class);
        } catch (DataAccessException ex) {
            System.out.println("Postgres table query failed; attempting H2 query as fallback. Error: " + ex.getMessage());
            try {
                return jdbc.queryForList(h2Query, String.class);
            } catch (DataAccessException ex2) {
                System.err.println("Both Postgres and H2 table queries failed.");
                ex2.printStackTrace();
                return Collections.emptyList();
            }
        }
    }

    /**
     * Query all rows from a table. Note: table name is appended directly;
     * ensure callers validate table names or that this method runs against trusted DB.
     */
    public DynamicTableData queryTable(String table) {
        String sql = "SELECT * FROM " + table;

        List<Map<String, Object>> rows = Collections.emptyList();
        try {
            rows = jdbc.queryForList(sql);
        } catch (DataAccessException ex) {
            System.err.println("queryTable failed for table " + table + ". Error: " + ex.getMessage());
            // return empty rows if query failed
        }

        List<String> columns = rows.isEmpty()
                ? Collections.emptyList()
                : new ArrayList<>(rows.get(0).keySet());

        System.out.println("Table: " + table);
        System.out.println("Columns : " + columns);
        System.out.println("Rows  : " + rows);

        return new DynamicTableData(table, columns, rows);
    }

    public List<DynamicTableData> queryMultipleTables(List<String> tables) {
        List<DynamicTableData> result = new ArrayList<>();
        for (String t : tables) {
            result.add(queryTable(t));
        }
        return result;
    }
}
