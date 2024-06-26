package com.dnd.domain.image.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dnd.domain.image.domain.Image;
import com.dnd.domain.image.domain.ImageFileExtension;
import com.dnd.domain.image.domain.ImageType;

public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query(
            "select i from Image i where i.imageType = :imageType and i.targetId = :targetId and i.imageFileExtension = :imageFileExtension order by i.id desc limit 1")
    Optional<Image> queryImageKey(
            ImageType imageType, Long targetId, ImageFileExtension imageFileExtension);
}
