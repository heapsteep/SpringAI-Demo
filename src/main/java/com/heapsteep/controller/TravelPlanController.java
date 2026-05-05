package com.heapsteep.controller;

import com.heapsteep.model.TravelPlan;
import com.heapsteep.service.ChatService;
import com.heapsteep.service.TravelPlanService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class TravelPlanController {

    private final ChatService chatService;
    private final TravelPlanService travelPlanService;

    public TravelPlanController(ChatService chatService, TravelPlanService travelPlanService) {
        this.chatService = chatService;
        this.travelPlanService = travelPlanService;
    }

    @GetMapping("/travel-plan")
    public TravelPlan getTravelPlan(@RequestParam String city, @RequestParam int days){
        return travelPlanService.getTravelPlan(city, days);
    }

    @GetMapping("/travel-plan-chained")
    public TravelPlan getTravelPlanChained(@RequestParam String city, @RequestParam int days){
        return travelPlanService.getTravelPlanChained(city, days);
    }
}
