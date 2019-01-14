package net.fklj.richanemic.adm.repository;

import net.fklj.richanemic.data.Balance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface BalanceRepository {

    @Results({
            @Result(property = "userId",  column = "userId")
    })
    @Select("SELECT * FROM balance WHERE userId = #{userId}")
    Balance get(int userId);

    @Select("SELECT * FROM balance WHERE userId = #{userId} FOR UPDATE")
    Balance lock(int userId);

    @Update("UPDATE balance SET amount = amount + #{delta} WHERE userId = #{userId}")
    void changeAmount(int userId, int delta);

}
