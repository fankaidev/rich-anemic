package net.fklj.richanemic.adm.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Account {

    private int userId;

    private int balance;

}
