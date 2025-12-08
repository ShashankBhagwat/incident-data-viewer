package com.example.demo.controller;

import com.example.demo.model.DynamicTableData;
import com.example.demo.service.DatabaseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class UIController {

    private final DatabaseService db;

    public UIController(DatabaseService db) {
        this.db = db;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tables", db.listTables());
        return "index";
    }

    @PostMapping("/query")
    public String queryTables(
            @RequestParam(value = "selectedTables", required = false) List<String> selected,
            Model model) {

        if (selected == null || selected.isEmpty()) {
            selected = List.of("ALL");
        }

        List<String> finalTables;

        if (selected.contains("ALL")) {
            finalTables = db.listTables();
        } else {
            finalTables = selected;
        }

        var data = db.queryMultipleTables(finalTables);

        System.out.println("Listed tables : " + db.listTables());
        System.out.println("Result : " + data);

        model.addAttribute("tables", db.listTables());
        model.addAttribute("result", data);

        return "index";
    }

}
