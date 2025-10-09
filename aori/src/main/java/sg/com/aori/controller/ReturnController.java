package sg.com.aori.controller;

import sg.com.aori.model.ReturnRequest;
import sg.com.aori.service.ReturnService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/returns")
public class ReturnController {

    private final ReturnService returnService;

    public ReturnController(ReturnService returnService) {
        this.returnService = returnService;
    }

    // Step 3 & 4: Receives the ReturnRequest entity directly from the JSON body.
    @PostMapping("/request")
    public ResponseEntity<String> initiateReturn(
            @RequestBody ReturnRequest requestEntity) { // 👈 Accepting the Entity directly

        try {
            // NOTE: We rely on the request JSON body to contain 'orderId', 'productId', and
            // 'requestReason'.

            String userId = "current_logged_in_user_id";

            // Delegate the entire entity to the service
            String confirmationMessage = returnService.processReturnRequest(requestEntity, userId);

            // Step 6 & 7 (Return Confirmation/Instructions)
            return ResponseEntity.ok(confirmationMessage);

        } catch (IllegalArgumentException e) {
            // Catches validation or "not found" errors
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            // Catches failure during the refund process (Step 11)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Processing failed: " + e.getMessage());
        }
    }
}