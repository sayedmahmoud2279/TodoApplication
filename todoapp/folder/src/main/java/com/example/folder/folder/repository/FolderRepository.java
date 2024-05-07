package com.example.folder.folder.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.folder.folder.model.Folder;
import java.util.List;
import java.util.Optional;


public interface FolderRepository extends JpaRepository<Folder, Integer>{
    List<Folder> findByParentFolder(Folder parent);
    List<Folder> findByOwnerId(Integer ownerId);
    List<Folder> findByOwnerIdNot(Integer ownerId);
    Optional<Folder> findByNameAndOwnerId(String name, Integer ownerId);
}
