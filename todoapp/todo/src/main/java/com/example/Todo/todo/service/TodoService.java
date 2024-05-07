package com.example.Todo.todo.service;

import com.example.Todo.todo.service.UtilityClass;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


import com.example.Todo.todo.model.Task;
import com.example.Todo.todo.model.Todo;
import com.example.Todo.todo.model.Todo_Folder;
import com.example.Todo.todo.model.dto.FolderRequest;
import com.example.Todo.todo.model.dto.ShareDTO;
import com.example.Todo.todo.repository.TodoRepository;
import com.example.Todo.todo.repository.TodoFolderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TaskService taskService;
    private final UtilityClass utilityClass;

    private final TodoRepository todoRepo;
    private final TodoFolderRepository todoFolderRepo;

    private final RestTemplate restTemplate;

    private final String SHAREURL = "http://share/todo";

    private final String EDITFOLDERSHARE = "http://share/folder/edit";

    private final String VIEWTODOSHARE = "http://share/todo/view";

    private final String READTODOSHARE = "http://share/todo/read";
    
    private final String EDITTODOSHARE = "http://share/todo/edit";

    private final String FILENAME = "todo";

    // Reusable Functions
    private Integer getFolderOwner(Integer folderId, String token){
        MultiValueMap<String, String> queryParams = utilityClass.buildQueryParams(Map.of("folderId", folderId.toString()));
        // Make Header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token); 
        // Create an HttpEntity with the headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);  
        // send Request
        URI url = utilityClass.buildUrl("http://folder/folders", queryParams);
        List<LinkedHashMap> folderRequest = restTemplate.exchange(url, HttpMethod.GET, requestEntity, List.class).getBody();
        Integer folderOwnerId = (Integer) folderRequest.get(0).get("ownerId");
        return folderOwnerId; // Potential null pointer access: The variable folder may be null at this location [folder variable]
    }

    private Boolean isFolderOwner(Integer userId, Integer folderId, String token){
        Integer folderOwner = this.getFolderOwner(folderId, token);
        return utilityClass.isOwner(userId, folderOwner);
    }

    private Boolean canReadTodo(MultiValueMap<String, String> queryParams){
        URI todoEdit = utilityClass.buildUrl(READTODOSHARE, queryParams);
        Boolean canReadTodo = restTemplate.getForObject(todoEdit, Boolean.class);

        return canReadTodo;
    }

    // TODO : Refactor it remove it from here
    private String getTodoView(MultiValueMap<String, String> queryParams){
        URI todoEdit = utilityClass.buildUrl(VIEWTODOSHARE, queryParams);
        String view = restTemplate.getForObject(todoEdit, String.class);

        return view;
    }
    
    private Boolean canEditFolder(MultiValueMap<String, String> queryParams) {
        URI todoEdit = utilityClass.buildUrl(EDITFOLDERSHARE, queryParams);
        Boolean canEditFolder = restTemplate.getForObject(todoEdit, Boolean.class);

        return canEditFolder;
    }

    private String giveNameToCopy(String todoName){
        if (todoName.contains("_Copy")){
            String[] nameSplit = todoName.split("_");
            try {
                int lastIndex = nameSplit.length - 1;
                Integer number = Integer.parseInt(nameSplit[lastIndex]) + 1;
                nameSplit[lastIndex] = number.toString();
                todoName = String.join("_", nameSplit);
            }
            catch (Exception e){
                todoName = todoName + "_1";
            }
        }
        else{
            todoName = todoName + "_Copy";
        }
        return todoName;
    }

    private List<Todo> mergeItemsOnce(List<Todo> todoList, List<Todo> tempTodoList){
        for (Todo todo : tempTodoList){
            if (!todoList.contains(todo)){
                todoList.add(todo);
            }
        }
        return todoList;
    }
   
    // Create TodoList
    public Todo createTodo(Todo todoList, Integer userId, String token){
        // Check if I can create todo in this folder
        // Get Folder Owner
        Integer folderId = todoList.getFolderId();
        Boolean isFolderOwner = this.isFolderOwner(userId, folderId, token);
        // Get folder permission
        Map<String, String> params = utilityClass.buildShareParams("folder", folderId.toString(), userId.toString());
        MultiValueMap<String, String> queryParams = utilityClass.buildQueryParams(params);
        Boolean canEditFolder = this.canEditFolder(queryParams); // Send as Params in URL
        if (isFolderOwner || canEditFolder){
            Todo todo = Todo.builder()
            .folderId(folderId)
            .ownerId(userId)
            .name(todoList.getName())
            .build();
            todoRepo.save(todo);

            // Create Tasks
            List<Task> tasks = todoList.getTasks()
            .stream()
            .map(task -> taskService.createTask(task, todo))
            .toList();

            todo.setTasks(tasks);
            return todo;
        }
        throw new IllegalAccessError("Write Access to Folder is denied!");
    }

    // Update Function
    public Todo updateTodo(Todo todoList, Integer id, Integer tempUserId, String token){
        // TODO : Get user who want to update todo using JWT && Ask about all those condition below if clean code 
        // Check if todo is found
        Optional<Todo> optionalTodoList = todoRepo.findById(id);
        if (optionalTodoList.isPresent()){ 
            Todo currentTodo = optionalTodoList.get();
            Integer folderId = todoList.getFolderId();
            // Check shorcuts
            List<Todo_Folder> todoFolders = todoFolderRepo.findByTodoId(id);
//            Boolean isAnotherFolder = false;
//            for (Todo_Folder todoFolder : todoFolders){
//                if (todoFolder.getFolderId() == folderId){
//                    isAnotherFolder = true;
//                    break;
//                }
//            }

//            if ((todoList.getId() == id) && (currentTodo.getFolderId() == folderId || isAnotherFolder)){
            if ((todoList.getId() == id)){
                // to be changed to user who sends request
                String userId = tempUserId.toString();
                String todoId = id.toString();
                
                Map<String, String> params = utilityClass.buildShareParams("todo", todoId, userId);
                MultiValueMap<String, String> queryParams = utilityClass.buildQueryParams(params);

                Boolean canEditTodo = utilityClass.canEditTodo(queryParams);
//                Boolean isFolderOwner = this.isFolderOwner(tempUserId, currentTodo.getFolderId(), token);
                String view = this.getTodoView(queryParams);
                if (utilityClass.isOwner(tempUserId, currentTodo.getOwnerId()) || canEditTodo ){

                    // update tasks
                    List<Task> taskList;
                    if (view == "organization"){
                        taskList = todoList.getTasks()
                        .stream()
                        .map(task -> taskService.updateStatus(task))
                        .collect(Collectors.toList());;

                    }
                    else{
                        currentTodo.setName(todoList.getName());

                        taskList = todoList.getTasks()
                        .stream()
                        .map(task -> taskService.updateTask(task, currentTodo))
                        .collect(Collectors.toList());;

                    }
                    currentTodo.setTasks(taskList);
                    todoRepo.save(currentTodo);

                    return currentTodo;
                }
                throw new IllegalAccessError("Write Access is Denied!");
            }
        }
        throw new NullPointerException("Invalid Todo Data");


    }

    // Get Todo by Id
    public Todo getTodoById(Integer id, Integer tempUserId, String token){
        // TODO : add for shotcut
        Optional<Todo> optionalTodoList  = todoRepo.findById(id);
        if (optionalTodoList.isPresent()){
            Todo todoList = optionalTodoList.get();
            Integer ownerId = todoList.getOwnerId();

            // Integer tempUserId = todoList.getOwnerId(); // to be changed to user who sends request
            String userId = tempUserId.toString();
            String todoId = todoList.getId().toString();
            Map<String, String> params = utilityClass.buildShareParams("todo", todoId, userId);
            MultiValueMap<String, String> queryParams = utilityClass.buildQueryParams(params);
            Boolean canReadTodo = this.canReadTodo(queryParams);
            if (utilityClass.isOwner(tempUserId, ownerId) || canReadTodo){
                return todoList;
            }
        }
        return Todo.builder().build();

    }

    // Get all Todo
    public List<Todo> getTodoLists(Integer folderId, Integer userId, String token){
        // Look to refactor from line 148 to 154 similar to getTodoById from 126 to 133
        List<Todo_Folder> shortcuts  = todoFolderRepo.findByFolderId(folderId);
        List<Todo> todoList  = todoRepo.findByFolderId(folderId);
        // System.out.println("Todo List : " + todoList);
        List<Todo> tempTodoList = shortcuts.stream().map(record -> todoRepo.findById(record.getTodoId()).get())
        .toList();
        // System.out.println("Short Todo List : " + tempTodoList);
        todoList = this.mergeItemsOnce(todoList, tempTodoList);
//        System.out.println("final Todo List : " + todoList);

        // System.out.println();
        if (!this.isFolderOwner(userId, folderId, token)){
            System.out.println("Entered is owner!");
            // Loop over todoList check read permission
            for (Todo todo : todoList){
                Integer ownerId = todo.getOwnerId();
                String todoId = todo.getId().toString();
                String tempUserId = userId.toString();
                Map<String, String> params = utilityClass.buildShareParams("todo", todoId, tempUserId);
                MultiValueMap<String, String> queryParams = utilityClass.buildQueryParams(params);
                Boolean canReadTodo = this.canReadTodo(queryParams);
                if (!(utilityClass.isOwner(userId, ownerId) || canReadTodo)){ // User can be owner of Todo_1 in Folder that he does not own. Check if he is owner of Todo || canRead todo
                    todoList.remove(todo);
                }

            }
        }
        // Check if user is Folder owner

        return todoList;
    }

    public List<Todo> getSharedFolders(Integer userId){
        List<Todo> todos = todoRepo.findByOwnerIdNot(userId);
        List<Todo> sharedTodos = new ArrayList<>();

        todos.forEach(todo -> {
            String todoId = todo.getId().toString();
            String tempUserId = userId.toString();
            Map<String, String> params = utilityClass.buildShareParams("todo", todoId, tempUserId);
            MultiValueMap<String, String> queryParams = utilityClass.buildQueryParams(params);
            Boolean canRead = canReadTodo(queryParams);
            if (canRead){
                sharedTodos.add(todo);
            }
        });
        return sharedTodos;
    }

    // Delete Todo
    public Boolean deleteFolderById(Integer folderId, Integer userId, String token){
        List<Todo> todoLists = todoRepo.findByFolderId(folderId);
        Boolean isFolderOwner = this.isFolderOwner(userId, folderId, token);
        if(isFolderOwner){
            todoRepo.deleteAll(todoLists);
            return true;
        }
        return false;
    }

    public void delete(Integer id, Integer folderId, Integer userId, String token){


        Optional<Todo> optionalTodoList = todoRepo.findById(id);
        if (optionalTodoList.isPresent()){
            Todo todo = optionalTodoList.get();
            Integer todoFolderId = todo.getFolderId();
            if (folderId == todoFolderId){
                this.deleteTodo(todo, userId);
            }
            else{
                this.deleteShortcut(folderId, todo, userId, token);

            }

        }
    }

    public void deleteTodo(Todo todo, Integer userId){
        Integer ownerId = todo.getOwnerId();
        // to be changed to user who sends request
        if (utilityClass.isOwner(userId, ownerId)){
            todoRepo.delete(todo);
        }
    }

    public void deleteShortcut(Integer folderId, Todo todo, Integer userId, String token){
        Integer todoId = todo.getId();
        Optional<Todo_Folder> optionalTodoFolder = todoFolderRepo.findByTodoIdAndFolderId(todoId, folderId);
        if (optionalTodoFolder.isPresent()){
            Map<String, String> params = utilityClass.buildShareParams("folder", todoId.toString(), userId.toString());
            MultiValueMap<String, String> queryParams = utilityClass.buildQueryParams(params);
            Boolean canEditFolder = this.canEditFolder(queryParams);
            if (this.isFolderOwner(userId, folderId, token) || canEditFolder){
                Todo_Folder todoFolder = optionalTodoFolder.get();
                todoFolderRepo.delete(todoFolder);
            }

        }
        
    }

    public void copyTodo(FolderRequest destination, Integer userId, String token) {
        // Check if user can read todo || owner
        // Check if user is owner of destination || can edit
        Integer todoId = destination.getTodoIdList().get(0);
        Optional<Todo> optionalTodoList = todoRepo.findById(todoId);
        if (optionalTodoList.isPresent()){
            Todo todoList = optionalTodoList.get();
            // Get Owner Todo
            Integer todoOwnerId = todoList.getOwnerId();

            // Convert UserId and TodoId to String
            String tempUserId = userId.toString();
            String tempTodoId = todoList.getId().toString();


            // Send Request for checking todo read permission
            Map<String, String> params = utilityClass.buildShareParams("todo", tempTodoId, tempUserId);
            MultiValueMap<String, String> queryParams = utilityClass.buildQueryParams(params);
            Boolean canReadTodo = this.canReadTodo(queryParams);

            // Send Request for checking folder write permission 
            Integer folderId = destination.getId();

            if ((utilityClass.isOwner(userId, todoOwnerId) || canReadTodo)){
                try {
                    Todo todoListClone = (Todo) todoList.clone();
                    todoListClone.setFolderId(folderId);
                    todoListClone.setName(giveNameToCopy(todoList.getName()));
                    this.createTodo(todoListClone, userId, token);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
    }

    public void addShortcut(FolderRequest destination, Integer userId, String token){
        Integer todoId = destination.getTodoIdList().get(0);
        Optional<Todo> optionalTodoList = todoRepo.findById(todoId);
        if (optionalTodoList.isPresent()){
            Todo todoList = optionalTodoList.get();
            // Get Owner Todo
            Integer todoOwnerId = todoList.getOwnerId();
            // Get folder Id
            Integer folderId = destination.getId();
            if (folderId == todoList.getFolderId()){
                return;
            }

            // Convert UserId and TodoId to String
            String tempUserId = userId.toString();
            String tempTodoId = todoList.getId().toString();

            // Send Request for checking todo read permission
            Map<String, String> params = utilityClass.buildShareParams("todo", tempTodoId, tempUserId);
            MultiValueMap<String, String> queryParams = utilityClass.buildQueryParams(params);
            Boolean canReadTodo = this.canReadTodo(queryParams);

            // Send Request for checking folder write permission 
            params = utilityClass.buildShareParams("folder", folderId.toString(), tempUserId);
            queryParams = utilityClass.buildQueryParams(params);
            Boolean canEditFolder = this.canEditFolder(queryParams);

            if ((utilityClass.isOwner(userId, todoOwnerId) || canReadTodo) && (this.isFolderOwner(userId, folderId, token) || canEditFolder)){
                // Add shorcut
                Optional<Todo_Folder> shortcut = todoFolderRepo.findByTodoIdAndFolderId(todoId, folderId);
                if (!shortcut.isPresent()){
                    Todo_Folder todoFolder = Todo_Folder.builder()
                    .folderId(folderId)
                    .todoId(todoId)
                    .build();
                    todoFolderRepo.save(todoFolder);
                }
            }
        }
    }

    public void shareTodo(ShareDTO shareSettings, Integer userId){
        // Check if todo is found
        Integer todoId = shareSettings.getFileId();
        Optional<Todo> optionalTodoList = todoRepo.findById(todoId);
        if (optionalTodoList.isPresent()){
            Todo todo = optionalTodoList.get();

            // Check if user is owner
            if (utilityClass.isOwner(userId, todo.getOwnerId())){
                // Build Share Object
                shareSettings.setFileId(todoId);
                shareSettings.setFileName(FILENAME);

                // Send request to Share Service
                Boolean isShared = restTemplate.postForObject(SHAREURL, shareSettings, Boolean.class);
                if (isShared){
                    System.out.println("Todo is shared successfully");
                }
            }
        }
    }

    public void moveTodo(FolderRequest destination, Integer userId, String token){
        for (Integer todoId : destination.getTodoIdList()){
            Optional<Todo> optionalTodoList = todoRepo.findById(todoId);
            if (optionalTodoList.isPresent()){
                Todo todoList = optionalTodoList.get();
                // Get Owner Todo
                Integer todoOwnerId = todoList.getOwnerId();

                // Send Request for checking folder write permission 
                Integer folderId = destination.getId();

                if ((utilityClass.isOwner(userId, todoOwnerId)) && (this.isFolderOwner(userId, folderId, token))){
                    todoList.setFolderId(folderId);
                    todoRepo.save(todoList);
                }
            }
        }
        
    }

    public List<Todo> getRestTodoLists(Integer folderId, Integer userId, String token) {
        // Look to refactor from line 148 to 154 similar to getTodoById from 126 to 133
        List<Todo> todoList  = todoRepo.findByOwnerIdAndFolderIdNot(userId, folderId);

        if (!this.isFolderOwner(userId, folderId, token)){
           throw new IllegalAccessError("You can not access this folder!");
        }
        // Check if user is Folder owner
        return todoList;
    }
}
