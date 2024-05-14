//これやんないとうごかない
window.onload = () => {

};

function removeTask(button) {
        button.closest('.task-row').remove();
    }

//これやんないとうごかない
document.addEventListener('DOMContentLoaded', function () {

    //追加ボタン押下によるタスクの入力フィールドの追加
    document.getElementById('add-todo').addEventListener('click', function() {
        // Clone the template node
        const template = document.querySelector('.task-row');
        console.log(template);
        if(template) {
            console.log('clone')
            const clone = template.cloneNode(true);
            // Clear values from cloned inputs
            const inputs = clone.querySelectorAll('input[type="text"], input[type="number"]');
            inputs.forEach(input => input.value = '');
            // Append cloned node to container
            document.getElementById('taskListContainer').appendChild(clone);
        }else{
 const clone = document.createElement('div');
    clone.classList.add('task-row');

    // 各入力フィールドを作成
    clone.innerHTML = `
        <div class="flex-row d-flex gap-3 col-md-8">
            <label>タスク名</label>
            <input type="text" class="form-control" placeholder="タスク名（最大100文字）" th:value="" maxlength="100" required>
            <label>進捗率</label>
            <input type="number" class="form-control" placeholder="進捗率" th:value="" min="0" max="100" required>
            <label>コメント</label>
            <input type="text" class="form-control" placeholder="コメント（最大300文字）" th:value="" maxlength="300" required>
            <button type="button" class="btn btn-danger" onclick="removeTask(this)">削除</button>
        </div>
    `;

    // 入力フィールドをクリア
    const inputs = clone.querySelectorAll('input[type="text"], input[type="number"]');
    inputs.forEach(input => input.value = '');

    // クローンを追加
    document.getElementById('taskListContainer').appendChild(clone);
        }

    });

    //体調コメント文字数制限：50文字
    const textCondition = document.getElementById('text_condition');
    const charCount = document.getElementById('charCount');
    textCondition.addEventListener('input', function () {
        const remainingChars = 50 - this.value.length;
        charCount.textContent = '残り文字数: ' + remainingChars;
        // 文字数が50を超える場合、入力を制限
        if (remainingChars < 0) {
            this.value = this.value.slice(0, 50);
            charCount.textContent = '残り文字数: 0';
        }
    });

    //報告内容文字数制限：300文字
    const textImpressions = document.getElementById('text_impressions');
    const charCountIm = document.getElementById('charCount-im');
    textImpressions.addEventListener('input', function () {
        const remainingCharsIm = 300 - this.value.length;
        charCountIm.textContent = '残り文字数: ' + remainingCharsIm;
        // 文字数が300を超える場合、入力を制限
        if (remainingCharsIm < 0) {
            this.value = this.value.slice(0, 300);
            charCountIm.textContent = '残り文字数: 0';
        }
    });

    //明日の予定文字数制限：200文字
    const textTomorrow = document.getElementById('text_tomorrowSchedule');
    const charCountTom = document.getElementById('charCount-tom');
    textTomorrow.addEventListener('input', function () {
        const remainingCharsTom = 200 - this.value.length;
        charCountTom.textContent = '残り文字数: ' + remainingCharsTom;
        // 文字数が200を超える場合、入力を制限
        if (remainingCharsTom < 0) {
            this.value = this.value.slice(0, 200);
            charCountTom.textContent = '残り文字数: 0';
        }
    });

    //遅刻理由文字数制限100文字
    const textLate = document.getElementById('text_latenessReason');
    const charCountLat = document.getElementById('charCountLt');
    if (textLate) {
        textLate.addEventListener('input', function () {
            const remainingCharsLat = 100 - this.value.length;
            charCountLat.textContent = '残り文字数: ' + remainingCharsLat;
            // 文字数が100を超える場合、入力を制限
            if (remainingCharsLat < 0) {
                this.value = this.value.slice(0, 100);
                charCountLat.textContent = '残り文字数: 0';
            }
        });
    }


});