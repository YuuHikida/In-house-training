package analix.DHIT.service;

import analix.DHIT.input.*;
import analix.DHIT.model.Task;
import analix.DHIT.repository.HandoverRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HandoverService {
   private final HandoverRepository handoverRepository;

   public HandoverService(HandoverRepository handoverRepository) {
       this.handoverRepository = handoverRepository;
   }

    //teamIdからemployeeCodeと名前を取得
    public List<TaskHandoverInput>selectByTeamId(int teamId){
        return this.handoverRepository.selectByTeamId(teamId);
    }

    //employeeCodeからtaskを取得する
    public List<Task>selectByEmployeeCode(int employeeCode){
        return this.handoverRepository.selectByEmployeeCode(employeeCode);
    }

    //user_taskのタスク所持者(employee_code)を変更する
    public void updateUserTask(TaskHandoverSetInput taskHandoverSetInput){
       this.handoverRepository.updateUserTask(taskHandoverSetInput);
    }

    //user_task_reportの引継ぎフラグを変更する(引継ぎ中)
    public void updateOnFlag(TaskHandoverSetInput taskHandoverSetInput){
        this.handoverRepository.updateOnFlag(taskHandoverSetInput);
    }

    //taskIdからreportIdを取得する
    public int selectReportIdUsingTaskId(int taskId){
        return this.handoverRepository.selectReportIdUsingTaskId(taskId);
    }

    //引継ぎ履歴をログに登録：
    public void saveHandoverLog(TaskHandoverSetInput taskHandoverSetInput){
        //受け渡し
        this.handoverRepository.saveHandoverAfterLog(taskHandoverSetInput);
        //受け取り
        this.handoverRepository.saveHandoverBeforeLog(taskHandoverSetInput);
    }


}
