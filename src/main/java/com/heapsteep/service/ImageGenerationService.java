package com.heapsteep.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ImageGenerationService {

    private final RestClient restClient;

    @Value("${spring.ai.image.generation.model:sd3.5-medium}")
    private String imageModel;

    public ImageGenerationService(
            RestClient.Builder restClientBuilder,
            @Value("${spring.ai.stabilityai.api-key}") String apiKey) {
        this.restClient = restClientBuilder
                .baseUrl("https://api.stability.ai")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Accept", "image/*")
                .build();
    }

    public byte[] generate(String prompt) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("none", new ByteArrayResource(new byte[0]));
        bodyBuilder.part("prompt", prompt);
        bodyBuilder.part("model", imageModel);
        bodyBuilder.part("output_format", "png");
        bodyBuilder.part("aspect_ratio", "1:1");

        return restClient.post()
                .uri("/v2beta/stable-image/generate/sd3")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(bodyBuilder.build())
                .retrieve()
                .body(byte[].class);
    }
}
