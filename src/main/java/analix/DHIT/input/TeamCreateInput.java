package analix.DHIT.input;
import analix.DHIT.model.Assignment;
import analix.DHIT.model.Team;

import java.util.ArrayList;
import java.util.List;

public class TeamCreateInput {

    private String name;
    private boolean release;
    //チームに人を入れるために追加
    private int[] employeeCodeIsManager;
    private  int[] employeeCodeIsMember;

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

    public void setEmployeeCodeIsManager(int[] employeeCodeIsManager) {
        this.employeeCodeIsManager = employeeCodeIsManager;
    }

    public int[] getEmployeeCodeIsManager() {
        return employeeCodeIsManager;
    }

    public void setEmployeeCodeIsMember(int[] employeeCodeIsMember) {
        this.employeeCodeIsMember = employeeCodeIsMember;
    }

    public int[] getEmployeeCodeIsMember() {
        return employeeCodeIsMember;
    }
}
