package com.example.Share.share.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Share.share.model.ShareSettings;

public interface ShareRepository extends JpaRepository<ShareSettings, Integer> {
    public Optional<ShareSettings> findByFileId(Integer fileId);
    public Optional<ShareSettings> findByFileIdAndFileName(Integer fileId, String fileName);
}
