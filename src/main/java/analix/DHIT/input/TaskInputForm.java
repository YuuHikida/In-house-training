package analix.DHIT.input;

import java.util.ArrayList;
import java.util.List;

public class TaskInputForm {
    private List<UserTaskInput> taskInputList = new ArrayList<>();

    public List<UserTaskInput> getTaskInputList() {
        return taskInputList;
    }

    public void setTaskInputList(List<UserTaskInput> taskInputList) {
        this.taskInputList = taskInputList;
    }
}
