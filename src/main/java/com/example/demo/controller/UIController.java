package com.example.demo.controller;

import com.example.demo.service.DatabaseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Controller
public class UIController {

    private final DatabaseService db;

    public UIController(DatabaseService db) {
        this.db = db;
    }

    @GetMapping("/ping")
    @ResponseBody
    public String ping() {
        return "✅ LAMBDA MVC OUTPUT IS WORKING";
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tables", db.listTables());
        return "index";
    }

    @PostMapping("/query")
    public String queryTables(
            HttpServletRequest request,
            Model model) {

        String[] selected = request.getParameterValues("selectedTables");

        List<String> finalTables;

        if (selected == null || selected.length == 0) {
            // ✅ No selection sent → backend default to all
            finalTables = db.listTables();
        } else {
            finalTables = Arrays.asList(selected);
        }

        var data = db.queryMultipleTables(finalTables);

        System.out.println("Selected (RAW)      : " + Arrays.toString(selected));
        System.out.println("Final tables used  : " + finalTables);

        model.addAttribute("tables", db.listTables());
        model.addAttribute("result", data);

        if (selected != null && selected.length > 0) {
            model.addAttribute("selectedTables", finalTables);
        }

        return "index";
    }


}
