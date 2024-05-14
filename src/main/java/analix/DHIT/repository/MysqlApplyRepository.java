package analix.DHIT.repository;

//import analix.DHIT.input.ReportSortInput;
import analix.DHIT.input.ApplySortInput;
import analix.DHIT.input.ReportSortInput;
import analix.DHIT.mapper.ApplyMapper;
import analix.DHIT.model.Apply;
import analix.DHIT.model.Report;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MysqlApplyRepository implements ApplyRepository {
    private final ApplyMapper applyMapper;

    public MysqlApplyRepository(ApplyMapper applyMapper) {
        this.applyMapper = applyMapper;
    }

    @Override
    public Apply findById(int applyId) {
        return this.applyMapper.SelectById(applyId);
    }

    @Override
    public void save(Apply apply) {
        this.applyMapper.insertApply(apply);
    }

    @Override
    public List<Apply> findAll(int employeeCode) {
        return this.applyMapper.selectAll(employeeCode);
    }

    @Override
    public List<Apply> sortApply(ApplySortInput applySortInput) {
        return this.applyMapper.sortApply(applySortInput);
    }


    @Override
    public void deleteById(int applyId){
        this.applyMapper.deleteById(applyId);
    }

    @Override
    //ユーザー削除に伴う全件削除
    public void deleteAll(int employeeCode){
        this.applyMapper.deleteAll(employeeCode);
    }

    //承認結果を記録する
    @Override
    public void updateApproval(int applyId, int approval){
        this.applyMapper.updateApproval(applyId,approval);
    }
}

