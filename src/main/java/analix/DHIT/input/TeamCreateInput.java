package analix.DHIT.input;
import analix.DHIT.model.Assignment;
import analix.DHIT.model.Team;

import java.util.ArrayList;
import java.util.List;

public class TeamCreateInput {

    private String name;
    private boolean release;
    //チームに人を入れるために追加
    private List<Integer> employeeCodeIsManager;
    private  List<Integer> employeeCodeIsMember;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean getRelase(){
        return release;
    }
    public void setRelease(boolean release){
        this.release=release;
    }

    public List<Integer> getEmployeeCodeIsManager() {
        return employeeCodeIsManager;
    }

    public List<Integer> getEmployeeCodeIsMember() {
        return employeeCodeIsMember;
    }

    public void setEmployeeCodeIsManager(List<Integer> employeeCodeIsManager) {
        this.employeeCodeIsManager = employeeCodeIsManager;
    }

    public void setEmployeeCodeIsMember(List<Integer> employeeCodeIsMember) {
        this.employeeCodeIsMember = employeeCodeIsMember;
    }
}
