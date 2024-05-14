package analix.DHIT.repository;

import analix.DHIT.input.TaskSearchInput;
import analix.DHIT.mapper.TaskMapper;
import analix.DHIT.model.Task;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MysqlTaskRepository implements TaskRepository {
    private final TaskMapper taskMapper;
    public MysqlTaskRepository(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

//=== user_task ==============================================
    //employeeCodeをkeyにレコードを取得する
    @Override
    public List<Task> selectByEmployeeCode(int employeeCode){
        return this.taskMapper.selectByEmployeeCode(employeeCode);
    }
    //employeeCodeをkeyにレコードを未達成のみ取得する
    @Override
    public List<Task> selectByEmployeeCodeNotAchieved(int employeeCode){
        return this.taskMapper.selectByEmployeeCodeNotAchieved(employeeCode);
    }

    //検索条件に基づいてレコードを取得する
    //上限下限が0による全件検索
    @Override
    public List<Task> selectAllFind(TaskSearchInput taskSearchInput){
        return this.taskMapper.selectAllFind(taskSearchInput);
    }
    //上限が100、下限が0の場合
    @Override
    public List<Task> selectMaXLimitFind(TaskSearchInput taskSearchInput){
        return this.taskMapper.selectMaXLimitFind(taskSearchInput);
    }
    //上限が1~99、下限が0の場合
    @Override
    public List<Task> selectUpperLimitFind(TaskSearchInput taskSearchInput){
        return this.taskMapper.selectUpperLimitFind(taskSearchInput);
    }
    //下限が1~99、上限が0の場合
    @Override
    public List<Task> selectLowerLimitFind(TaskSearchInput taskSearchInput){
        return this.taskMapper.selectLowerLimitFind(taskSearchInput);
    }
    //上限が1~99、下限が1~99の場合
    @Override
    public List<Task> selectUpLowLimitFind(TaskSearchInput taskSearchInput){
        return this.taskMapper.selectUpLowLimitFind(taskSearchInput);
    }

    //userとassignmentとuser_taskを結合して、task情報とユーザー名情報を取得する
    @Override
    public List<Task>selectTaskAndUserNameByEmployeeCode(int employeeCode, int teamId){
        return this.taskMapper.selectTaskAndUserNameByEmployeeCode(employeeCode, teamId);
    }

    //検索条件に基づいてレコードを取得するチームメンバー用
    //上限下限が0による全件検索
    @Override
    public List<Task> selectTeamAllFind(TaskSearchInput taskSearchInput){
        return this.taskMapper.selectTeamAllFind(taskSearchInput);
    }
    //上限が100、下限が0の場合
    @Override
    public List<Task> selectTeamMaXLimitFind(TaskSearchInput taskSearchInput){
        return this.taskMapper.selectTeamMaXLimitFind(taskSearchInput);
    }
    //上限が1~99、下限が0の場合
    @Override
    public List<Task> selectTeamUpperLimitFind(TaskSearchInput taskSearchInput){
        return this.taskMapper.selectTeamUpperLimitFind(taskSearchInput);
    }
    //下限が1~99、上限が0の場合
    @Override
    public List<Task> selectTeamLowerLimitFind(TaskSearchInput taskSearchInput){
        return this.taskMapper.selectTeamLowerLimitFind(taskSearchInput);
    }
    //上限が1~99、下限が1~99の場合
    @Override
    public List<Task> selectTeamUpLowLimitFind(TaskSearchInput taskSearchInput){
        return this.taskMapper.selectTeamUpLowLimitFind(taskSearchInput);
    }

    //タスクIDをkeyにレコードを取得する
    @Override
    public Task selectByTaskIdDetail(int taskId){
        return this.taskMapper.selectByTaskIdDetail(taskId);
    }

    //タスクの新規作成
    @Override
    public void saveUserTask(Task task){
        this.taskMapper.saveUserTask(task);
    }
    //タスクの編集・更新
    @Override
    public void updateUserTask(Task task){
        this.taskMapper.updateUserTask(task);
    }

    //タスクの削除
    @Override
    public void deleteUserTask(int taskId){
        this.taskMapper.deleteUserTask(taskId);
    }

//=== user_task_report ==============================================
    //レポートとタスクの関係性を新規設定
    @Override
    public void saveUserTaskReport(Task task, int reportId){
        this.taskMapper.saveUserTaskReport(task, reportId);
    }
    //reportIdからtaskIdを取得する
    @Override
    public List<Integer> selectReportDetail(int reportId){
        return this.taskMapper.selectReportDetail(reportId);
    }
    //handoverFlagを取得
    @Override
    public int selectHandoverFlag(int taskId, int reportId){
        return this.taskMapper.selectHandoverFlag(taskId, reportId);
    }

    //タスクの削除:taskIdとreportId
    @Override
    public void deleteUserTaskReport(int taskId, int reportId){
        this.taskMapper.deleteUserTaskReport(taskId,reportId);
    }
    //タスクの削除:taskId
    public void deleteUserTaskReportByTaskId(int taskId){
        this.taskMapper.deleteUserTaskReportByTaskId(taskId);
    }
    //タスクの削除_引継ぎ中(Flag判定)により、削除はせず、reportIdを0に書き換える
    @Override
    public void updateDelete(int taskId, int reportId){
        this.taskMapper.updateDelete(taskId,reportId);
    }
    //user_taskを削除するまえに、紐づくレポートが無いか確認する
    @Override
    public int countTaskByTaskId(int taskId){
        return this.taskMapper.countTaskByTaskId(taskId);
    }

//    //レポートとタスクの関係性を編集
//    public void updateUserTaskReport(Task task, int reportId){
//        this.taskMapper.updateUserTaskReport(task,reportId);
//    }

//=== task_log ==============================================
    //タスクログからタスクIDをkeyに取得する（詳細ページに使用）
    @Override
    public List<Task> selectByTaskIdHistory(int taskId){
        return this.taskMapper.selectByTaskIdHistory(taskId);
    }
    //タスクの変更ログを登録_作成
    @Override
    public void saveTaskLogCreate(Task task, int reportId){
        this.taskMapper.saveTaskLogCreate(task,reportId);
    }
    //タスクの変更ログを登録_編集
    @Override
    public void saveTaskLogEdit(Task task, int reportId){
        this.taskMapper.saveTaskLogEdit(task,reportId);
    }
    //削除時の削除ログ
    @Override
    public void saveDeleteTask(Task task, int reportId){
        this.taskMapper.saveDeleteTask(task,reportId);
    }

//===全てに対して============================================
    //task_idはオートインクリメントではないので、一旦最大値を取得している
    @Override
    public int selectMaxTaskId(){
        return this.taskMapper.selectMaxTaskId();
    }
}
