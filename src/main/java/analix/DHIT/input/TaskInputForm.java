package analix.DHIT.input;

import analix.DHIT.model.Task;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class TaskInputForm {
    private List<UserTaskInput> taskInputList = new ArrayList<>()  ;

    public void setTaskInputList(List<UserTaskInput> taskInputList) {
        this.taskInputList = taskInputList;
    }

    public List<UserTaskInput> getTaskInputList() {
        return taskInputList;
    }
}
