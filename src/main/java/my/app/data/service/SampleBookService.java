package my.app.data.service;

import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import my.app.data.entity.ImageData;
import my.app.data.entity.SampleBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SampleBookService {

    private final SampleBookRepository repository;
    private final ImageDataRepository imageDataRepository;

    @Autowired
    public SampleBookService(SampleBookRepository repository, ImageDataRepository imageDataRepository) {
        this.repository = repository;
        this.imageDataRepository = imageDataRepository;
    }

    public Optional<SampleBook> get(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public SampleBook update(SampleBook entity, ImageData sampleBookImage) {
        if (sampleBookImage != null) {
            sampleBookImage = imageDataRepository.save(sampleBookImage);
            entity.setImage(sampleBookImage.getId());
        }
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<SampleBook> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

    @Transactional
    public String getImageUrl(SampleBook entity) {
        Optional<ImageData> image = imageDataRepository.findById(entity.getImageId());
        if (image.isEmpty()) {
            return "";
        }
        return "data:image;base64," + Base64.getEncoder().encodeToString(image.get().getData());
    }


}
