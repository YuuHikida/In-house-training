package analix.DHIT.controller;


import analix.DHIT.input.*;
import analix.DHIT.model.*;
import analix.DHIT.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/member")
public class MemberController {

    private final UserService userService;
    private final ReportService reportService;
    private final FeedbackService feedbackService;
    private final AssignmentService assignmentService;
    private final TeamService teamService;
    private final SettingService settingService;
    private final MailService mailService;
    private final ApplyService applyService;
    private final HandoverService handoverService;
    private final TaskService taskService;


    //    @Autowired
    public MemberController(UserService userService,
                            ReportService reportService,
                            FeedbackService feedbackService,
                            AssignmentService assignmentService,
                            TeamService teamService,
                            SettingService settingService,
                            MailService mailService,
                            ApplyService applyService,
                            HandoverService handoverService,
                            TaskService taskService) {
        this.userService = userService;
        this.reportService = reportService;
        this.feedbackService = feedbackService;
        this.assignmentService = assignmentService;
        this.teamService = teamService;
        this.settingService = settingService;
        this.mailService = mailService;
        this.applyService = applyService;
        this.handoverService = handoverService;
        this.taskService = taskService;
    }

    @GetMapping("/report/create")
    public String displayReportCreate(
            Model model,
            LocalDate targetDate
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //バリデーションInteger
        int employeeCode = Integer.parseInt(authentication.getName());
        //employeeCodeを使用し、直近のreportがあるか調べる(取得)
        String latestReportId = reportService.getLatestIdByEmployeeCode(employeeCode);
        ReportCreateInput reportCreateInput = new ReportCreateInput();
        SettingInput settingInput = new SettingInput();
        //java.timeパッケージから現在の時刻を取得
        reportCreateInput.setDate(LocalDate.now());
        if (targetDate != null) {
            reportCreateInput.setDate(targetDate);
        }
        String title = "報告作成";
        model.addAttribute("title", title);
        //規定の終業時間を取得し、セット
        Setting setting = settingService.getSettingTime(employeeCode);
        settingInput.setStartTime(setting.getStartTime());
        settingInput.setEndTime(setting.getEndTime());
        settingInput.setEmployment(false);
        model.addAttribute("settingInput", settingInput);

        //自分のタスクを取得する(未達成のみ)
        List<Task> tasks = this.taskService.selectByEmployeeCodeNotAchieved(employeeCode);
        reportCreateInput.setTaskLogs(tasks);
        model.addAttribute("reportCreateInput", reportCreateInput);
        return "member/report-create";
    }

    //↓Transactionalはトランザクション処理で一連の流れが失敗した場合ロールバックする
    @Transactional
    @PostMapping("/report/create")
    public String createReport(ReportCreateInput reportCreateInput, RedirectAttributes redirectAttributes, SettingInput settingInput, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        //遅刻早退を判定
        Setting setting = settingService.getSettingTime(employeeCode);

        if (!settingInput.getEmployment()) {
            if (settingInput.getStartTime().isAfter(setting.getStartTime()) || settingInput.getEndTime().isBefore(setting.getEndTime())) {
                String reason = "";
                if (settingInput.getStartTime().isAfter(setting.getStartTime()) && settingInput.getEndTime().isBefore(setting.getEndTime())) {
                    reason = "※遅刻 及び 早退の理由を記入してください";
                    reportCreateInput.setIsLateness(true);
                    reportCreateInput.setIsLeftEarly(true);
                } else if (settingInput.getStartTime().isAfter(setting.getStartTime())) {
                    reason = "※遅刻の理由を記入してください";
                    reportCreateInput.setIsLateness(true);
                } else if (settingInput.getEndTime().isBefore(setting.getEndTime())) {
                    reason = "※早退の理由を記入してください";
                    reportCreateInput.setIsLeftEarly(true);
                }

                settingInput.setEmployment(true);

                model.addAttribute("settingInput", settingInput);
                model.addAttribute("reportCreateInput", reportCreateInput);
                String title = "報告作成";
                model.addAttribute("title", title);
                model.addAttribute("reason", reason);
                return "member/report-create";
            }
        }

        if (reportService.existsReport(employeeCode, reportCreateInput.getDate())) {
            redirectAttributes.addFlashAttribute("error", reportCreateInput.getDate() + "は既に業務報告書が存在しています");
            return "redirect:/member/report/create";
        }

        //遅刻・早退判定
        if (settingInput.getStartTime().isAfter(setting.getStartTime()) && settingInput.getEndTime().isBefore(setting.getEndTime())) {
            reportCreateInput.setIsLateness(true);
            reportCreateInput.setIsLeftEarly(true);
        } else if (settingInput.getStartTime().isAfter(setting.getStartTime())) {
            reportCreateInput.setIsLateness(true);
        } else if (settingInput.getEndTime().isBefore(setting.getEndTime())) {
            reportCreateInput.setIsLeftEarly(true);
        }

        //newReportIdには新たにInsertされたreportのIDが入る
        int newReportId = reportService.create(
                employeeCode,
                reportCreateInput.getCondition(),
                reportCreateInput.getImpressions(),
                reportCreateInput.getTomorrowSchedule(),
                reportCreateInput.getDate(),
                reportCreateInput.getEndTime(),
                reportCreateInput.getStartTime(),
                reportCreateInput.getIsLateness(),
                reportCreateInput.getLatenessReason(),
                reportCreateInput.getIsLeftEarly(),
                reportCreateInput.getConditionRate()
        );

        // タスクが存在するならタスクログに追加
        if (reportCreateInput.getTaskLogs() != null) {
            List<Task> taskLogs = reportCreateInput.getTaskLogs();
            taskService.createTasks(taskLogs, employeeCode, newReportId);
        }

        return "redirect:/member/report/create-completed";
    }

    @GetMapping("/report/create-completed")
    public String displayReportCreateCompleted(
    ) {
        return "member/report-create-completed";
    }

    @GetMapping("/report-search")
    public String displayReportSearch(
            Model model
    ) {


        String title = "報告一覧";
        model.addAttribute("title", title);

        model.addAttribute("reportSearchInput", new ReportSearchInput());
        model.addAttribute("error", model.getAttribute("error"));

        //追記*****************************************************

        //ログイン中のユーザーのemployeeCodeを取得する
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());
        User member = userService.getUserByEmployeeCode(employeeCode);
        model.addAttribute("member", member);
        //報告一覧表示---------------------------------
        List<Report> reports = reportService.getfindAll(employeeCode);
//        model.addAttribute("reports", reports);

        //検索機能---------------------------------------
        //既読or未読
        for (Report report : reports) {
            boolean isFeedbackGiven = feedbackService.count(report.getId());
            report.setReadStatus(isFeedbackGiven ? "既読" : "未読");
        }
        model.addAttribute("reports", reports);
        //年月で重複しないList作成
        List<LocalDate> dateList = reports.stream()
                .map(Report::getDate)
                .map(date -> date.withDayOfMonth(1))
                .distinct()
                .toList();
        model.addAttribute("dateList", dateList);
        //データ格納用
        model.addAttribute("reportSortInput", new ReportSortInput());
        //追記*****************************************************

        return "member/report-search";
    }

    @PostMapping("/search-report")
    public String searchReport(
            ReportSearchInput reportSearchInput,
            RedirectAttributes redirectAttributes,
            ReportSortInput reportSortInput,
            Model model
    ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        String reportId = reportService.searchId(
                employeeCode,
                reportSearchInput.getDate()
        );

//追記*****************************************************
        //日付、、
        if (reportSortInput.getSort() == true) {
            reportSortInput.setEmployeeCode(employeeCode);

            //ソート用
            List<Report> reports = reportService.getSorrtReport(reportSortInput);
            User member = userService.getUserByEmployeeCode(employeeCode);
            for (Report report : reports) {
                boolean isFeedbackGiven = feedbackService.count(report.getId());
                report.setReadStatus(isFeedbackGiven ? "既読" : "未読");
            }
            model.addAttribute("member", member);
            model.addAttribute("reportSearchInput", new ReportSearchInput());
            model.addAttribute("error", model.getAttribute("error"));
            model.addAttribute("reports", reports);
            //年月で重複しないList作成
            List<LocalDate> dateList = reports.stream()
                    .map(Report::getDate)
                    .map(date -> date.withDayOfMonth(1))
                    .distinct()
                    .toList();
            model.addAttribute("dateList", dateList);
            //データ格納用
            model.addAttribute("reportSortInput", new ReportSortInput());
            return "member/report-search";
        }
//追記*****************************************************


        if (reportId == null) {
            redirectAttributes.addFlashAttribute("error", "選択された日付に提出されたレポートはありません");
            return "redirect:/member/report-search";
        }

        redirectAttributes.addAttribute("reportId", reportId);
        return "redirect:/member/reports/{reportId}";
    }

    @GetMapping("/reports/{reportId}")
    public String displayReportDetail(@PathVariable("reportId") int reportId, FeedbackUpdateInput feedbackUpdateInput, Model model, Boolean del) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        Report report = reportService.getReportById(reportId);
//        if (report.getEmployeeCode() != employeeCode) {
//            return "redirect:/member/report/create";
//        }

        //Feedback feedback = feedbackService.getFeedbackById(reportId);
        //Assignment assignment = assignmentService.getAssignmentByEmployeeCode(employeeCode);

        List<Task> taskLogs = taskService.selectReportDetail(reportId);
        User member = userService.getUserByEmployeeCode(report.getEmployeeCode());

        model.addAttribute("report", report);
        model.addAttribute("taskLogs", taskLogs);
        model.addAttribute("member", member);
        //model.addAttribute("feedback", feedback);

        model.addAttribute("beforeReportId", reportService.getBeforeIdById(reportId));
        model.addAttribute("afterReportId", reportService.getAfterIdById(reportId));

        String date = report.getDate().format(DateTimeFormatter.ofPattern("yyyy年M月d日(E)", Locale.JAPANESE));
        model.addAttribute("date", date);

        //フィードバック用追記
        //レポートが所持しているemployeeCode
        int reportByEmployeeCode = report.getEmployeeCode();

        //レポートのemployeeCodeとログインユーザーのemployeeCodeが一致ならフィードバックを閲覧する
        //不一致なら
        //レポート持ち主のemployeecodeで検索して、ismanager falseのアサインメントがあるかどうかチェック→assignmentがあったらTrueなければfalse
        if (employeeCode == reportByEmployeeCode) {
            boolean isMgr = false;
            model.addAttribute("isManager", isMgr);
        } else {
            boolean isMgr = assignmentService.getCountIsManager(employeeCode, reportId);
            model.addAttribute("isManager", isMgr);
        }

        if (del != null && del) {
            feedbackService.deleteById(reportId);
        }

        if (feedbackUpdateInput.getComment() != null && !feedbackService.count(reportId)) {
            feedbackUpdateInput.setNameByEmployeeCode(employeeCode, userService);
            feedbackUpdateInput.setReportId(reportId);
            feedbackService.create(feedbackUpdateInput);
            model.addAttribute("feedback", feedbackUpdateInput);
        } else if (feedbackService.count(reportId)) {
            Feedback feedback = feedbackService.getFeedbackById(reportId);


            model.addAttribute("feedback", feedback);
        }

        return "member/report-detail";
    }

//    @PostMapping("/reports/{reportId}/delete/list")
//    @Transactional
//    public String listTransitionAfterDeleteReport(
//            @PathVariable int reportId
//    ) {
//        Report report = reportService.getReportById(reportId);
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        int employeeCode = Integer.parseInt(authentication.getName());
//
//        if (report.getEmployeeCode() != employeeCode) {
//            return "redirect:/member/report/create";
//        }
//        //this.feedbackService.deleteById(reportId);
//        this.taskService.deleteTask(reportId);
//        this.reportService.deleteById(reportId);
//
//
//        return "redirect:/member/report-search";
//    }

    @GetMapping("/reports/{reportId}/delete")
    @Transactional
    public String deleteReport(
            @PathVariable int reportId
    ) {
        Report report = reportService.getReportById(reportId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        if (report.getEmployeeCode() != employeeCode) {
            return "redirect:/member/report/create";
        }

        this.feedbackService.deleteById(reportId);
        this.taskService.deleteTask(reportId);
        this.reportService.deleteById(reportId);

        return "redirect:/member/report/delete-completed";
    }

    @GetMapping("/report/delete-completed")
    public String displayReportDeleteCompleted(
    ) {
        return "member/report-delete-completed";
    }

    @GetMapping("/reports/{reportId}/edit")
    public String displayReportEdit(
            Model model,
            @PathVariable int reportId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        Report report = this.reportService.getReportById(reportId);

        String title = "報告編集";
        model.addAttribute("title", title);

        if (report.getEmployeeCode() != employeeCode) {
            return "redirect:/member/report/create";
        }

        //遅刻・早退　関係
        Setting setting = this.settingService.getSettingTime(employeeCode);
        SettingInput settingInput = new SettingInput();
        String reason = "";
        if (report.getStartTime().isAfter(setting.getStartTime()) && report.getEndTime().isBefore(setting.getEndTime())) {
            settingInput.setEmployment(true);
            reason = "※遅刻 及び 早退の理由を記入してください";
        } else if (report.getStartTime().isAfter(setting.getStartTime())) {
            settingInput.setEmployment(true);
            reason = "※遅刻の理由を記入してください";
        } else if (report.getEndTime().isBefore(setting.getEndTime())) {
            settingInput.setEmployment(true);
            reason = "※早退の理由を記入してください";
        }

        model.addAttribute("reason", reason);
        model.addAttribute("settingInput", settingInput);


        List<Task> taskLogs = taskService.selectReportDetail(reportId);

        model.addAttribute("report", report);
        model.addAttribute("taskLogs", taskLogs);
        model.addAttribute("reportUpdateInput", new ReportUpdateInput());

        return "member/report-edit";

    }

    @Transactional
    @PostMapping("/report/update")
    public String updateReport(ReportUpdateInput reportUpdateInput, RedirectAttributes redirectAttributes, SettingInput settingInput, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        Report report = this.reportService.getReportById(reportUpdateInput.getReportId());

        if (report.getEmployeeCode() != employeeCode) {
            return "redirect:/member/report/create";
        }

        //遅刻・早退関係
        Setting setting = settingService.getSettingTime(employeeCode);
        String reason = "";
        if (reportUpdateInput.getStartTime().isAfter(setting.getStartTime()) && reportUpdateInput.getEndTime().isBefore(setting.getEndTime())) {
            reportUpdateInput.setIsLateness(true);
            reportUpdateInput.setIsLeftEarly(true);
            reason = "※遅刻 及び 早退の理由を記入してください。";
        } else if (reportUpdateInput.getStartTime().isAfter(setting.getStartTime())) {
            reportUpdateInput.setIsLateness(true);
            reason = "※遅刻の理由を記入してください";
        } else if (reportUpdateInput.getEndTime().isBefore(setting.getEndTime())) {
            reportUpdateInput.setIsLeftEarly(true);
            reason = "※早退の理由を記入してください";
        } else {
            reportUpdateInput.setLatenessReason(null);
        }

        if (reportUpdateInput.getIsLeftEarly() || reportUpdateInput.getIsLateness()) {
            if (reportUpdateInput.getLatenessReason() == null) {
                report.setStartTime(settingInput.getStartTime());
                report.setEndTime(settingInput.getEndTime());
                settingInput.setEmployment(true);
                model.addAttribute("reason", reason);
                model.addAttribute("reportUpdateInput", reportUpdateInput);
                model.addAttribute("settingInput", settingInput);
                String title = "報告編集";
                model.addAttribute("title", title);
                model.addAttribute("report", report);
                List<Task> taskLogs = this.taskService.selectReportDetail(reportUpdateInput.getReportId());
                model.addAttribute("taskLogs", taskLogs);
                return "member/report-edit";
            }
        }

        this.reportService.update(reportUpdateInput);
        // タスクが存在するならタスクログに追加
        if (reportUpdateInput.getTaskLogs() != null) {
            List<Task> taskLogs = reportUpdateInput.getTaskLogs();
            taskService.updateTask(taskLogs, employeeCode, reportUpdateInput.getReportId());
        }

        redirectAttributes.addAttribute("reportId", reportUpdateInput.getReportId());
        redirectAttributes.addFlashAttribute("editCompleteMSG", "報告を編集しました。");
        return "redirect:/member/reports/{reportId}";

    }


    @GetMapping("/taskMenu")
    public String taskMenu(@RequestParam(value = "myTask", required = false) String myTask, Model model) {

        //ログインユーザーのemployeeCodeを取得する
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        //ログインユーザーが管理しているチームの有無を取得する
        boolean isManager;
        isManager = assignmentService.getCountIsManagerByEmployeeCode(employeeCode);

        //管理しているチームがある場合『/member/taskMenu』に遷移================
        if (isManager && myTask == null) {
            return "member/task-menu";
        }
        //===============================================================

        //管理しているチームが無い場合『/member/task-list』に遷移**********************
        //管理しているチームがある時「自分のタスク」を押下した時も実行される
        //自分のタスクを取得する
        List<Task> tasks = this.taskService.selectByEmployeeCode(employeeCode);
        model.addAttribute("taskList", tasks);
        //自分のフルネームを取得する
        User user = this.userService.selectUserById(employeeCode);
        String userName = user.getName();
        model.addAttribute("userName", userName);
        //検索条件格納用オブジェクト
        model.addAttribute("TaskSearchInput", new TaskSearchInput());
        return "member/task-myTaskList";
        //********************************************************************
    }

    @PostMapping("/task-search-list")
    public String searchTaskList(TaskSearchInput taskSearchInput, Model model) {
        //ログインユーザーのemployeeCodeを取得する
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());
        taskSearchInput.setEmployeeCode(employeeCode);

        //state(空欄、達成、未達成)で「達成」の時「progressRateAbove」を100にする
        if (taskSearchInput.getState().equals("達成")) {
            taskSearchInput.setProgressRateAbove(100);
        } else if (taskSearchInput.getState().equals("未達成")) {
            taskSearchInput.setProgressRateAbove(99);
        }

        //検索条件に基づいてレコードを取得する
        List<Task> tasks = taskService.selectSearch(taskSearchInput);
        model.addAttribute("taskList", tasks);

        //自分のフルネームを取得する
        User user = this.userService.selectUserById(employeeCode);
        String userName = user.getName();
        model.addAttribute("userName", userName);
        //検索条件格納用オブジェクト
        model.addAttribute("TaskSearchInput", new TaskSearchInput());

        return "member/task-myTaskList";
    }

    @PostMapping("/teamTask")
    public String teamTask(Model model,
                           @RequestParam(value = "teamTask", required = false) String teamTask) {

        //ログインユーザーのemployeeCodeを取得する
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        //管理しているチームIDを取得するselectTeamByEmployeeCode
        List<Team> teams = teamService.selectTeamByEmployeeCode(employeeCode);
        model.addAttribute("teams", teams);

        return "member/task-teamList";
    }

    @PostMapping("/taskTeamList")
    public String taskTeamList(@ModelAttribute Team teamList,
                               Model model) {
        //teamListのteamIdからチームメンバーを取得する
        List<Assignment> teamMembers = assignmentService.selectEmployeeCodeByTeamId(teamList.getTeamId());

        //取得したチームメンバーから各自が保有しているタスクを取得する
        List<Task> teamMemberTask = new ArrayList<>();
        for (Assignment teamMember : teamMembers) {
            List<Task> tasks = taskService.selectTaskAndUserNameByEmployeeCode(teamMember.getEmployeeCode(), teamList.getTeamId());
            teamMemberTask.addAll(tasks);
        }

        model.addAttribute("TaskSearchInput", new TaskSearchInput());
        model.addAttribute("taskList", teamMemberTask);
        model.addAttribute("teamId", teamList.getTeamId());

        return "member/task-teamTaskList";
    }

    @PostMapping("/task-search-taskTeamList")
    public String searchTaskTeamList(TaskSearchInput taskSearchInput,
                                     @ModelAttribute Team teamList,
                                     Model model) {
        //チームメンバーのemployeeCodeを取得する
        List<Assignment> teamMembers = assignmentService.selectEmployeeCodeByTeamId(taskSearchInput.getTeamId());

        //state(空欄、達成、未達成)で「達成」の時「progressRateAbove」を100にする
        if (taskSearchInput.getState().equals("達成")) {
            taskSearchInput.setProgressRateAbove(100);
        } else if (taskSearchInput.getState().equals("未達成")) {
            taskSearchInput.setProgressRateAbove(99);
        }

        //検索条件に基づいてレコードを取得する
        List<Task> teamMemberTask = new ArrayList<>();
        for (Assignment member : teamMembers) {
            taskSearchInput.setEmployeeCode(member.getEmployeeCode());
            List<Task> tasks = taskService.selectSearchTaskAndUserNameByEmployeeCode(taskSearchInput);
            teamMemberTask.addAll(tasks);
        }

        model.addAttribute("taskList", teamMemberTask);

        //検索条件格納用オブジェクト
        model.addAttribute("TaskSearchInput", new TaskSearchInput());

        model.addAttribute("teamId", teamList.getTeamId());

        return "member/task-teamTaskList";
    }

    @GetMapping("/taskDetail/{taskId}")
    public String taskDetail(@PathVariable int taskId, Model model) {

        //タスクIDをもとにタスクの詳細を取得する
        Task taskDetail = taskService.selectByTaskIdDetail(taskId);
        model.addAttribute("taskDetail", taskDetail);

        //タスクの履歴
        List<Task> taskHistory = taskService.selectByTaskIdHistory(taskId);
        model.addAttribute("taskHistory", taskHistory);

        //自分のタスクの時、編集・削除をしたいので、ボタンの表示非表示のためのフラグを作成
        //自分のemployeeCode
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int myEmployeeCode = Integer.parseInt(authentication.getName());
        //タスク詳細の所有者のemployeeCode
        int targetEmployeeCode = taskDetail.getEmployeeCode();
        //フラグ：自分のタスクの場合[true]として、viewで編集・削除ボタンを表示させる
        boolean isOwner = myEmployeeCode == targetEmployeeCode;
        model.addAttribute("isOwner", isOwner);

        model.addAttribute("taskId", taskDetail.getTaskId());

        return "member/task-Detail";
    }

    @GetMapping("/taskEdit/{taskId}")
    public String taskEdit(@PathVariable int taskId, Model model) {
        Task task = taskService.selectByTaskIdDetail(taskId);
        model.addAttribute("task", task);
        model.addAttribute("Task", new Task());
        return "member/task-edit";
    }

    @Transactional
    @PostMapping("/task-update")
    public String taskUpdate(Task task, Model model) {
        //user_task：update
        task.setUpdateAt(LocalDate.now());
        taskService.updateOneTask(task);
        //task_log：insert
        task.setCreateAt(LocalDate.now());
        taskService.saveOneTaskLog(task);

        //ログインユーザーのemployeeCodeを取得する
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());
        //自分のタスクを取得する
        List<Task> tasks = this.taskService.selectByEmployeeCode(employeeCode);
        model.addAttribute("taskList", tasks);
        //自分のフルネームを取得する
        User user = this.userService.selectUserById(employeeCode);
        String userName = user.getName();
        model.addAttribute("userName", userName);
        //検索条件格納用オブジェクト
        model.addAttribute("TaskSearchInput", new TaskSearchInput());
        return "member/task-myTaskList";
    }

    @GetMapping("/taskDelete/{taskId}")
    public String taskDelete(@PathVariable int taskId, Model model) {
        taskService.deleteTaskByTaskId(taskId);

        //ログインユーザーのemployeeCodeを取得する
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());
        //自分のタスクを取得する
        List<Task> tasks = this.taskService.selectByEmployeeCode(employeeCode);
        model.addAttribute("taskList", tasks);
        //自分のフルネームを取得する
        User user = this.userService.selectUserById(employeeCode);
        String userName = user.getName();
        model.addAttribute("userName", userName);
        //検索条件格納用オブジェクト
        model.addAttribute("TaskSearchInput", new TaskSearchInput());
        return "member/task-myTaskList";
    }

    @PostMapping("/handover_Team")
    public String handover_Team(Model model) {

        //ログインユーザーのemployeeCodeを取得する
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        //管理しているチームIDを取得するselectTeamByEmployeeCode
        List<Team> teams = teamService.selectTeamByEmployeeCode(employeeCode);
        model.addAttribute("teams", teams);

        return "member/task-handover-teamList";
    }

    @PostMapping("/handover")
    public String handover(@ModelAttribute Team teamId, Model model) {

        //チームメンバーの名前とemployeeCodeを取得する
        List<TaskHandoverInput> teamTasks = this.handoverService.selectByTeamId(teamId.getTeamId());

        //個人のタスクを取得する
        for (TaskHandoverInput task : teamTasks) {
            List<Task> taskList = this.handoverService.selectByEmployeeCode(task.getEmployeeCode());
            task.setTasks(taskList);
        }


        model.addAttribute("taskList", teamTasks);
        model.addAttribute("TaskHandoverSetInput", new TaskHandoverSetInput());

        return "member/task-handover-test";
    }

    @PostMapping("/handover-add")
    public String handoverAdd(TaskHandoverSetInput taskHandoverSetInput, Model model) {

        //user_taskの引き継いだtaskIdのemployeeCodeを変更する：update
        //　⇒　・employee_code
        //　　　 ・update_at
        taskHandoverSetInput.setUpdateAt(LocalDate.now());
        handoverService.updateUserTask(taskHandoverSetInput);
        //user_task_reportの引き継いだtaskIdのhandover_flagを「2」にする：update
        //　⇒　・handover_flag ベタ打ちで２に設定
        //　　　 ・update_at
        handoverService.updateOnFlag(taskHandoverSetInput);
        //task_logに変更記録を登録する：insert
        //　⇒　・task_log_id
        //　　　 ・task_id
        //　　　 ・report_id
        //　　　 ・process
        //　　　 ・employee_code
        //　　　 ・progress_rate
        //　　　 ・create_at
        taskHandoverSetInput.setReportId(handoverService.selectReportIdUsingTaskId(taskHandoverSetInput.getTaskId()));
        taskHandoverSetInput.setCreateAt(LocalDate.now());
        handoverService.saveHandoverLog(taskHandoverSetInput);
        model.addAttribute("taskHandoverSetInput", taskHandoverSetInput);
        return "member/task-handover-completed";
    }

    //[追加機能]タスクの追加画面遷移
    @GetMapping("/handover_TaskAdd")
    public String handover_TaskAdd(Model model) {
        TaskInputForm taskInputForm = new TaskInputForm();
        model.addAttribute("TaskInputForm",  taskInputForm);
        //model.addAttribute("employeeCode", userService.getAuthenticationEmployeeCode());
        return "member/handover_TaskAdd";
    }

    //追加処理後、メニュー画面へ遷移
    @PostMapping("/handover-TaskAdd-Complete")
    public String createTask(RedirectAttributes redirectAttributes, TaskInputForm taskInputForm) {
        //employeeCodeを取得
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        //(多分)TaskIDを取得←AutoINCRMENTなので指定不要?

        //上記二つをもとに新しいタスクを紐付け

        return "redirect:/manager/taskMenu";
    }

    @GetMapping("/taskBulkDeletion")
    public String taskBulkDeletion(Model model) {
        //ログインユーザーのemployeeCodeを取得する
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        //EmployeeCode1から全てのタスクを取得する
        List<Task> taskList = taskService.selectByEmployeeCode(employeeCode);
        model.addAttribute("taskList", taskList);
        model.addAttribute("TaskBulkDeletionInput", new TaskBulkDeletionInput());
        return "member/task-bulkDeletion";
    }

    @Transactional
    @PostMapping("/taskBulkDeletionExecution")
    public String taskBulkDeletionExecution(TaskBulkDeletionInput taskBulkDeletionInput, Model model) {
        //「,」で区切って配列に変換
        String[] taskIdArray = taskBulkDeletionInput.getTaskList().split(",");

        //文字列型なので、数値型に変換
        List<Integer> taskIdList = new ArrayList<>();
        for (String taskId : taskIdArray) {
            taskIdList.add(Integer.parseInt(taskId));
        }

        //タスクIDを基に削除
        for (int i = 0; i < taskIdList.size(); i++) {
            taskService.deleteTaskByTaskId(taskIdList.get(i));
        }

        //自分のタスク一覧に戻る
        //ログインユーザーのemployeeCodeを取得する
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());
        //自分のタスクを取得する
        List<Task> tasks = this.taskService.selectByEmployeeCode(employeeCode);
        model.addAttribute("taskList", tasks);
        //自分のフルネームを取得する
        User user = this.userService.selectUserById(employeeCode);
        String userName = user.getName();
        model.addAttribute("userName", userName);
        //検索条件格納用オブジェクト
        model.addAttribute("TaskSearchInput", new TaskSearchInput());
        return "member/task-myTaskList";
    }


    @GetMapping("/user-main")
    public ModelAndView userMain(ModelAndView mav) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        List<Assignment> myast = assignmentService.getAssignmentByEmployeeCode(employeeCode);
        if (myast == null) {
            myast = new ArrayList<>();
        }

        LocalDate targetDate;
        LocalDate firstDayOfLastWeek = LocalDate.now().minusWeeks(1).with(DayOfWeek.MONDAY);
        DayOfWeek currentDayOfWeek = LocalDate.now().getDayOfWeek();

        if (currentDayOfWeek.equals(DayOfWeek.MONDAY) ||
                currentDayOfWeek.equals(DayOfWeek.SATURDAY) ||
                currentDayOfWeek.equals(DayOfWeek.SUNDAY)) {
            targetDate = firstDayOfLastWeek.plusDays(4);
        } else {
            targetDate = LocalDate.now().minusDays(1);
        }
        boolean hasSentReport = reportService.existsReport(employeeCode, targetDate);

        List<Assignment> allast = assignmentService.getAllAssignment();

        List<Team> allteam = teamService.getAllTeam();
        List<Team> myteammem = new ArrayList<>();
        List<Team> myteammgr = new ArrayList<>();

        List<User> allusers = userService.getAllEmployeeInfo();
        List<User> managers = new ArrayList<>();

        String title = "メイン";
        mav.addObject("title", title);

//        自分がメンバーとして所属しているチーム情報を自分のassignment情報から割り出す
        if (!myast.isEmpty()) {
            for (Team team : allteam) {
                for (Assignment ast : myast) {
                    if (team.getTeamId() == ast.getTeamId() && !ast.getIsManager()) {
                        myteammem.add(team);
                    } else if (team.getTeamId() == ast.getTeamId() && ast.getIsManager()) {
                        myteammgr.add(team);
                    }
                }
            }
        }

//        自分が所属しているチームのマネージャー情報を割り出す、自分がマネージャーだったらリストには追加しない
        for (Assignment ast : allast) {
            for (Team team : myteammem) {
                if (ast.getTeamId() == team.getTeamId()) {
                    for (User user : allusers) {
                        if (ast.getEmployeeCode() == user.getEmployeeCode() && user.getEmployeeCode() != employeeCode) {
                            if (ast.getIsManager()) {
                                managers.add(user);
                            }
                        }
                    }
                }
            }
        }

//        直近のレポート特定と未達成タスクリストの取得
        List<Report> two = reportService.getLastTwoByUser(employeeCode);
        Report lastReport = new Report();
        LocalDate todaysDate = LocalDate.now();

        if (two != null) {
            for (Report rp : two) {
                if (rp.getDate().isBefore(todaysDate)) {
                    lastReport = rp;
                    break;
                }
            }
        }

        List<Task> taskLogs;
        if (two != null) {
            taskLogs = taskService.selectByEmployeeCodeNotAchieved(employeeCode);
        } else {
            taskLogs = new ArrayList<>();
        }
        mav.addObject("taskList", taskLogs);

        mav.addObject("lastReport", lastReport);
        mav.addObject("managerList", managers);
        mav.addObject("assignmentList", myast);
        mav.addObject("memteamList", myteammem);
        mav.addObject("mgrteamList", myteammgr);

//        自分がマネージャーとして所属しているチームのメンバー抽出
        List<Assignment> asMgr = assignmentService.getAsManager(employeeCode);
        List<User> members = new ArrayList<>();
        if (!asMgr.isEmpty()) {
            for (Team tm : myteammgr) {
                for (Assignment as : allast) {
                    for (User us : allusers) {
                        if (tm.getTeamId() == as.getTeamId() && !as.getIsManager() && as.getEmployeeCode() == us.getEmployeeCode()) {
                            members.add(us);
                        }
                    }
                }
            }
        }

//        昨日の曜日を定義。昨日が日曜日か土曜日の場合は金曜日の日付を設定
        LocalDate yesterdayDate = todaysDate.minusDays(1);
        DayOfWeek dw = yesterdayDate.getDayOfWeek();
        if (dw.getValue() == 7) {
            yesterdayDate.minusDays(3);
        } else if (dw.getValue() == 6) {
            yesterdayDate.minusDays(2);
        }

//        今日報告提出したメンバー抽出
        List<User> todaymem = new ArrayList<>();

        if (!members.isEmpty()) {
            for (User user : members) {
                Report report = reportService.getOneByUserByDate(user.getEmployeeCode(), todaysDate);
                if (report != null) {
                    todaymem.add(user);
                }
            }
        }

//        前営業日に未提出のメンバー抽出
        List<User> notsubmem = new ArrayList<>();
        if (!members.isEmpty()) {
            for (User user : members) {
                Report report = reportService.getOneByUserByDate(user.getEmployeeCode(), yesterdayDate);
                if (report == null) {
                    notsubmem.add(user);

                }
            }
        }

        mav.addObject("todaymembers", todaymem);
        mav.addObject("notsubmit", notsubmem);
        mav.addObject("targetDate", targetDate);
        mav.addObject("hasSentReport", hasSentReport);

        mav.setViewName("member/user-main");

        return mav;
    }

    //ユーザー情報変更一覧
    @GetMapping("/userDetailsList")
    public String displayUserInfoList() {
        return "member/userDetailsList";
    }

    //ユーザー情報変更画面(名前、パスワード、アイコン)
    @GetMapping("/userDetailsList/userEdit")
    public String userEdit(Model model) {
        model.addAttribute("userEditInput", new UserEditInput());
        return "member/userDetailsList-userEdit";
    }

    //ユーザ情報編集情報処理
    @PostMapping("/userDetailsList/complete")
    public String editComplete(@ModelAttribute("userEditInput") UserEditInput userEditInput,
                               RedirectAttributes redirectAttributes) {
        //↓ログイン中のemployeeCodeをAuthentication(認証情報)から取得
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        userEditInput.setEmployeeCode(employeeCode);
        userEditInput.setRole("USER");

        //↓userSeriviceでの処理した値が正しくDBに"入ったら"ErrorMSGがnullになる
        Exception Error = userService.checkTest(userEditInput, employeeCode);

        if (Error != null) {
            redirectAttributes.addFlashAttribute("ErrorMSG", "更新失敗,再度お試しください");
            return "redirect:/member/userDetailsList-userEdit";
        }
        redirectAttributes.addFlashAttribute("editCompleteMSG", "情報を更新しました");
        return "redirect:/member/userDetailsList";
    }

    //報告未提出メンバーへ通知メールを送信する
    @GetMapping("/sendReportReminder")
    public ResponseEntity<String> sendReportReminder(@RequestParam("employeeCode") int employeeCode,
                                                     Model model) {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");

        User member = userService.getUserByEmployeeCode(employeeCode);
        String memberEmail = member.getEmail();
        String memberName = member.getName();

        String subject = "【DHITシステム】報告提出の通知";
        String body = "昨日、報告未提出があります。\n" +
                "\n" +
                "下記より報告を行ってください。\n" +
                baseUrl + "/login\n" +
                "※当メールは送信専用となっております。";

        try {
            if (!memberEmail.isEmpty()) {
                mailService.sendMail(memberEmail, subject, body);
                return ResponseEntity.ok("{\"message\":\"" + memberName + "\"}");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Member email is empty");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(memberName + "のメール送信に失敗しました" + ". Error: " + e.getMessage());
        }
    }


    /////////////////////////////////////////////////////////////////////////
    @GetMapping("/apply/create")
    public String displayApplyCreate(
            Model model
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //バリデーションInteger
        int employeeCode = Integer.parseInt(authentication.getName());

        ApplyCreateInput applyCreateInput = new ApplyCreateInput();
        SettingInput settingInput = new SettingInput();
        //java.timeパッケージから現在の時刻を取得
        applyCreateInput.setCreatedDate(LocalDateTime.now());

        String title = "申請作成";
        model.addAttribute("title", title);
        //規定の終業時間を取得し、セット
        Setting setting = settingService.getSettingTime(employeeCode);
        settingInput.setStartTime(setting.getStartTime());
        settingInput.setEndTime(setting.getEndTime());
//        settingInput.setEmployment(false);
        model.addAttribute("settingInput", settingInput);

        // 提出ボタンを押した瞬間の時刻を取得し、createdDateにセット
        applyCreateInput.setCreatedDate(LocalDateTime.now());

        model.addAttribute("applyCreateInput", applyCreateInput);
        return "member/apply-create";

    }

    //↓Transactionalはトランザクション処理で一連の流れが失敗した場合ロールバックする
    @Transactional
    @PostMapping("/apply/create")
    public String createApply(ApplyCreateInput applyCreateInput, RedirectAttributes redirectAttributes, SettingInput settingInput, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        Setting setting = settingService.getSettingTime(employeeCode);

        // 提出ボタンを押した瞬間の時刻を取得し、createdDateにセット
        applyCreateInput.setCreatedDate(LocalDateTime.now());

        //newApplyIdには新たにInsertされたreportのIDが入る
        int newApplyId = applyService.create(
                employeeCode,
                applyCreateInput.getApplicationType(),
                applyCreateInput.getAttendanceType(),
                applyCreateInput.getStartDate(),
                applyCreateInput.getEndDate(),
                applyCreateInput.getStartTime(),
                applyCreateInput.getEndTime(),
                applyCreateInput.getReason(),
                applyCreateInput.getApproval(),
                applyCreateInput.getCreatedDate()
        );

        return "redirect:/member/apply/create-completed";
    }

    // 申請提出完了画面
    @GetMapping("/apply/create-completed")
    public String displayApplyCreateCompleted(
    ) {
        return "member/apply-create-completed";
    }

    // 申請一覧
    @GetMapping("/apply-search")
    public String displayApplySearch(
            Model model
    ) {
        String title = "申請一覧";
        model.addAttribute("title", title);

        model.addAttribute("applySearchInput", new ApplySearchInput());
        model.addAttribute("error", model.getAttribute("error"));

        //ログイン中のユーザーのemployeeCodeを取得する
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());
        User member = userService.getUserByEmployeeCode(employeeCode);
        model.addAttribute("member", member);
        //報告一覧表示---------------------------------
        List<Apply> applys = applyService.getfindAll(employeeCode);

        //検索機能---------------------------------------

        //既読or未読
//        for (Apply apply : applys) {
//            boolean isApprovalGiven = applyService.count(apply.getId());
//            apply.setStatus(isApprovalGiven ? "既読" : "未読");
//        }
        model.addAttribute("applys", applys);
//        //年月で重複しないList作成
//        List<LocalDate> dateList = applys.stream()
//                .map(Apply::getDate)
//                .map(date -> date.withDayOfMonth(1))
//                .distinct()
//                .toList();
//        model.addAttribute("dateList", dateList);

//        //データ格納用
        model.addAttribute("applySortInput", new ApplySortInput());

        return "member/apply-search";
    }

    @PostMapping("/search-apply")
    public String searchApply(
            ApplySearchInput applySearchInput,
            RedirectAttributes redirectAttributes,
            ApplySortInput applySortInput,
            Model model
    ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        String applyId = applyService.searchId(
                employeeCode,
                applySearchInput.getCreatedDate()
        );

        //日付、、
        if (applySortInput.getSort()) {
            applySortInput.setEmployeeCode(employeeCode);

            //ソート用
            List<Apply> applys = applyService.getSortApply(applySortInput);
            User member = userService.getUserByEmployeeCode(employeeCode);

            model.addAttribute("member", member);
            model.addAttribute("applySearchInput", new ApplySearchInput());
            model.addAttribute("error", model.getAttribute("error"));
            model.addAttribute("applys", applys);


//            年月で重複しないList作成
//            List<LocalDateTime> dateList = applys.stream()
//                    .map(Apply::getCreatedDate)
//                    .map(date -> date.withDayOfMonth(1))
//                    .distinct()
//                    .toList();
//            model.addAttribute("dateList", dateList);

            //データ格納用
            model.addAttribute("applySortInput", new ApplySortInput());
            return "member/apply-search";
        }

        redirectAttributes.addAttribute("applyId", applyId);
        return "redirect:/member/applys/{applyId}";
    }

    @GetMapping("/apply/{applyId}")
    public String applyDetail(
            @PathVariable("applyId") int applyId,
            Model model
    ) {
        Apply apply = applyService.findById(applyId);
        apply.setUser(userService.getUserByEmployeeCode(apply.getEmployeeCode()));
        model.addAttribute("apply", apply);
        return "/member/apply-detail";
    }

    @GetMapping("/apply/{applyId}/delete")
    @Transactional
    public String deleteApply(
            @PathVariable("applyId") int applyId,
            Model model
    ) {
        Apply apply = applyService.findById(applyId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int employeeCode = Integer.parseInt(authentication.getName());

        if (apply.getEmployeeCode() != employeeCode) {
            return "redirect:/member/apply-detail";
        }

        this.applyService.deleteById(applyId);

        User member = userService.getUserByEmployeeCode(employeeCode);
        model.addAttribute("member", member);
        List<Apply> applys = applyService.getfindAll(employeeCode);
        model.addAttribute("applys", applys);
        model.addAttribute("applySearchInput", new ApplySearchInput());
        model.addAttribute("error", model.getAttribute("error"));
        model.addAttribute("applySortInput", new ApplySortInput());
        String title = "申請一覧";
        model.addAttribute("title", title);

        return "member/apply-search";
    }
}


