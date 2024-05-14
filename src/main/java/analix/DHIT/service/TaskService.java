package analix.DHIT.service;

import analix.DHIT.input.ReportCreateInput;
import analix.DHIT.input.TaskDetailInput;
import analix.DHIT.input.TaskSearchInput;
import analix.DHIT.model.Task;
import analix.DHIT.repository.TaskRepository;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

//=== user_task ==============================================
    //employeeCodeをkeyにレコードを取得する
    public List<Task> selectByEmployeeCode(int employeeCode){
        return taskRepository.selectByEmployeeCode(employeeCode);
    }
    //employeeCodeをkeyにレコードを未達成のみ取得する
    public List<Task> selectByEmployeeCodeNotAchieved(int employeeCode){
        return this.taskRepository.selectByEmployeeCodeNotAchieved(employeeCode);
    }

    //検索条件に基づいてレコードを取得する
    public List<Task> selectSearch(TaskSearchInput taskSearchInput){

        //上限下限が0による全件検索
        if (taskSearchInput.getProgressRateAbove() == 0 && taskSearchInput.getProgressRateBelow() == 0) {
            return this.taskRepository.selectAllFind(taskSearchInput);
        //上限が100、下限が0の場合
        }else if (taskSearchInput.getProgressRateAbove() == 100 && taskSearchInput.getProgressRateBelow() == 0) {
            return this.taskRepository.selectMaXLimitFind(taskSearchInput);
        //上限が1~100、下限が0の場合
        }else if (taskSearchInput.getProgressRateAbove() != 0 && taskSearchInput.getProgressRateBelow() == 0) {
            return this.taskRepository.selectUpperLimitFind(taskSearchInput);
        //下限が1~100、上限が0の場合
        }else if (taskSearchInput.getProgressRateAbove() == 0 && taskSearchInput.getProgressRateBelow() != 0) {
            return this.taskRepository.selectLowerLimitFind(taskSearchInput);
        }
        //上限が1~100、下限が1~100の場合
        return this.taskRepository.selectUpLowLimitFind(taskSearchInput);
    }

    public List<Task>selectTaskAndUserNameByEmployeeCode(int employeeCode, int teamId){
        return this.taskRepository.selectTaskAndUserNameByEmployeeCode(employeeCode, teamId);
    }

    public List<Task>selectSearchTaskAndUserNameByEmployeeCode(TaskSearchInput taskSearchInput){
        //上限下限が0による全件検索
        if (taskSearchInput.getProgressRateAbove() == 0 && taskSearchInput.getProgressRateBelow() == 0) {
            return this.taskRepository.selectTeamAllFind(taskSearchInput);
            //上限が100、下限が0の場合
        }else if (taskSearchInput.getProgressRateAbove() == 100 && taskSearchInput.getProgressRateBelow() == 0) {
            return this.taskRepository.selectTeamMaXLimitFind(taskSearchInput);
            //上限が1~100、下限が0の場合
        }else if (taskSearchInput.getProgressRateAbove() != 0 && taskSearchInput.getProgressRateBelow() == 0) {
            return this.taskRepository.selectTeamUpperLimitFind(taskSearchInput);
            //下限が1~100、上限が0の場合
        }else if (taskSearchInput.getProgressRateAbove() == 0 && taskSearchInput.getProgressRateBelow() != 0) {
            return this.taskRepository.selectTeamLowerLimitFind(taskSearchInput);
        }
        //上限が1~100、下限が1~100の場合
        return this.taskRepository.selectTeamUpLowLimitFind(taskSearchInput);
    }

    //タスクIDをkeyにレコードを取得する
    public Task selectByTaskIdDetail(int taskId){
        return this.taskRepository.selectByTaskIdDetail(taskId);
    }

    //タスクを更新する：単体
    public void updateOneTask(Task task){
        taskRepository.updateUserTask(task);
    }

//=== user_task_report ==============================================
    public boolean countHasTask(int taskId){
        int count = taskRepository.countTaskByTaskId(taskId);
        return  count > 0;
    }

//=== task_log ==============================================
    public List<Task> selectByTaskIdHistory(int taskId){
        return this.taskRepository.selectByTaskIdHistory(taskId);
    }

    public void saveOneTaskLog(Task task){
        taskRepository.saveTaskLogEdit(task, 0);
    }
//=== user_task & user__task_report ============================
    public List<Task> selectReportDetail(int reportId){
        //reportIdからtaskIdを取得する
        List<Integer>taskIdList = taskRepository.selectReportDetail(reportId);
        List<Task>taskList = new ArrayList<>();
        for(int i = 0; i < taskIdList.size(); i++){
            //taskIdから詳細を取得する
            Task task = taskRepository.selectByTaskIdDetail(taskIdList.get(i));
            taskList.add(task);
        }
        return taskList;
    }
//===全てに対して============================================
    public void createTasks(List<Task> taskLogs, int employeeCode, int reportId){

        for (Task taskLog : taskLogs) {
            //taskIdが0か0ではないかで分岐⇒既存or新規
            taskLog.setEmployeeCode(employeeCode);
            taskLog.setCreateAt(LocalDate.now());
            taskLog.setUpdateAt(LocalDate.now());
            if (taskLog.getTaskId() == 0) {
                //  0のとき、Insertでレコードを追加する
                //task_idはオートインクリメントではないので、一旦最大値を取得して、インクリメントする
                int taskId = taskRepository.selectMaxTaskId() + 1;
                taskLog.setTaskId(taskId);
                //　Insert：user_task
                taskRepository.saveUserTask(taskLog);
                //  Insert：user_task_report
                taskRepository.saveUserTaskReport(taskLog,reportId);
                //  Insert：task_log
                taskRepository.saveTaskLogCreate(taskLog,reportId);
            } else {
                //  0ではないとき、UpdateでtaskIdをkeyにcreate_atを除く全データを書き換える
                //　Update：user_task
                taskRepository.updateUserTask(taskLog);
                //  Insert：user_task_report
                taskRepository.saveUserTaskReport(taskLog,reportId);
                //  Insert：task_log
                taskRepository.saveTaskLogEdit(taskLog,reportId);
            }
        }
    }

    public void updateTask(List<Task> taskLogs, int employeeCode, int reportId){
        for (Task taskLog : taskLogs) {
            taskLog.setEmployeeCode(employeeCode);
            taskLog.setCreateAt(LocalDate.now());
            taskLog.setUpdateAt(LocalDate.now());
            if (taskLog.getTaskId() == 0) {
                //  0のとき、Insertでレコードを追加する
                //task_idはオートインクリメントではないので、一旦最大値を取得して、インクリメントする
                int taskId = taskRepository.selectMaxTaskId() + 1;
                taskLog.setTaskId(taskId);
                //　Insert：user_task
                taskRepository.saveUserTask(taskLog);
                //  Insert：user_task_report
                taskRepository.saveUserTaskReport(taskLog,reportId);
                //  Insert：task_log
                taskRepository.saveTaskLogCreate(taskLog,reportId);
            } else {
                //  0ではないとき、UpdateでtaskIdをkeyにcreate_atを除く全データを書き換える
                //　Update：user_task
                taskRepository.updateUserTask(taskLog);
                //  Insert：task_log
                taskRepository.saveTaskLogEdit(taskLog,reportId);
            }
        }
    }

//===Delete関係============================================
    public void deleteTaskByTaskId(int taskId){
        Task task = taskRepository.selectByTaskIdDetail(taskId);
        taskRepository.deleteUserTask(taskId);
        taskRepository.deleteUserTaskReportByTaskId(taskId);
        taskRepository.saveDeleteTask(task, 0);
    }
    public void deleteTask(int reportId){
        //レポートに紐づいたタスクを全て取得
        List<Integer>taskIdList = taskRepository.selectReportDetail(reportId);
        List<Task>taskList = new ArrayList<>();
        for(int i = 0; i < taskIdList.size(); i++){
            //taskIdから詳細を取得する
            Task task = taskRepository.selectByTaskIdDetail(taskIdList.get(i));
            taskList.add(task);
        }

        for(int i = 0; i < taskList.size(); i++) {
            //handover_flagを確認し、「１」であれば削除可能、「2」であれば削除不可能
            int handoverFlag = taskRepository.selectHandoverFlag(taskList.get(i).getTaskId(), reportId);
            // [1]の時1
            if(handoverFlag == 1){
                //該当するデータを削除する
                //Delete：user_task_report
                taskRepository.deleteUserTaskReport(taskList.get(i).getTaskId(), reportId);
                //Delete：user_task
                boolean hasTask = countHasTask(taskList.get(i).getTaskId());
                //紐づくレポートが無い場合にのみタスク自体を削除する
                if (!hasTask){
                    taskRepository.deleteUserTask(taskList.get(i).getTaskId());
                }
                //Insert：task_log
                    //process = 3
                taskList.get(i).setCreateAt(LocalDate.now());
                taskRepository.saveDeleteTask(taskList.get(i),reportId);

            }else if(handoverFlag == 2){
                //削除は実行しないが、report_idを書き換える
                //Update：user_task_report
                    //report_id = 0
                taskRepository.updateDelete(taskList.get(i).getTaskId(), reportId);
            }
        }
    }

}