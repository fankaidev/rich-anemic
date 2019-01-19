package net.fklj.richanemic.rdm.repository;

import net.fklj.richanemic.data.Variant;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariantRepository extends BaseRepository<Variant, Integer> {

    List<Variant> findByProductId(int productId);
}
