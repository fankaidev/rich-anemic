package net.fklj.richanemic.rdm.repository;

import net.fklj.richanemic.data.Variant;
import net.fklj.richanemic.rdm.entity.product.VariantEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariantRepository extends BaseRepository<VariantEntity, Integer> {

    List<VariantEntity> findByProductId(int productId);
}
