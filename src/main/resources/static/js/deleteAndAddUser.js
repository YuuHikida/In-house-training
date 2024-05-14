 function setListValues() {
        // managerの要素を取得
        var managerListElement = document.getElementById("managerList");
        var managerInputElement = document.getElementById("managerInput");

        // memberの要素を取得
        var memberListElement = document.getElementById("memberList");
        var memberInputElement = document.getElementById("memberInput");

        // 全てのオプションの値をmanagerInputElementのvalueに設定
        var selectedValuesMgr = Array.from(managerListElement.options)
                            .map(option => parseInt(option.value));
        // 全てのオプションの値をmemberInputElementのvalueに設定
        var selectedValuesMem = Array.from(memberListElement.options)
                            .map(option => parseInt(option.value));

        //上記のint配列をinputタグに値を埋め込む
        var managerInput = document.getElementById(managerInput);
        var memberInput = document.getElementById(memberInput);

       managerInput.value = selectedValuesMgr;
       memberInput.value  = selectedValuesMem;
        //console.log( "selectedValuesMgr:" + selectedValuesMgr );

}


    function addManager() {
        var userSelect = document.getElementById("userList");
        var managerList = document.getElementById("managerList");
        var memberList = document.getElementById("memberList");

        // userListの選択状態の数を取得
        for (var i = 0; i < userSelect.selectedOptions.length; i++) {
            var userOption = userSelect.selectedOptions[i];
            //console.log("userOption:"+userOption.value);　//employeeCode

            // 追加時Manager,Memberに重複がないか調べる
            if (!isOptionInManagerList(userOption, managerList) && !isOptionInList(userOption, memberList)) {
                // Create a new option element for the managerList
                var managerAdd = document.createElement("option");
                managerAdd.text = userOption.text;
                managerAdd.value = userOption.value;

                // Add the new option to the managerList
                managerList.add(managerAdd);
            }
        }

    }

    // Helper function to check if an option is already in the managerList
    function isOptionInManagerList(option, managerList) {
        for (var i = 0; i < managerList.options.length; i++) {
            if (managerList.options[i].value === option.value) {
                return true;
            }
        }
        return false;
    }

    function addMember() {
        var userSelect = document.getElementById("usersList");
        var memberList = document.getElementById("memberList");
        var managerList = document.getElementById("managerList"); // Add this line

        // Loop through all selected options
        for (var i = 0; i < userSelect.selectedOptions.length; i++) {
            var userOption = userSelect.selectedOptions[i];

            // Check if the option is not already in memberList and not in managerList
            if (!isOptionInList(userOption, memberList) && !isOptionInManagerList(userOption, managerList)) {
                // Create a new option element for the memberList
                var memberAdd = document.createElement("option");
                memberAdd.text = userOption.text;
                memberAdd.value = userOption.value;

                // Add the new option to the memberList
                memberList.add(memberAdd);
            }
        }
    }

    // Helper function to check if an option is already in the list
    function isOptionInList(option, list) {
        for (var i = 0; i < list.options.length; i++) {
            if (list.options[i].value === option.value) {
                return true;
            }
        }
        return false;
    }

    function removeMember() {
        var memberList = document.getElementById("memberList");
        var selectedIndexes = [];
        // Get the selected indexes in reverse order
        for (var i = memberList.options.length - 1; i >= 0; i--) {
            if (memberList.options[i].selected) {
                selectedIndexes.push(i);
            }
        }
        // Remove the selected options
        selectedIndexes.forEach(function (index) {
            memberList.remove(index);
        });
    }

    function removeManager() {
        var managerList = document.getElementById("managerList");
        var selectedIndexes = [];
        // Get the selected indexes in reverse order
        for (var i = managerList.options.length - 1; i >= 0; i--) {
            if (managerList.options[i].selected) {
                selectedIndexes.push(i);
            }
        }
        // Remove the selected options
        selectedIndexes.forEach(function (index) {
            managerList.remove(index);
        });
    }