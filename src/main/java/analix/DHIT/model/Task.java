package analix.DHIT.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name="user_task")
public class Task {
    @Id
    @Column(name="task_id")
    private int taskId;
    @Column(name="employee_code")
    private int employeeCode;
    @Column(name="task_name")
    private String taskName;
    @Column(name="progress_rate")
    private int progressRate;
    @Column(name="create_at")
    private LocalDate createAt;
    @Column(name="update_at")
    private LocalDate updateAt;

    private String comment;

    //DBには関係ないけど持たせたい
    private String name;

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(int employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
