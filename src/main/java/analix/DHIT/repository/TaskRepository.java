package analix.DHIT.repository;

import analix.DHIT.input.TaskSearchInput;
import analix.DHIT.model.Task;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface TaskRepository {
//=== user_task ==============================================
    //employeeCodeをkeyにレコードを取得する
    List<Task> selectByEmployeeCode(int employeeCode);
    //employeeCodeをkeyにレコードを未達成のみ取得する
    List<Task> selectByEmployeeCodeNotAchieved(int employeeCode);

    //検索条件に基づいてレコードを取得する自分用
    //上限下限が0による全件検索
    List<Task> selectAllFind(@Param("taskSearchInput")TaskSearchInput taskSearchInput);
    //上限が100、下限が0の場合
    List<Task> selectMaXLimitFind(@Param("taskSearchInput")TaskSearchInput taskSearchInput);
    //上限が1~99、下限が0の場合
    List<Task> selectUpperLimitFind(@Param("taskSearchInput")TaskSearchInput taskSearchInput);
    //下限が1~99、上限が0の場合
    List<Task> selectLowerLimitFind(@Param("taskSearchInput")TaskSearchInput taskSearchInput);
    //上限が1~99、下限が1~99の場合
    List<Task> selectUpLowLimitFind(@Param("taskSearchInput")TaskSearchInput taskSearchInput);

    //userとassignmentとuser_taskを結合して、task情報とユーザー名情報を取得する
    List<Task>selectTaskAndUserNameByEmployeeCode(int employeeCode, int teamId);

    //検索条件に基づいてレコードを取得するチームメンバー用
    //上限下限が0による全件検索
    List<Task> selectTeamAllFind(@Param("taskSearchInput")TaskSearchInput taskSearchInput);
    //上限が100、下限が0の場合
    List<Task> selectTeamMaXLimitFind(@Param("taskSearchInput")TaskSearchInput taskSearchInput);
    //上限が1~99、下限が0の場合
    List<Task> selectTeamUpperLimitFind(@Param("taskSearchInput")TaskSearchInput taskSearchInput);
    //下限が1~99、上限が0の場合
    List<Task> selectTeamLowerLimitFind(@Param("taskSearchInput")TaskSearchInput taskSearchInput);
    //上限が1~99、下限が1~99の場合
    List<Task> selectTeamUpLowLimitFind(@Param("taskSearchInput")TaskSearchInput taskSearchInput);

    //タスクIDをkeyにレコードを取得する
    Task selectByTaskIdDetail(int taskId);

    //タスクの新規作成
    void saveUserTask(Task task);
    //タスクの編集・更新
    void updateUserTask(Task task);

    //タスクの削除
    void deleteUserTask(int taskId);


//=== user_task_report ==============================================
    //レポートとタスクの関係性を新規設定
    void saveUserTaskReport(Task task, int reportId);
    //reportIdからtaskIdを取得する
    List<Integer> selectReportDetail(int reportId);
    //handoverFlagを取得
    int selectHandoverFlag(int taskId, int reportId);

    //タスクの削除:taskIdとreportId
    void deleteUserTaskReport(int taskId, int reportId);
    //タスクの削除:taskId
    void deleteUserTaskReportByTaskId(int taskId);
    //タスクの削除_引継ぎ中(Flag判定)により、削除はせず、reportIdを0に書き換える
    void updateDelete(int taskId, int reportId);
    //user_taskを削除するまえに、紐づくレポートが無いか確認する
    int countTaskByTaskId(int taskId);

//    //レポートとタスクの関係性を編集
//    void updateUserTaskReport(Task task, int reportId);

//=== task_log ==============================================
    //タスクログからタスクIDをkeyに取得する（詳細ページに使用）
    List<Task> selectByTaskIdHistory(int taskId);
    //タスクの変更ログを登録_作成
    void saveTaskLogCreate(Task task, int reportId);
    //タスクの変更ログを登録_編集
    void saveTaskLogEdit(Task task, int reportId);
    //削除時の削除ログ
    void saveDeleteTask(Task task, int reportId);



//===全てに対して============================================
    //task_idはオートインクリメントではないので、一旦最大値を取得している
    @Select("SELECT MAX(task_id) FROM user_task")
    int selectMaxTaskId();
}

