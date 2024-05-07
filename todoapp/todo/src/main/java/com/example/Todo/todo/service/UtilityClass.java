package com.example.Todo.todo.service;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;


@Data
@Service
@RequiredArgsConstructor
public class UtilityClass {

    private final RestTemplate restTemplate;

    private final String SHAREURL = "http://share/todo";

    private final String EDITFOLDERSHARE = "http://share/folder/edit";

    private final String VIEWTODOSHARE = "http://share/todo/view";

    private final String READTODOSHARE = "http://share/todo/read";

    private final String EDITTODOSHARE = "http://share/todo/edit";

    private final String FILENAME = "todo";

    public Boolean isOwner(Integer userId, Integer ownerId){
        return userId == ownerId;
    }

    public MultiValueMap<String, String> buildQueryParams(Map<String, String> params) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        
        for (Map.Entry<String, String> entry : params.entrySet()) {
            queryParams.add(entry.getKey(), entry.getValue());
        }
    
        return queryParams;
    }

    public Map<String, String> buildShareParams(String fileType, String fileId, String userId) {
        Map<String, String> params = new HashMap<>();
        params.put("fileName", fileType);
        params.put("fileId", fileId);
        params.put("userId", userId);
    
        return params;
    }

    public URI buildUrl(String url, MultiValueMap<String, String> params){
        return UriComponentsBuilder
        .fromUriString(url)
        .queryParams(params)
        .build()    
        .toUri();
    }

    public Boolean canEditTodo(MultiValueMap<String, String> queryParams){
        URI todoEdit = this.buildUrl(EDITTODOSHARE, queryParams);
        Boolean canEditTodo = restTemplate.getForObject(todoEdit, Boolean.class);

        return canEditTodo;
    }

    public String getTodoView(MultiValueMap<String, String> queryParams){
        URI todoEdit = this.buildUrl(VIEWTODOSHARE, queryParams);
        String view = restTemplate.getForObject(todoEdit, String.class);

        return view;
    }

}
