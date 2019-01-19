package net.fklj.richanemic.rdm.repository;

import net.fklj.richanemic.data.Product;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductRepository extends BaseRepository<Product, Integer> {

    List<Product> findAllById(Collection<Integer> ids);
}
