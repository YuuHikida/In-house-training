package analix.DHIT.repository;

import analix.DHIT.input.ApplySortInput;
import analix.DHIT.model.Apply;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ApplyRepository {
    Apply findById(int applyId);
    void save(Apply apply);

//    void deleteById(int applyId);
//    void update(Apply apply);

    //追記*****************************************************
    //報告一覧表示----------------------------------
    List<Apply> findAll(int employeeCode);

    //ソート検索結果
    List<Apply> sortApply(ApplySortInput applySortInput);

//    int count(int applyId);

    //申請削除
    void deleteById(int applyId);

    //ユーザー削除に伴う全件削除
    void deleteAll(int employeeCode);

    //承認結果を記録する
    void updateApproval(int applyId, int approval);
}
