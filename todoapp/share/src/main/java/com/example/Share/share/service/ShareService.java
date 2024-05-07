package com.example.Share.share.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.Share.share.model.Folder_User;
import com.example.Share.share.model.ShareSettings;
import com.example.Share.share.model.Todo_User;
import com.example.Share.share.model.dto.ShareDto;
import com.example.Share.share.repository.FolderUserRepository;
import com.example.Share.share.repository.ShareRepository;
import com.example.Share.share.repository.TodoUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShareService {
    private final ShareRepository shareRepo;

    private final FolderUserRepository folderRepo;

    private final TodoUserRepository todoRepo;

    private final List<String> VIEWS = new ArrayList<String>(){
        {
            add("organization");
            add("users");
        }
    };

    

    // Create or Update Share Settings
    public String share(ShareDto shareRequest){
        System.out.println(shareRequest);
        Integer fileId = shareRequest.getFileId();
        Optional<ShareSettings> optionalShareSettings = shareRepo.findByFileId(fileId);
        // Save Users if it is organization or users View
        this.saveUsers(shareRequest);

        if (optionalShareSettings.isPresent()){

            String view = shareRequest.getView();
            Boolean editable = shareRequest.getEditable();
            if (view == "private"){
                editable = false;
                this.removeUsers(shareRequest);
            }

            ShareSettings shareSettings = optionalShareSettings.get();
            shareSettings.setEditable(editable);
            shareSettings.setView(view);
            shareRepo.save(shareSettings);
            
        }
        else{
            shareRepo.save(shareRequest.mapToShareSettings());
        }
        
        System.out.println("Share is updated!");
        return "Done";
    }

    // Get File View
    public String getView(ShareSettings shareRequest){
        String view = "private";
        Optional<ShareSettings> optionalShareSettings = shareRepo.findByFileIdAndFileName(shareRequest.getFileId(), shareRequest.getFileName());
        if (optionalShareSettings.isPresent()){
            ShareSettings shareSettings = optionalShareSettings.get();
            view = shareSettings.getView();
        }
        return view;
    }

    // Check if file is editable
    public Boolean isEditable(ShareSettings shareRequest){
        Boolean editable = false;
        Optional<ShareSettings> optionalShareSettings = shareRepo.findByFileIdAndFileName(shareRequest.getFileId(), shareRequest.getFileName());
        if (optionalShareSettings.isPresent()){
            ShareSettings shareSettings = optionalShareSettings.get();
            editable = shareSettings.getEditable();
        }
        return editable;
    }

    // Check if file is readable
    public Boolean canRead(ShareDto shareRequest){
        System.out.println("ShareRequest : " + shareRequest);
        ShareSettings shareSettings = shareRequest.mapToShareSettings();
        String view = this.getView(shareSettings);
        switch (view) {
            case "private":
                return false;
            case "public":
                return true;
            case "users":
            case "organization":
                Integer userId = shareRequest.getUsersId().get(0);
                return this.isUserFound(userId, shareSettings.getFileId(), shareSettings.getFileName());
        }
        // Return error if not found 
        return true;        
    }

    // Check if file is readable and editable
    public Boolean canReadAndWrite(ShareDto shareRequest){
        ShareSettings shareSettings = shareRequest.mapToShareSettings();
        if (this.canRead(shareRequest) && this.isEditable(shareSettings)){
            return true;
        }
        return false;
    }


    // Reusable Function [Private]

    // Check if file can be accessed by user
    private Boolean isUserFound(Integer userId, Integer fileId, String fileType){
        Boolean result = false;
        if (fileType == "todo"){
           result = this.isUserTodoFound(userId, fileId);
        }
        else if (fileType == "folder"){
            result = this.isUserFolderFound(userId, fileId);
        }
        return result;
    }

    private Boolean isUserTodoFound(Integer userId, Integer fileId){
        Optional<Todo_User> todoUser = todoRepo.findByTodoIdAndUserId(fileId, userId);
        if (todoUser.isPresent()){
            return true;
        }
        return false;
    }

    private Boolean isUserFolderFound(Integer userId, Integer fileId){
        Optional<Folder_User> folderUser = folderRepo.findByUserIdAndFolderId(fileId, userId);
        if (folderUser.isPresent()){
            return true;
        }
        return false;
    }

    // Save Users to TodoFile
    private void saveTodoUsers(List<Integer> userIdList, Integer fileId){
        for (Integer userId : userIdList){
            Optional<Todo_User> todoUser = todoRepo.findByTodoIdAndUserId(fileId, userId);
            if (!todoUser.isPresent()){
                todoRepo.save(new Todo_User(fileId, userId));
            }
        }
    }

    // Save Users to FolderFile
    private void saveFolderUsers(List<Integer> userIdList, Integer fileId){
        for (Integer userId : userIdList){
            Optional<Folder_User> folderUser = folderRepo.findByUserIdAndFolderId(userId, fileId);
            if (!folderUser.isPresent()){
                folderRepo.save(new Folder_User(fileId, userId));
            }
        }
    }

    // Remove Users from TodoFile
    private void removeTodoUsers(Integer fileId){
       todoRepo.deleteByTodoId(fileId);
    }

    // Remove Users from FolderFile
    private void removeFolderUsers(Integer fileId){
        folderRepo.deleteByFolderId(fileId);
    }

    // Abstract function to save users
    private void saveUsers(ShareDto shareRequest){
        if (VIEWS.contains(shareRequest.getView())){
            List<Integer> userIdList = shareRequest.getUsersId();
            Integer fileId = shareRequest.getFileId();

            if (shareRequest.getFileName() == "folder"){
                this.saveFolderUsers(userIdList, fileId);
            }
            else{
                this.saveTodoUsers(userIdList, fileId);
            }
        }
    }

    // Abstract function to remove users
    private void removeUsers(ShareDto shareRequest){
        if (VIEWS.contains(shareRequest.getView())){

            Integer fileId = shareRequest.getFileId();

            if (shareRequest.getFileName() == "folder"){
                this.removeTodoUsers(fileId);
            }
            else{
                this.removeFolderUsers(fileId);
            }
        }
    }
}
