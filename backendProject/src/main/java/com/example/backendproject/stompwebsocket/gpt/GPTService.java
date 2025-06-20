package com.example.backendproject.stompwebsocket.gpt;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class GPTService {

    ///  내 키
    // String gpt_key = "Bearer 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";
    ///  강사님 키
    String gpt_key = "Bearer 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";

    //json문자열 <-> 자바객체, json객체2
    private final ObjectMapper mapper = new ObjectMapper();

    public String gptMessage(String message) throws Exception {

        //API 호출을 위한 본문 작성
        Map<String,Object> requestBody  = new HashMap<>();
        requestBody .put("model","gpt-4o");
        requestBody .put("input", message);

        //http 요청 작성
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/responses"))
                .header("Authorization",gpt_key)
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestBody))) //본문 삽입
                .build();


        //요청 전송 및 응답 수신
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());

        //응답을 Json으로 파싱
        JsonNode jsonNode = mapper.readTree(response.body());
        System.out.println("gpt 응답 : " + jsonNode);

        //메세지 부분만 추출하여 반환
        // 없으면 "" 반환
        String gptMessageResponse = jsonNode
                .path("output").path(0)
                .path("content").path(0)
                .path("text").asText();

        return gptMessageResponse;
    }

}