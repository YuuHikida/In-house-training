package analix.DHIT.repository;

import analix.DHIT.input.*;
import analix.DHIT.model.Task;
import org.apache.ibatis.annotations.Insert;

import java.util.List;

public interface HandoverRepository {


    //teamIdからemployeeCodeと名前を取得
    List<TaskHandoverInput>selectByTeamId(int teamId);

    //employeeCodeからtaskを取得する
    List<Task>selectByEmployeeCode(int employeeCode);

    //user_taskのタスク所持者(employee_code)を変更する
    void updateUserTask(TaskHandoverSetInput taskHandoverSetInput);

    //user_task_reportの引継ぎフラグを変更する(引継ぎ中)
    void updateOnFlag(TaskHandoverSetInput taskHandoverSetInput);

    //taskIdからreportIdを取得する
    int selectReportIdUsingTaskId(int taskId);

    //引継ぎ履歴をログに登録：受け渡し
    void saveHandoverAfterLog(TaskHandoverSetInput taskHandoverSetInput);
    //引継ぎ履歴をログに登録：受け取り
    void saveHandoverBeforeLog(TaskHandoverSetInput taskHandoverSetInput);
}

