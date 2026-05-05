package com.heapsteep.service;

import com.heapsteep.model.TravelPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TravelPlanService {

    private static final Logger log = LoggerFactory.getLogger(TravelPlanService.class);

    private final ChatClient chatClient;

    @Value("classpath:prompts/travel-plan.st")
    private Resource travelPlanResource;

    @Value("classpath:prompts/city-tips.st")
    private Resource cityTipsResource;

    public TravelPlanService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public TravelPlan getTravelPlan(String city, int days) {
        PromptTemplate promptTemplate = new PromptTemplate(travelPlanResource);

        Map<String, Object> variables = Map.of(
                "days", days,
                "city", city
        );

        Prompt prompt = promptTemplate.create(variables);

        return chatClient.prompt(prompt)
                .call()
                .entity(TravelPlan.class);
    }

    public TravelPlan getTravelPlanChained(String city, int days) {
        Prompt tipsPrompt = new PromptTemplate(cityTipsResource)
                .create(Map.of("city", city));

        String tips = chatClient.prompt(tipsPrompt)
                .call()
                .content();

        log.info("Step 1 - tips for {}:\n{}", city, tips);

        Prompt planPrompt = new PromptTemplate(travelPlanResource)
                .create(Map.of("days", days, "city", city));

        return chatClient.prompt(planPrompt)
                .system("Use these tips while building the plan:\n" + tips)
                .call()
                .entity(TravelPlan.class);
    }
}
