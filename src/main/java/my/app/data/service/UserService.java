package my.app.data.service;

import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import my.app.data.entity.ImageData;
import my.app.data.entity.User;

@Service
public class UserService {

    private final UserRepository repository;
    private final ImageDataRepository imageDataRepository;

    public UserService(UserRepository repository, ImageDataRepository imageDataRepository) {
        this.repository = repository;
        this.imageDataRepository = imageDataRepository;
    }

    public Optional<User> get(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public User update(User entity, ImageData profilePicture) {
        if (profilePicture != null) {
            profilePicture = imageDataRepository.save(profilePicture);
            entity.setProfilePictureId(profilePicture.getId());
        }
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<User> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

    @Transactional
    public String getProfilePictureUrl(User user) {
        Optional<ImageData> image = imageDataRepository.findById(user.getProfilePictureId());
        if (image.isEmpty()) {
            return "";
        }
        return "data:image;base64," + Base64.getEncoder().encodeToString(image.get().getData());
    }

}
