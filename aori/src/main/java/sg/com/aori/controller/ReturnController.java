package sg.com.aori.controller;

import sg.com.aori.model.Returns;
import sg.com.aori.service.ReturnService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

/**
 * @author Simon Lei
 * @date 2025-10-14
 * @version 1.0
 * @version 1.1 - Update on the ReturnController about validations and error
 *          handling.
 */

@RestController
@RequestMapping("/api/returns")
@Validated
public class ReturnController {

    private final ReturnService returnService;

    public ReturnController(ReturnService returnService) {
        this.returnService = returnService;
    }

    @PostMapping("/request")
    public ResponseEntity<String> initiateReturn(
            @RequestBody @Validated(Returns.OnCreate.class) @Valid Returns returns,
            @RequestHeader("X-User-Id") String userId) {
        try {
            String confirmationMessage = returnService.processReturnRequest(returns, userId);
            return ResponseEntity.ok(confirmationMessage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Processing failed: " + e.getMessage());
        }
    }
}