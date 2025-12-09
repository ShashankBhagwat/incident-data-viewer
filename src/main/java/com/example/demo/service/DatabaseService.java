package com.example.demo.service;

import com.example.demo.model.DynamicTableData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DatabaseService {

    private final JdbcTemplate jdbc;


    public DatabaseService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<String> listTables() {
        return jdbc.queryForList(
                "SELECT table_name FROM information_schema.tables " +
                        "WHERE table_schema = 'public' " +
                        "AND table_type = 'BASE TABLE' " +
                        "AND table_name NOT LIKE 'pg_%' " +
                        "ORDER BY table_name",
                String.class
        );

        // H2
//        return jdbc.queryForList(
//                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
//                        "WHERE TABLE_SCHEMA = 'PUBLIC'",
//                String.class
//        );
    }

    public DynamicTableData queryTable(String table) {

        String sql = "SELECT * FROM " + table;

        List<Map<String, Object>> rows = jdbc.queryForList(sql);

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
