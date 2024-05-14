package analix.DHIT.mapper;

import analix.DHIT.input.TaskHandoverInput;
import analix.DHIT.input.TaskHandoverSetInput;
import analix.DHIT.input.TaskSearchInput;
import analix.DHIT.model.Task;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface HandoverMapper {

    //teamIdからemployeeCodeと名前を取得
    @Select("SELECT u.employee_code, u.name FROM assignment AS a " +
            "LEFT JOIN user AS u ON a.employee_code = u.employee_code " +
            "WHERE a.team_id = #{teamId} AND a.is_manager = 0")
    List<TaskHandoverInput>selectByTeamId(int teamId);

    //employeeCodeからtaskを取得する
    @Select("SELECT * FROM user_task WHERE employee_code = #{employeeCode}")
    List<Task>selectByEmployeeCode(int employeeCode);

    //user_taskのタスク所持者(employee_code)を変更する
    @Update("UPDATE user_task SET " +
            "employee_code = #{employeeCodeAfter}, " +
            "update_at = #{updateAt}" +
            "WHERE task_id = #{taskId}")
    void updateUserTask(TaskHandoverSetInput taskHandoverSetInput);

    //user_task_reportの引継ぎフラグを変更する(引継ぎ中)
    @Update("UPDATE user_task_report SET " +
            "handover_flag = 2, update_at = #{updateAt} " +
            "WHERE task_id = #{taskId}")
    void updateOnFlag(TaskHandoverSetInput taskHandoverSetInput);

    //taskIdからreportIdを取得する
    @Select("SELECT report_id FROM user_task_report " +
            "WHERE task_id = #{taskId}")
    int selectReportIdUsingTaskId(int taskId);

    //引継ぎ履歴をログに登録：受け渡し
    @Insert("INSERT INTO task_log (task_id, report_id, process, employee_code, comment, progress_rate, create_at) " +
            "VALUES (#{taskId}, #{reportId}, 4, #{employeeCodeAfter}, #{comment}, #{progressRate}, #{createAt})")
    void saveHandoverAfterLog(TaskHandoverSetInput taskHandoverSetInput);
    //引継ぎ履歴をログに登録：受け取り
    @Insert("INSERT INTO task_log (task_id, report_id, process, employee_code, comment, progress_rate, create_at) " +
            "VALUES (#{taskId}, #{reportId}, 5, #{employeeCodeBefore}, #{comment}, #{progressRate}, #{createAt})")
    void saveHandoverBeforeLog(TaskHandoverSetInput taskHandoverSetInput);

}
