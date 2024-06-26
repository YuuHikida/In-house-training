package analix.DHIT.mapper;

import analix.DHIT.input.ApplySortInput;
import analix.DHIT.input.ReportSortInput;
import analix.DHIT.model.Apply;
import analix.DHIT.model.Report;
import analix.DHIT.model.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ApplyMapper {

    // 勤怠申請登録
    @Insert("INSERT INTO apply(employee_code, application_type, attendance_type, start_date, end_date, start_time, end_time, reason, approval, created_date) " +
            "VALUES(#{employeeCode}, #{applicationType}, #{attendanceType}, #{startDate}, #{endDate}, #{startTime}, #{endTime}, #{reason}, #{approval},#{createdDate})")
    @Options(useGeneratedKeys = true, keyProperty = "apply_id")
    void insertApply(Apply apply);

    // エラー回避
    @Select("SELECT * FROM apply WHERE apply_id = #{applyId}")
    Apply SelectById(int applyId);

    // 一覧表示
    @Select("SELECT * FROM apply WHERE employee_code=#{employeeCode}")
    List<Apply> selectAll(int employeeCode);


    //申請削除
    @Delete("DELETE FROM apply WHERE apply_id = #{applyId}")
    void deleteById(int applyId);

    //ユーザー削除に伴う全件削除
    @Delete("DELETE FROM apply WHERE employee_code = #{employeeCode}")
    void deleteAll(int employeeCode);

    @Select("SELECT apply_id FROM report WHERE employee_code = #{employeeCode} and created_date = #{createdDate}")
    String selectIdByEmployeeCodeAndCreatedDate(int employeeCode, LocalDateTime createdDate);

    @Select("SELECT * FROM apply as a " +
            "WHERE " +
//            "(#{applySortInput.createdDate} IS NULL OR DATE_FORMAT(a.createdDate,'%Y-%m')=DATE_FORMAT(#{applySortInput.createdDate},'%Y-%m')) " +
//            "AND " +
            "(a.employee_code=#{applySortInput.employeeCode}) ")
//            "AND " +
//            "((#{applySortInput.feedback} IS NULL) " +
//            "OR (#{reportSortInput.feedback} IS TRUE AND f.feedback_id IS NOT NULL) " +
//            "OR (#{reportSortInput.feedback} IS FALSE AND f.feedback_id IS NULL))"

    List<Apply> sortApply(@Param("applySortInput") ApplySortInput applySortInput);

    @Select("SELECT a.* FROM apply AS a " +
            "INNER JOIN assignment AS s ON a.employee_code = s.employee_code " +
            "WHERE s.team_id = #{teamId} AND s.is_manager = 0 " +
            "ORDER BY a.created_date")
    List<Apply> getOtherMembers(int teamId);

    //承認結果を記録する
    @Update("UPDATE apply SET approval = #{approval} WHERE apply_id = #{applyId}")
    void updateApproval(int applyId, int approval);

}
