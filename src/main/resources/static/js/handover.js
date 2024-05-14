//これやんないとうごかない
window.onload = () => {

    //defaultで選択されている引継ぎ元メンバーを除いた引継ぎ先メンバーを取得
    const beforeMember = document.getElementById('beforeMember').value;
    let afterMember = document.getElementById('afterMember');
    afterMember.innerHTML = "";
    for(let i = 0; i < taskList.length; i++){
        if(taskList[i].employeeCode != beforeMember){
            let option = document.createElement('option');
            option.value = taskList[i].employeeCode;
            option.text = taskList[i].name;
            afterMember.add(option);
        }
    }

};

//これやんないとうごかない
document.addEventListener('DOMContentLoaded', function () {

    //選択した引継ぎ元メンバーに対応したタスクを取得
    //引継ぎ先と同名になる場合があるので、そうならないように引継ぎ先メンバーを再設定
    document.getElementById('beforeMember').addEventListener('change', () => {
        const beforeMember = document.getElementById('beforeMember').value;
        let tasks = document.getElementById('memberTasks');
        tasks.innerHTML = "";
        for(let i = 0; i < taskList.length; i++){
            if(taskList[i].employeeCode == beforeMember){
                for(let j = 0; j < taskList[i].tasks.length; j++){
                    let option = document.createElement('option');
                    option.value = taskList[i].tasks[j].taskId;
                    option.text = taskList[i].tasks[j].taskName;
                    tasks.add(option);
                }
            }
        }
        let afterMember = document.getElementById('afterMember');
        afterMember.innerHTML = "";
        for(let i = 0; i < taskList.length; i++){
            if(taskList[i].employeeCode != beforeMember){
                let option = document.createElement('option');
                option.value = taskList[i].employeeCode;
                option.text = taskList[i].name;
                afterMember.add(option);
            }
        }
    });

    //タスクを選択すると引継ぎ先メンバーを更新する
    document.getElementById('memberTasks').addEventListener('change', () => {
        const beforeMember = document.getElementById('beforeMember').value;
        let afterMember = document.getElementById('afterMember');
        afterMember.innerHTML = "";
        for(let i = 0; i < taskList.length; i++){
            if(taskList[i].employeeCode != beforeMember){
                let option = document.createElement('option');
                option.value = taskList[i].employeeCode;
                option.text = taskList[i].name
                afterMember.add(option);
            }
        }
    });

    //確認ボタンを押下するとタスクの移動が表形式で分かるようにテーブルを作成する
    document.getElementById('handoverCheck').addEventListener('click', () => {
        const beforeMember = document.getElementById('beforeMember').value;
        const afterMember = document.getElementById('afterMember').value;
        const task = document.getElementById('memberTasks').value;
        let beforeName = '';
        let afterName = '';

        //引継ぎ元のタスクを取得
        let beforeTasks = [];
        for(let i = 0; i < taskList.length; i++){
            if (taskList[i].employeeCode == beforeMember) {
                for(let j = 0; j < taskList[i].tasks.length; j++){
                    if(taskList[i].tasks[j].taskId != task) {
                        beforeTasks.push(taskList[i].tasks[j]);
                        beforeName = taskList[i].name;
                    }
                }
            }
        }

        //引継ぎ先のタスクを取得
        let afterTasks = [];
        for(let i = 0; i < taskList.length; i++){
            if (taskList[i].employeeCode == afterMember) {
                for(let j = 0; j < taskList[i].tasks.length; j++){
                    afterTasks.push(taskList[i].tasks[j]);
                    afterName = taskList[i].name;
                }
            }
            //引き継ぐタスクを取得
            if(taskList[i].employeeCode == beforeMember){
                for(let j = 0; j < taskList[i].tasks.length; j++){
                    if(taskList[i].tasks[j].taskId == task) {
                        afterTasks.push(taskList[i].tasks[j]);
                        console.log(taskList[i].tasks[j]);
                        document.getElementById('handoverInput').elements['taskName'].value = taskList[i].tasks[j].taskName;
                        document.getElementById('handoverInput').elements['comment'].value = taskList[i].tasks[j].comment;
                        document.getElementById('handoverInput').elements['progressRate'].value = taskList[i].tasks[j].progressRate;
                    }
                }
            }
        }

        //HTML要素を取得する
        let beforeTableContainer = document.getElementById('tableContainerBefore');
        let afterTableContainer = document.getElementById('tableContainerAfter');

        //HTML要素の初期化
        beforeTableContainer.innerHTML ='';
        afterTableContainer.innerHTML ='';

        //テーブル要素を作成
        let beforeTable = document.createElement('table');
        let afterTable = document.createElement('table');

        //テーブルのクラスを追加
        beforeTable.classList.add('StripeTable', 'mb-4');
        afterTable.classList.add('StripeTable', 'mb-4');

        //テーブルのヘッダーを作成
        //before
        let beforeHeaderRow = beforeTable.insertRow();
        let beforeHeaderCell = document.createElement('th');
        beforeHeaderCell.textContent = beforeName + 'さんのタスク';
        beforeHeaderRow.appendChild(beforeHeaderCell);
        //after
        let afterHeaderRow = afterTable.insertRow();
        let afterHeaderCell = document.createElement('th');
        afterHeaderCell.textContent = afterName + 'さんのタスク';
        afterHeaderRow.appendChild(afterHeaderCell);

        //テーブルにデータを追加
        //before
        beforeTasks.forEach(function (task) {
            let row = beforeTable.insertRow();
            let cell = row.insertCell();
            cell.textContent = task.taskName;
        });

        //after
        afterTasks.forEach(function (task) {
            //afterMember
            if(task.employeeCode == beforeMember) {
                let row = afterTable.insertRow();
                let cell = row.insertCell();
                cell.innerHTML = task.taskName + '<sup style="vertical-align: top; font-size:xx-small; line-height: 2; color: red;">NEW</sup>';
            }
            if(task.employeeCode == afterMember) {
                let row = afterTable.insertRow();
                let cell = row.insertCell();
                cell.textContent = task.taskName;
            }

        });

        //HTMLに追加
        beforeTableContainer.appendChild(beforeTable);
        afterTableContainer.appendChild(afterTable);

        //変更をTaskHandoverSetInput記録用inputタグにセット
        document.getElementById('handoverInput').elements['employeeCodeBefore'].value = beforeMember;
        document.getElementById('handoverInput').elements['employeeCodeAfter'].value = afterMember;
        document.getElementById('handoverInput').elements['taskId'].value = task;

        //completed用inputタグにセット
        let nameBefore = document.getElementById('beforeMember');
        let nameAfter = document.getElementById('afterMember');
        nameBefore = nameBefore.options[nameBefore.selectedIndex].text;
        nameAfter = nameAfter.options[nameAfter.selectedIndex].text;
        document.getElementById('handoverInput').elements['nameBefore'].value = nameBefore;
        document.getElementById('handoverInput').elements['nameAfter'].value = nameAfter;


        //登録用のボタンを表示する
        let submitButton = document.getElementById('submitButton');
        submitButton.style.display = 'inline-block';


    });



});