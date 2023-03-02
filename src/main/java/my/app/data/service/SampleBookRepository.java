package my.app.data.service;

import my.app.data.entity.SampleBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SampleBookRepository extends JpaRepository<SampleBook, Long>, JpaSpecificationExecutor<SampleBook> {

}
