package analix.DHIT.input;
import analix.DHIT.model.Assignment;
import analix.DHIT.model.Team;

import java.util.ArrayList;
import java.util.List;

public class TeamCreateInput {

    private String name;
    private boolean release;
    //チームに人を入れるために追加
    private String[] employeeCodeIsManager;
    private  String[] employeeCodeIsMember;

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

    public String[] getEmployeeCodeIsManager() {
        return employeeCodeIsManager;
    }

    public String[] getEmployeeCodeIsMember() {
        return employeeCodeIsMember;
    }

    public void setEmployeeCodeIsManager(String[] employeeCodeIsManager) {
        this.employeeCodeIsManager = employeeCodeIsManager;
    }

    public void setEmployeeCodeIsMember(String[] employeeCodeIsMember) {
        this.employeeCodeIsMember = employeeCodeIsMember;
    }
}
