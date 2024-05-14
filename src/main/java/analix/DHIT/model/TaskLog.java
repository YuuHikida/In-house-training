package analix.DHIT.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name="task_log_new")
public class TaskLog {
    @Id
    @Column(name="task_log_id")
    private int taskLogId;
    @Column(name="task_id")
    private int taskId;
    @Column(name="report_id")
    private int reportId;
    @Column(name="employee_code")
    private int employeeCode;
    @Column(name="progress_rate")
    private int progressRate;
    @Column(name="create_at")
    private LocalDate createAt;

    private int process;
    private String comment;

    public int getTaskLogId() {
        return taskLogId;
    }

    public void setTaskLogId(int taskLogId) {
        this.taskLogId = taskLogId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(int employeeCode) {
        this.employeeCode = employeeCode;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }
}
