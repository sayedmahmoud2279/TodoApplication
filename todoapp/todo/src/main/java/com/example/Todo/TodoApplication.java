package com.example.Todo;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.example.Todo.todo.model.Task;
import com.example.Todo.todo.model.Todo;
import com.example.Todo.todo.repository.TaskRepository;
import com.example.Todo.todo.repository.TodoRepository;

@SpringBootApplication
@EnableDiscoveryClient
public class TodoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoApplication.class, args);
	}

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

	@Bean
	public CommandLineRunner loadData( 
	TodoRepository todoRepository,
	TaskRepository taskRepository) {
		return (args) -> {

			// Insert into Todo
			Todo[] todos = {
					Todo.builder().id(1).name("Todo 1").ownerId(1).folderId(10).build(),
					Todo.builder().id(2).name("Todo 2").ownerId(1).folderId(10).build(),
					Todo.builder().id(3).name("Todo 3").ownerId(2).folderId(2).build(),
					Todo.builder().id(4).name("Todo 4").ownerId(1).folderId(2).build(),
					Todo.builder().id(5).name("Todo 5").ownerId(1).folderId(3).build(),
					Todo.builder().id(6).name("Todo 6").ownerId(2).folderId(3).build(),
					Todo.builder().id(7).name("Todo 7").ownerId(1).folderId(1).build(),
					Todo.builder().id(8).name("Todo 8").ownerId(2).folderId(1).build(),
					Todo.builder().id(9).name("Todo 9").ownerId(2).folderId(7).build(),
					Todo.builder().id(10).name("Todo 10").ownerId(2).folderId(1).build()
			};
			todoRepository.saveAll(Arrays.asList(todos));

			// Insert into Task
			Task[] tasks = {
					Task.builder().id(1).name("Task 1").status("pending").todo(todos[0]).build(),
					Task.builder().id(2).name("Task 2").status("pending").todo(todos[0]).build(),
					Task.builder().id(3).name("Task 3").status("pending").todo(todos[0]).build(),
					Task.builder().id(4).name("Task 4").status("pending").todo(todos[0]).build(),
					Task.builder().id(5).name("Task 5").status("completed").todo(todos[0]).build(),
					Task.builder().id(6).name("Task 6").status("pending").todo(todos[8]).build(),
					Task.builder().id(7).name("Task 7").status("pending").todo(todos[8]).build(),
					Task.builder().id(8).name("Task 8").status("pending").todo(todos[8]).build(),
					Task.builder().id(9).name("Task 9").status("pending").todo(todos[8]).build(),
					Task.builder().id(10).name("Task 1").status("pending").todo(todos[4]).build()
			};
			taskRepository.saveAll(Arrays.asList(tasks));
		};
	}
}
