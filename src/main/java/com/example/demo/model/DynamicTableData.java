package com.example.demo.model;

import java.util.List;
import java.util.Map;

public record DynamicTableData(
        String tableName,
        List<String> columns,
        List<Map<String, Object>> rows
) {}
