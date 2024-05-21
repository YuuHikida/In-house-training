package analix.DHIT.input;

import lombok.Data;

@Data
public class UserTaskInput {
    private String task_name;
    private String comment;
    private int progressRate;

    public int getProgressRate() {
        return progressRate;
    }

    public String getComment() {
        return comment;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setProgressRate(int progressRate) {
        this.progressRate = progressRate;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }
}
