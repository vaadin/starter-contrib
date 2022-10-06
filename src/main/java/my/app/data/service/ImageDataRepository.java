package my.app.data.service;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import my.app.data.entity.ImageData;

public interface ImageDataRepository extends JpaRepository<ImageData, UUID> {

}
