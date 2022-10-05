package my.app.data.service;

import java.util.UUID;
import my.app.data.entity.SampleBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleBookRepository extends JpaRepository<SampleBook, UUID> {

}