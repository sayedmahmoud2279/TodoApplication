package com.example.Share.share.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Share.share.model.Folder_User;


public interface FolderUserRepository extends JpaRepository<Folder_User, Integer>{
    Optional<Folder_User> findByUserIdAndFolderId(Integer userId, Integer folderId);
    void deleteByFolderId(Integer folderId);
}
