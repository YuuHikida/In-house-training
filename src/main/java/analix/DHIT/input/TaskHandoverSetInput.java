package analix.DHIT.input;

import analix.DHIT.model.Task;

import java.time.LocalDate;
import java.util.List;

public class TaskHandoverSetInput {

    private int employeeCodeBefore;
    private int employeeCodeAfter;
    private String nameBefore;
    private String nameAfter;
    private int taskId;
    private String taskName;
    private int reportId;
    private  String comment;
    private int progressRate;
    private LocalDate createAt;
    private LocalDate updateAt;

    public int getEmployeeCodeBefore() {
        return employeeCodeBefore;
    }

    public void setEmployeeCodeBefore(int employeeCodeBefore) {
        this.employeeCodeBefore = employeeCodeBefore;
    }

    public String getNameBefore() {
        return nameBefore;
    }

    public void setNameBefore(String nameBefore) {
        this.nameBefore = nameBefore;
    }

    public String getNameAfter() {
        return nameAfter;
    }

    public void setNameAfter(String nameAfter) {
        this.nameAfter = nameAfter;
    }

    public int getEmployeeCodeAfter() {
        return employeeCodeAfter;
    }

    public void setEmployeeCodeAfter(int employeeCodeAfter) {
        this.employeeCodeAfter = employeeCodeAfter;
    }

    public int getTaskId() {
        return taskId;
    }

    public int getReportId() {
        return reportId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getProgressRate() {
        return progressRate;
    }

    public void setProgressRate(int progressRate) {
        this.progressRate = progressRate;
    }

    public LocalDate getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDate createAt) {
        this.createAt = createAt;
    }

    public LocalDate getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDate updateAt) {
        this.updateAt = updateAt;
    }
}
