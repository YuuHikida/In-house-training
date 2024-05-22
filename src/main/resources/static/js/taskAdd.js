        //配列の添字に使用
        let arrayCount = 0 ;
        let todoField = null;
       //let taskIndex = /*[[${reportCreateInput.taskLogs.size()}]]*/;

        //読み込み時にタスクのname,comment,progressに配列の添字を添える
        document.addEventListener("DOMContentLoaded",(event)=>{
            todoField = document.getElementById("todo-field");

            const name = document.getElementById("inputName");
            const progress = document.getElementById("inputProgress");
            const comment = document.getElementById("inputComment");

            name.name = `taskInputList[${arrayCount}].task_name`;
            progress.name = `taskInputList[${arrayCount}].progressRate`;
            comment.name = `taskInputList[${arrayCount}].comment`;


        })

         function addTask() {
            window.console.log("IM HERE!!!!!!!!!!!!");
            arrayCount += 1 ;
            const node = document.createElement("div");
            node.setAttribute("class", "row");
            node.innerHTML += `<div class="flex-row gap-3 col-md-8">
                                    <label>タスク名</label>
                                    <input type="text" class="form-control" placeholder="タスク名（最大100文字）" aria-label=""
                                           name="taskInputList[${arrayCount}].task_name" maxlength="100" required>
                                   <label>進捗率</label>
                                    <input type="number" class="form-control" placeholder="進捗率" aria-label=""
                                           name="taskInputList[${arrayCount}].progressRate" min="0" max="100" required>
                                   <label>コメント</label>
                                   <input type="text" class="form-control" placeholder="コメント（最大300文字）" aria-label=""
                                           name="taskInputList[${arrayCount}].comment" maxlength="300" required>
                                    <button type="button" class="btn btn-danger" onclick="removeTask(this)">削除</button>
                                </div>`;

            todoField.appendChild(node);

        };


        function removeTask(button) {
            button.parentNode.parentNode.remove();
        }


