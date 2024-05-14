package analix.DHIT.repository;

import analix.DHIT.input.*;
import analix.DHIT.mapper.HandoverMapper;
import analix.DHIT.model.Task;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MysqlHandoverRepository implements HandoverRepository {
    private final HandoverMapper handoverMapper;

    public MysqlHandoverRepository(HandoverMapper handoverMapper){ this.handoverMapper = handoverMapper; }


    @Override
    //teamIdからemployeeCodeと名前を取得
    public List<TaskHandoverInput>selectByTeamId(int teamId){
        return this.handoverMapper.selectByTeamId(teamId);
    }

    @Override
    //employeeCodeからtaskを取得する
    public List<Task>selectByEmployeeCode(int employeeCode){
        return this.handoverMapper.selectByEmployeeCode(employeeCode);
    }

    @Override
    //user_taskのタスク所持者(employee_code)を変更する
    public void updateUserTask(TaskHandoverSetInput taskHandoverSetInput){
        this.handoverMapper.updateUserTask(taskHandoverSetInput);
    }

    @Override
    //user_task_reportの引継ぎフラグを変更する(引継ぎ中)
    public void updateOnFlag(TaskHandoverSetInput taskHandoverSetInput){
        this.handoverMapper.updateOnFlag(taskHandoverSetInput);
    }

    @Override
    //taskIdからreportIdを取得する
    public int selectReportIdUsingTaskId(int taskId){
        return this.handoverMapper.selectReportIdUsingTaskId(taskId);
    }

    @Override
    //引継ぎ履歴をログに登録：受け渡し
    public void saveHandoverAfterLog(TaskHandoverSetInput taskHandoverSetInput){
        this.handoverMapper.saveHandoverAfterLog(taskHandoverSetInput);
    }
    @Override
    //引継ぎ履歴をログに登録：受け取り
    public void saveHandoverBeforeLog(TaskHandoverSetInput taskHandoverSetInput){
        this.handoverMapper.saveHandoverBeforeLog(taskHandoverSetInput);
    }
}
