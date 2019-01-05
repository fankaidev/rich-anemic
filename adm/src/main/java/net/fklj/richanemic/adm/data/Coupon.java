package net.fklj.richanemic.adm.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Coupon {

    private int id;

    private int userId;

    private int value;

}
