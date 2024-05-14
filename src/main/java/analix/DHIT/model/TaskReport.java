package analix.DHIT.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name="user_task_report")
public class TaskReport {
    @Id
    @Column(name="task_id")
    private int taskId;
    @Id
    @Column(name="report_id")
    private int reportId;
    @Column(name="handover_flag")
    private int handoverFlag;
    @Column(name="create_at")
    private LocalDate createAt;
    @Column(name="update_at")
    private LocalDate updateAt;

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

    public int getHandoverFlag() {
        return handoverFlag;
    }

    public void setHandoverFlag(int handoverFlag) {
        this.handoverFlag = handoverFlag;
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
