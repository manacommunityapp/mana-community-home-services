package com.manacommunity.homeservices.controller;

import com.manacommunity.homeservices.dto.HomeServiceChatMessageRequest;
import com.manacommunity.homeservices.model.entity.ContextualChatMessageEntity;
import com.manacommunity.homeservices.service.HomeServiceContextualChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController("homeServiceContextualChatController")
@RequestMapping("/api/v1/home-services/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HomeServiceContextualChatController {
    private final HomeServiceContextualChatService chatService;

    @PostMapping
    public ResponseEntity<ContextualChatMessageEntity> sendMessage(@RequestBody HomeServiceChatMessageRequest req) {
        return ResponseEntity.ok(chatService.sendMessage(req));
    }

    @GetMapping
    public ResponseEntity<List<ContextualChatMessageEntity>> getMessages(
            @RequestParam String workerId,
            @RequestParam String residentUserId) {
        return ResponseEntity.ok(chatService.getChatHistory(workerId, residentUserId));
    }
}
