package net.fklj.richanemic.rdm.repository;

import net.fklj.richanemic.rdm.entity.product.ProductEntity;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductRepository extends BaseRepository<ProductEntity, Integer> {

    List<ProductEntity> findAllById(Collection<Integer> ids);
}
