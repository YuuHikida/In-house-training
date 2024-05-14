package analix.DHIT.mapper;

import analix.DHIT.input.TaskSearchInput;
import analix.DHIT.model.Task;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TaskMapper {
//=== user_task ==============================================
    //employeeCodeをkeyにレコードを全件取得する
    @Select("SELECT * FROM user_task WHERE employee_code = #{employeeCode}")
    List<Task> selectByEmployeeCode(int employeeCode);
    //employeeCodeをkeyにレコードを未達成のみ取得する
    @Select("SELECT * FROM user_task WHERE employee_code = #{employeeCode} AND progress_rate != 100")
    List<Task> selectByEmployeeCodeNotAchieved(int employeeCode);

    //検索条件に基づいてレコードを取得する自分用
    //上限下限が0による全件検索
    @Select("SELECT * FROM user_task WHERE employee_code = #{taskSearchInput.employeeCode}")
    List<Task> selectAllFind(@Param("taskSearchInput")TaskSearchInput taskSearchInput);
    //上限が100、下限が0の場合
    @Select("SELECT * FROM user_task WHERE employee_code = #{taskSearchInput.employeeCode} AND progress_rate = 100")
    List<Task> selectMaXLimitFind(@Param("taskSearchInput")TaskSearchInput taskSearchInput);
    //上限が1~99、下限が0の場合
    @Select("SELECT * FROM user_task WHERE employee_code = #{taskSearchInput.employeeCode} AND #{taskSearchInput.progressRateAbove} >= progress_rate")
    List<Task> selectUpperLimitFind(@Param("taskSearchInput")TaskSearchInput taskSearchInput);
    //下限が1~99、上限が0の場合
    @Select("SELECT * FROM user_task WHERE employee_code = #{taskSearchInput.employeeCode} AND #{taskSearchInput.progressRateBelow} <= progress_rate")
    List<Task> selectLowerLimitFind(@Param("taskSearchInput")TaskSearchInput taskSearchInput);
    //上限が1~99、下限が1~99の場合
    @Select("SELECT * FROM user_task WHERE employee_code = #{taskSearchInput.employeeCode} AND #{taskSearchInput.progressRateAbove} >= progress_rate AND #{taskSearchInput.progressRateBelow} <= progress_rate")
    List<Task> selectUpLowLimitFind(@Param("taskSearchInput")TaskSearchInput taskSearchInput);

    //userとassignmentとuser_taskを結合して、task情報とユーザー名情報を取得する
    @Select("SELECT t.*,u.name FROM user as u " +
            "JOIN assignment as a ON u.employee_code = a.employee_code " +
            "JOIN user_task as t ON a.employee_code = t.employee_code " +
            "WHERE a.team_id = #{teamId} AND u.employee_code = #{employeeCode}")
    List<Task>selectTaskAndUserNameByEmployeeCode(int employeeCode, int teamId);

    //検索条件に基づいてレコードを取得するチームメンバー用
    //上限下限が0による全件検索
    @Select("SELECT t.*,u.name FROM user as u " +
            "JOIN assignment as a ON u.employee_code = a.employee_code " +
            "JOIN user_task as t ON a.employee_code = t.employee_code " +
            "WHERE a.team_id = #{taskSearchInput.teamId} AND u.employee_code = #{taskSearchInput.employeeCode}")
    List<Task> selectTeamAllFind(@Param("taskSearchInput")TaskSearchInput taskSearchInput);
    //上限が100、下限が0の場合
    @Select("SELECT t.*,u.name FROM user as u " +
            "JOIN assignment as a ON u.employee_code = a.employee_code " +
            "JOIN user_task as t ON a.employee_code = t.employee_code " +
            "WHERE a.team_id = #{taskSearchInput.teamId} AND u.employee_code = #{taskSearchInput.employeeCode} AND t.progress_rate = 100")
    List<Task> selectTeamMaXLimitFind(@Param("taskSearchInput")TaskSearchInput taskSearchInput);
    //上限が1~99、下限が0の場合
    @Select("SELECT t.*,u.name FROM user as u " +
            "JOIN assignment as a ON u.employee_code = a.employee_code " +
            "JOIN user_task as t ON a.employee_code = t.employee_code " +
            "WHERE a.team_id = #{taskSearchInput.teamId} AND u.employee_code = #{taskSearchInput.employeeCode} AND #{taskSearchInput.progressRateAbove} >= t.progress_rate")
    List<Task> selectTeamUpperLimitFind(@Param("taskSearchInput")TaskSearchInput taskSearchInput);
    //下限が1~99、上限が0の場合
    @Select("SELECT t.*,u.name FROM user as u " +
            "JOIN assignment as a ON u.employee_code = a.employee_code " +
            "JOIN user_task as t ON a.employee_code = t.employee_code " +
            "WHERE a.team_id = #{taskSearchInput.teamId} AND u.employee_code = #{taskSearchInput.employeeCode} AND #{taskSearchInput.progressRateBelow} <= t.progress_rate")
    List<Task> selectTeamLowerLimitFind(@Param("taskSearchInput")TaskSearchInput taskSearchInput);
    //上限が1~99、下限が1~99の場合
    @Select("SELECT t.*,u.name FROM user as u " +
            "JOIN assignment as a ON u.employee_code = a.employee_code " +
            "JOIN user_task as t ON a.employee_code = t.employee_code " +
            "WHERE a.team_id = #{taskSearchInput.teamId} AND u.employee_code = #{taskSearchInput.employeeCode} AND #{taskSearchInput.progressRateAbove} >= t.progress_rate AND #{taskSearchInput.progressRateBelow} <= t.progress_rate")
    List<Task> selectTeamUpLowLimitFind(@Param("taskSearchInput")TaskSearchInput taskSearchInput);

    //タスクIDをkeyにレコードを取得する
    @Select("SELECT * FROM user_task WHERE task_id = #{taskId}")
    Task selectByTaskIdDetail(int taskId);

    //task_idはオートインクリメントではないので、一旦最大値を取得している
    @Select("SELECT MAX(task_id) FROM user_task")
    int selectMaxTaskId();

    //タスクの新規作成
    @Insert("INSERT INTO user_task (task_id, employee_code, task_name, comment, progress_rate, create_at) " +
            "VALUES (#{taskId}, #{employeeCode}, #{taskName}, #{comment}, #{progressRate}, #{createAt})")
    void saveUserTask(Task task);
    //タスクの編集・更新
    @Update("UPDATE user_task SET employee_code = #{employeeCode}, task_name = #{taskName}, " +
            "comment = #{comment}, progress_rate = #{progressRate}, update_at = #{updateAt} " +
            "WHERE task_id = #{taskId}")
    void updateUserTask(Task task);

    //タスクの削除
    @Delete("DELETE FROM user_task WHERE task_id = #{taskId}")
    void deleteUserTask(int taskId);

//=== user_task_report ==============================================
    //レポートとタスクの関係性を新規設定
    @Insert("INSERT INTO user_task_report (task_id,report_id, handover_flag, create_at) " +
        "VALUES (#{task.taskId}, #{reportId}, 1, #{task.createAt})")
    void saveUserTaskReport(Task task, int reportId);
    //reportIdからtaskIdを取得する
    @Select("SELECT task_id FROM user_task_report WHERE report_id = #{reportId}")
    List<Integer> selectReportDetail(int reportId);
    //handover_flagを取得する
    @Select("SELECT handover_flag FROM user_task_report WHERE task_id = #{taskId} AND report_id = #{reportId}")
    int selectHandoverFlag(int taskId, int reportId);

    //タスクの削除:taskIdとreportId
    @Delete("DELETE FROM user_task_report WHERE task_id = #{taskId} AND report_id = #{reportId}")
    void deleteUserTaskReport(int taskId, int reportId);
    //タスクの削除:taskId
    @Delete("DELETE FROM user_task_report WHERE task_id = #{taskId}")
    void deleteUserTaskReportByTaskId(int taskId);
    //タスクの削除_引継ぎ中(Flag判定)により、削除はせず、reportIdを0に書き換える
    @Update("UPDATE user_task_report SET report_id = 0 WHERE task_id = #{taskId} AND report_id = #{reportId}")
    void updateDelete(int taskId, int reportId);
    //user_taskを削除するまえに、紐づくレポートが無いか確認する
    @Select("SELECT COUNT(*) FROM user_task_report WHERE task_id = #{taskId}")
    int countTaskByTaskId(int taskId);


    //レポートとタスクの関係性を編集_要らないかも？
//    @Update("UPDATE user_task_report SET " +
//            "handover_flag = 1, update_at = #{task.updateAt} " +
//            "WHERE task_id = #{task.taskId}")
//    void updateUserTaskReport(Task task, int reportId);


//=== task_log ==============================================
    //タスクログからタスクIDをkeyに取得する（詳細ページに使用）
    @Select("SELECT t.task_name, tl.comment, tl.progress_rate, tl.create_at, u.name " +
            "FROM task_log AS tl " +
            "JOIN user_task AS t ON tl.task_id = t.task_id " +
            "JOIN user AS u ON tl.employee_code = u.employee_code " +
            "WHERE tl.task_id = #{taskId}")
    List<Task> selectByTaskIdHistory(int taskId);

    //タスクの変更ログを登録_作成
    @Insert("INSERT INTO task_log (task_id, report_id, process, employee_code, comment, progress_rate, create_at) " +
            "VALUES (#{task.taskId}, #{reportId}, 1, #{task.employeeCode}, #{task.comment}, #{task.progressRate}, #{task.createAt})")
    void saveTaskLogCreate(Task task, int reportId);
    //タスクの変更ログを登録_編集
    @Insert("INSERT INTO task_log (task_id, report_id, process, employee_code, comment, progress_rate, create_at) " +
            "VALUES (#{task.taskId}, #{reportId}, 2, #{task.employeeCode}, #{task.comment}, #{task.progressRate}, #{task.createAt})")
    void saveTaskLogEdit(Task task, int reportId);
    //削除時の削除ログ
    @Insert("INSERT INTO task_log (task_id, report_id, process, employee_code, comment, progress_rate, create_at) " +
            "VALUES (#{task.taskId}, #{reportId}, 3, #{task.employeeCode}, #{task.comment}, #{task.progressRate}, #{task.createAt})")
    void saveDeleteTask(Task task, int reportId);

}
