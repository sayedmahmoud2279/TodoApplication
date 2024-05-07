package com.example.folder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.client.RestTemplate;

import com.example.folder.folder.model.Folder;
import com.example.folder.folder.repository.FolderRepository;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAspectJAutoProxy
public class FolderApplication {

	public static void main(String[] args) {
		SpringApplication.run(FolderApplication.class, args);
	}
	
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

	

	@Bean
	public CommandLineRunner loadData( FolderRepository folderRepository) {
		return (args) -> {

			Folder rootFolder = Folder.builder()
				.name("root")
				.ownerId(1)
				.build();
				folderRepository.save(rootFolder);

			for(int i = 0; i < 8; i++){
				Folder rootFolder23 = Folder.builder()
				.name("root" + (i+1))
				.ownerId(1)
				.build();
				folderRepository.save(rootFolder23);
			}
			Folder rootFolder23 = Folder.builder()
				.name("root9" )
				.ownerId(2)
				.build();
				folderRepository.save(rootFolder23);

		};
	}
}
