package com.example.folder.folder.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.folder.folder.model.Folder;
import com.example.folder.folder.model.dto.FolderRequest;
import com.example.folder.folder.model.dto.ShareDTO;
import com.example.folder.folder.repository.FolderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FolderService {


    private final RestTemplate restTemplate;

    private final FolderRepository folderRepository;

    private final String SHAREURL = "http://share/folder";
    private final String EDITFOLDERSHARE = "http://share/folder/edit";
    private final String VIEWFOLDERSHARE = "http://share/folder/view";
    private final String READFOLDERSHARE = "http://share/folder/read";

    private enum AccessType {
        EDIT, READ
    }
    private final String FILENAME = "folder";

    // Reusable Function 
    private Folder getFolder(Integer folderId){

        Folder folder =  folderRepository.findById(folderId).orElse(null);
        return folder;

        
    }

    private Boolean isUsedName(String folderName, List<Folder> folderList){
        for (Folder folder : folderList){   
            if (folder.getName().equals(folderName)){
                return true;
            }
        }
        return false;
    }

    public Boolean isOwner(Folder folder, Integer userId){
        return folder.getOwnerId() == userId;
    }

    public MultiValueMap<String, String> buildQueryParams(String fileType, String folderId, String userId){
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("fileName", fileType);
        queryParams.add("fileId", folderId);
        queryParams.add("userId", userId);

        return queryParams;
        
    }

    public URI buildUrl(String url, MultiValueMap<String, String> params){
        return UriComponentsBuilder
        .fromUriString(url)
        .queryParams(params)
        .build()    
        .toUri();
    }

    private URI buildRequest(String url, String folderId, String userId){
        MultiValueMap<String, String> queryParams = this.buildQueryParams("folder", folderId, userId);
        URI requestUrl = this.buildUrl(url, queryParams);
        return requestUrl;
    }

    public Boolean canEditFolder(String folderId, String userId){
        URI requestUrl = this.buildRequest(EDITFOLDERSHARE, folderId, userId);
        Boolean canEditTodo = restTemplate.getForObject(requestUrl, Boolean.class);
        System.out.println(canEditTodo);
        return canEditTodo;
    }

    public String getFolderView(String folderId, String userId){
        URI requestUrl = this.buildRequest(VIEWFOLDERSHARE, folderId, userId);
        String view = restTemplate.getForObject(requestUrl, String.class);

        return view;
    }

    private Boolean canReadFolder(String folderId, String userId){
        URI requestUrl = this.buildRequest(READFOLDERSHARE, folderId, userId);
        System.out.println(requestUrl);

        Boolean canReadTodo = restTemplate.getForObject(requestUrl, Boolean.class);

        return canReadTodo;
    }
   
    private void checkAccess(Folder folder, Integer userId, AccessType accessType) throws Exception {
        String folderId = String.valueOf(folder.getId());
        String tempUserId = String.valueOf(userId);
        
        Boolean canAccessFolder = false;
        switch (accessType) {
            case EDIT:
                canAccessFolder = this.canEditFolder(folderId, tempUserId);
                break;
            case READ:
                canAccessFolder = this.canReadFolder(folderId, tempUserId);
                break;
        }

        Boolean isFolderOwner = this.isOwner(folder, userId);
        if (!(canAccessFolder || isFolderOwner)){
            throw new Exception("Access Denied!");
        }
    }

    private void checkEditAccess(Folder folder, Integer userId) throws Exception {
        checkAccess(folder, userId, AccessType.EDIT);
    }
    
    private void checkReadAccess(Folder folder, Integer userId) throws Exception {
        checkAccess(folder, userId, AccessType.READ);
    }
    
    private void deleteTodoLists(Integer folderId){
        restTemplate.delete("http://todos/folders/" + folderId );
    }
    // Services
    public Folder createFolder(FolderRequest request, Integer userId) {
        System.out.println(request);
        // Get folders in currentFolder 
        Integer parentId =  request.getParentFolder();
        Folder parentFolder = this.getFolder(parentId);
        String folderName = request.getName();

        if (Objects.nonNull(parentFolder)){
            
            try {
                System.out.println("During Check");
                this.checkEditAccess(parentFolder, userId);
                
            } catch (Exception e) {
                // TODO: handle exception
                System.out.println("Failed");

                return Folder.builder().build();
            }
            System.out.println("During Check");

            Boolean isNameInUse = this.isUsedName(folderName, parentFolder.getSubFolders());
            if (!isNameInUse){


                Folder folder = Folder.builder()
                .name(folderName)
                .ownerId(userId)
                .parentFolder(parentFolder)
                .build();


                folderRepository.save(folder);
                // System.out.println(folder);
                return folder;
            }
            throw new RuntimeException("Name is already used!");

        }
        throw new RuntimeException("Folder is not found!");
    }

// =====================================================================================

    public Folder updateFolder(Integer folderId, FolderRequest request, Integer userId) {

        Folder folder = this.getFolder(folderId);
        String folderName = request.getName();

        if (Objects.nonNull(folder)){
            // Check if user can access folder
            try {
                this.checkEditAccess(folder, userId);
                
            } catch (Exception e) {
                // TODO: handle exception
                return folder;
            }

            Folder parentFolder = folder.getParentFolder();
            Boolean isNameInUse = this.isUsedName(folderName, parentFolder.getSubFolders());

            if (!isNameInUse){
                folder.setName(folderName);
                
                folderRepository.save(folder);
                return folder;
            }
            throw new RuntimeException("Name is already used!");

        }
        throw new RuntimeException("Folder is not found!");
    }
// ======================================================================================

    public List<Folder> getFolderById(List<Integer> folderId, Integer userId) {
        if (folderId.size() == 0){
            List<Folder> folders = new ArrayList<Folder>();
            Folder root = folderRepository.findByNameAndOwnerId("root", userId).get();
            folders.add(root);
            return folders;
        }

        // Get Folder 
        List<Folder> folders = folderId.stream()
        .map(id ->  this.getFolder(id))
        .collect(Collectors.toList());
        if (Objects.nonNull(folders)){
            // Check if user can access it as read
            List<Folder> excludedFolders = new ArrayList<Folder>();
            for (Folder folder : folders){
                try {
                    checkReadAccess(folder, userId);
                    
                } catch (Exception e) {
                    excludedFolders.add(folder);
                }
            }

            excludedFolders.forEach(folder -> {
                folders.remove(folder);
            });

            return folders;
        }
        throw new RuntimeException("Folder is not found!");

    }

    public List<Folder> getFolders(Optional<Integer> folderId, Integer userId) {
        Folder folder;
        if(folderId.isPresent()){
            folder = this.getFolder(folderId.get());
            
        }
        else{
            Optional<Folder> optionalFolder = folderRepository.findByNameAndOwnerId("root", userId);
            folder = optionalFolder.orElse(null);
        }
        // Get Folder 
        if (Objects.nonNull(folder)){

            List<Folder> folderList = folderRepository.findByParentFolder(folder);

            return folderList;
        }
        throw new RuntimeException("Folder is not found!");

    }

    public List<Folder> getFoldersByUser(Integer userId) {
        return folderRepository.findByOwnerId(userId);

    }

    public List<Folder> getSharedFolders(Integer userId){
        List<Folder> folders = folderRepository.findByOwnerIdNot(userId);
        folders.forEach(folder -> {
            Boolean canRead = canReadFolder(folder.getId().toString(), userId.toString());
            if (!canRead){
                folders.remove(folder);
            }
        });
        return folders;
    }

    // =======================================================================================

    public Boolean deleteFolderById(Integer folderId, Integer userId) {
        Folder folder = this.getFolder(folderId);
        if (Objects.nonNull(folder)){
            // Check if user can access it as read
            Boolean isFolderOwner =  this.isOwner(folder, userId);
            Boolean isNotRoot =  folder.getName() != "root";
            if (isFolderOwner && isNotRoot){
                this.deleteTodoLists(folderId); // Make it for internal use and remove isFolderOwner from todoService
                folderRepository.delete(folder);
                return true;
            }
            throw new IllegalAccessError("Access Deined");
        }
        throw new RuntimeException("Folder is not found!");

    }
// =============================================================================================
    public void shareFolder(Integer folderId, Integer userId, ShareDTO shareSettings) {
        Folder folder = this.getFolder(folderId);
        if (Objects.nonNull(folder)){
            // Check if user can access it as read
            Boolean isFolderOwner =  this.isOwner(folder, userId);
            if (isFolderOwner){
                shareSettings.setFileName(FILENAME);

                // Send request to Share Service
                ResponseEntity<?> response = restTemplate.postForObject(SHAREURL, shareSettings, ResponseEntity.class);
                Boolean status = response.getStatusCode().is2xxSuccessful();
                if (status){
                    System.out.println("Folder is shared successfully");
                }
                
            }
            throw new IllegalAccessError("Access Deined");
        }
        throw new RuntimeException("Folder is not found!");

    }
}
// ==============================================================================================
    
//     public void moveFolder(Integer folderId, Integer userId, Integer sourceId, Integer destinationId){
//         Folder sourceFolder = this.getFolder(sourceId);
//         Folder destFolder = this.getFolder(destinationId);
//         Folder folder = this.getFolder(folderId);
//         Boolean validFolders = Objects.nonNull(folder) && Objects.nonNull(sourceFolder) && Objects.nonNull(destFolder);
        
//         if (validFolders){
//             // Check if user can access it as read
//             Boolean isFolderOwner =  this.isOwner(folder, userId);
//             if (isFolderOwner){
//                 // TODO
//                 // Send request to Share Service
//                 ResponseEntity<?> response = restTemplate.postForObject(SHAREURL, shareSettings, ResponseEntity.class);
//                 Boolean status = response.getStatusCode().is2xxSuccessful();
//                 if (status){
//                     System.out.println("Folder is shared successfully");
//                 }
                
//             }
//             throw new IllegalAccessError("Access Deined");
//         }
//         throw new RuntimeException("Folder is not found!");

//     }
// // ========================================================================================

//     public String generateShareLink(Integer folderId, Integer userId) {
//         Folder folder = this.getFolder(folderId);
//         if (Objects.nonNull(folder)){
//             // Check if user can access it as read
//             Boolean isFolderOwner =  this.isOwner(folder, userId);
//             if (isFolderOwner){
//                 // TODO
//                 return "http://localhost:8081/folder/" + String.valueOf(folderId);
                
//             }
//             throw new IllegalAccessError("Access Deined");
//         }
//         throw new RuntimeException("Folder is not found!");
//     }
// // =================================================================================================
// // ==============================================================================================
//     public void copyFolder(Long folderId) {
//         Optional<Folder> optionalFolder = folderRepository.findById(folderId);
//         if (optionalFolder.isPresent()) {
//             Folder originalFolder = optionalFolder.get();
//             Folder newFolder = new Folder();
//             newFolder.setName(originalFolder.getName() + "_copy");
//             newFolder.setOwner(originalFolder.getOwner()); 
//             folderRepository.save(newFolder);
//             System.out.println("Folder copied successfully with ID: " + newFolder.getId());
//         } else {
//             System.out.println("Folder not found with ID: " + folderId);
//         }
//     }
// // ===============================================================================================

//     public void addFolderToTodoList(Long folderId, Long todoListId) {
//         Optional<Folder> optionalFolder = folderRepository.findById(folderId);
//         if (optionalFolder.isPresent()) {
//             Folder folder = optionalFolder.get();
//             Optional<TodoList> optionalTodoList = todoListRepository.findById(todoListId);
//             if (optionalTodoList.isPresent()) {
//                 TodoList todoList = optionalTodoList.get();
//                 todoList.addFolder(folder);
//                 todoListRepository.save(todoList);
//                 System.out.println("Folder added to to-do list successfully.");
//             } else {
//                 System.out.println("To-do list not found with ID: " + todoListId);
//             }
//         } else {
//             System.out.println("Folder not found with ID: " + folderId);
//         }
//     }

// }
