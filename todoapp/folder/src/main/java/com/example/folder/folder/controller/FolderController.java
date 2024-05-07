package com.example.folder.folder.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.folder.folder.model.Folder;
import com.example.folder.folder.model.dto.FolderResponse;
import com.example.folder.folder.service.FolderService;
import com.example.folder.folder.model.dto.FolderRequest;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PutMapping;





@RestController
@RequestMapping("/folders")
@RequiredArgsConstructor
@CrossOrigin
public class FolderController {
    private final FolderService folderService;
    private final RestTemplate restTemplate;


    @GetMapping("/health")
    public String getMethodName() {
        String url = "http://share/folder/read?fileName=folder&fileId=userId&userId=userId";
        // ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        String response = restTemplate.getForObject(url, String.class);
        return "Response from user-service: " + response;
    }
    

    @GetMapping(params = "folderId")
    public List<FolderResponse> getFolderDetails(@RequestParam(value = "folderId") List<Integer> folderId, @RequestHeader(name = "Authorization") String token) {
        Integer userId = -1;
        try {
            userId = Integer.parseInt(token);
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("System detect error : " + token);
        }
        List<Folder> folders = folderService.getFolderById(folderId, userId);
        List<FolderResponse> response = folders.stream()
        .map(folder -> FolderResponse.makeFolderResponse(folder))
        .toList();
        return response;
    }

    @GetMapping({"", "/{folderId}"})
    public List<FolderResponse> getSubFolders(@PathVariable(value = "folderId", required = false) Optional<Integer> folderId, @RequestHeader(name = "Authorization") String token) {
        // System.out.println(folderId);
        Integer userId = -1;
        try {
            userId = Integer.parseInt(token);
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("System detect error : " + token);
        }
        List<Folder> folderList = folderService.getFolders(folderId, userId);
        List<FolderResponse> folders = folderList.stream()
        .map(folder -> FolderResponse.makeFolderResponse(folder))
        .toList();

        return folders;
    }

    @GetMapping("/user")
    public List<FolderResponse> getFoldersByUser(@RequestHeader(name = "Authorization") String token){
        // System.out.println(folderId);
        Integer userId = -1;
        try {
            userId = Integer.parseInt(token);
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("System detect error : " + token);
        }
        List<Folder> folderList = folderService.getFoldersByUser(userId);
        List<FolderResponse> folders = folderList.stream()
        .map(folder -> FolderResponse.makeFolderResponse(folder))
        .toList();

        return folders;
    }

    @PostMapping
    public FolderResponse createFolder(@RequestBody FolderRequest request, @RequestHeader(name = "Authorization") String token) {
        
        Integer userId = -1;
        try {
            userId = Integer.parseInt(token);
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("System detect error : " + token);
        }
        try {
            Folder folder = folderService.createFolder(request, userId);  
            // System.out.println(folder);
            return FolderResponse.makeFolderResponse(folder);
        } catch (Exception e) {
            System.out.println(e.toString());
            return FolderResponse.makeFolderResponse();
        }
        
    }
    
    @PutMapping("/{folderId}")
    public FolderResponse updateFolder(@PathVariable(value = "folderId") Integer folderId, @RequestBody FolderRequest request, @RequestHeader(name = "Authorization") String token) {
        
        Integer userId = -1;
        try {
            userId = Integer.parseInt(token);
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("System detect error : " + token);
        }
        try {
            Folder folder = folderService.updateFolder(folderId, request, userId);  
            return FolderResponse.makeFolderResponse(folder);
        } catch (Exception e) {
            System.out.println(e.toString());
            return FolderResponse.builder()
            .name(request.getName())
            .id(request.getId())
            .parentFolder(request.getParentFolder())
            .build();
        }
        
    }

    @DeleteMapping("/{folderId}")
    public Boolean deleteFolder(@PathVariable(value = "folderId") Integer folderId, @RequestHeader(name = "Authorization") String token){
        
        Integer userId = -1;
        try {
            userId = Integer.parseInt(token);
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("System detect error : " + token);
        }
        return folderService.deleteFolderById(folderId, userId);
    }

    
}
