package sg.com.aori.controller;

/**
 * ViewHistoryController for handling HTTP requests related to product view history.
 * 
 * @author Yunhe
 * @date 2025-10-12
 * @version 1.0
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sg.com.aori.model.ViewHistory;
import sg.com.aori.service.HistoryService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/view-history")
public class ViewHistoryController {
    @Autowired
    private HistoryService historyService;

    @GetMapping()
    public ResponseEntity<List<ViewHistory>> getHistory(@RequestParam("id") String id) {
        List<ViewHistory> history = historyService.getViewHistory(id);
        System.out.println(history);
        return ResponseEntity.ok(history);
    }

    @PutMapping()
    public ResponseEntity<String> setHistory(@RequestParam("id") String id, @RequestParam("product") String product) {
        if (id == null || id.isEmpty()) {
            return ResponseEntity.ok("User not logged in");
        }
        String result = historyService.addView(id, product);
        System.out.println(result);
        return ResponseEntity.ok(result);
    }

}