package net.fklj.richanemic.adm.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountTransaction {

    private int id;

    private int amount;

}
