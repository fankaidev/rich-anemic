package net.fklj.richanemic.rdm.repository;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.LockModeType;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends CrudRepository<T, ID> {

    @Lock(LockModeType.WRITE)
    default Optional<T> lock(ID id) {
        return findById(id);
    }

}
