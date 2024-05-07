package com.example.Share.share.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.ServerRequest;

import com.example.Share.share.model.ShareSettings;
import com.example.Share.share.model.dto.ShareDto;
import com.example.Share.share.service.ShareService;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.hibernate.mapping.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;



@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ShareController {
    private final ShareService shareService;

    @GetMapping("health")
    public String getMethodName() {
        return "Share is alive service";
    }
    

    @PostMapping({"folder", "todo"})
    public Boolean share(@RequestBody ShareDto request) {
        String returnedVal = shareService.share(request);
        if (returnedVal == "Done"){
            return  true;
        }
        return  false;

    }

    @GetMapping({"folder/view", "todo/view"})
    public String getView(@ModelAttribute ShareDto request) {
        ShareSettings shareSettings = request.mapToShareSettings();
        return shareService.getView(shareSettings);
    }

    @GetMapping({"folder/read", "todo/read"})
    public Boolean canRead(@ModelAttribute ShareDto params) {
        System.out.println("Params: " + params);
        return shareService.canRead(params);
        // return true;
    }

    @GetMapping({"folder/edit", "todo/edit"})
    public Boolean isEditable(@ModelAttribute ShareDto request) {
        ShareSettings shareRequest = request.mapToShareSettings();
        return shareService.isEditable(shareRequest);
    }
    
    // "/folder/root/todo/1/share" {Share Todo}
}
