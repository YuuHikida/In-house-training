function setListValues()
{
   //inputタグ、Listタグの取得
   var managerList = document.getElementById("managerList");
   var memberList = document.getElementById("memberList");
   //manaYerListの選択肢を全て選択状態にする
    for (var i = 0; i < managerList.options.length; i++)
    {
        managerList.options[i].selected = true;
    }
    //memberListの選択肢を全て選択状態にする
    for (var i = 0; i < memberList.options.length; i++)
    {
        memberList.options[i].selected = true;
    }
}

    function addManager() {
        var userSelect = document.getElementById("userList");
        var managerList = document.getElementById("managerList");
        var memberList = document.getElementById("memberList");

        // Loop through all selected options
        for (var i = 0; i < userSelect.selectedOptions.length; i++) {
            var userOption = userSelect.selectedOptions[i];
            console.log("userOption:"+userOption.value);

            // Check if the option is not already in managerList and not in memberList
            if (isOptionInManagerList(userOption, managerList) && isOptionInList(userOption, memberList)) {
                // Create a new option element for the managerList
                var managerAdd = document.createElement("option");
                managerAdd.text = userOption.text;
                managerAdd.value = userOption.value;

                // Add the new option to the managerList
                managerList.add(managerAdd);
            }
        }
    }

    function addMember() {
        var userSelect = document.getElementById("usersList");
        var memberList = document.getElementById("memberList");
        var managerList = document.getElementById("managerList"); // Add this line
console.log("managerList.length:"+managerList.options.length);
        // Loop through all selected options
        for (var i = 0; i < userSelect.selectedOptions.length; i++) {
            var userOption = userSelect.selectedOptions[i];

            // Check if the option is not already in memberList and not in managerList
            if (isOptionInList(userOption, memberList) && isOptionInManagerList(userOption, managerList)) {
                // Create a new option element for the memberList
                var memberAdd = document.createElement("option");
                memberAdd.text = userOption.text;
                memberAdd.value = userOption.value;

                // Add the new option to the memberList
                memberList.add(memberAdd);
            }
        }
    }

    function isOptionInManagerList(option, managerList) {
        //console.log("managerList.options.length":managerList.options.length);
        for (var i = 0; i < managerList.options.length; i++) {
            if (managerList.options[i].value === option.value) {
                return false;
            }
        }
        return true;
    }

    function isOptionInList(option, list) {
    //console.log("memberlist.options.length":list.options.length);
        for (var i = 0; i < list.options.length; i++) {
            if (list.options[i].value === option.value) {
                return false;
            }
        }
        return true;
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