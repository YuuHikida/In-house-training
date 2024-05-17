function setListValues()
{
   //inputタグ、Listタグの取得
   var managerInput = document.getElementById("managerInput");
   var memberInput  = document.getElementById("memberInput");

   var managerList = document.getElementById("managerList");
   var memberList = document.getElementById("memberList");

    for (var i = 0; i < managerList.options.length; i++)
    {
        managerList.options[i].selected = true;
    }

    for (var i = 0; i < memberList.options.length; i++)
    {
        memberList.options[i].selected = true;
    }


/*
   //Inputタグに入れる配列を作成
   const managerArray = [];
   const memberArray = [];
   //リストの値を全て読み込む
   //managerList
   for(let i = 0; i <managerList.options.length;i++)
   {
        var element =managerList.options[i];
        managerArray.push(element.value);
        //managerInput[i].value = element.value;
        //console.log("managerArray[i]:"+ managerArray[i] );
   }
   //managerList
     for(let i = 0; i <memberList.options.length;i++)
     {
          var element =memberList.options[i];
          memberArray.push(element.value);
          //managerInput[i].value = element.value;
          //console.log("memberArray[i]:"+ memberArray[i] );
     }
     // inputタグに配列を代入（JSON文字列に変換して代入）
     managerInput.value = JSON.stringify(managerArray);
     memberInput.value = JSON.stringify(memberArray);

            // 確認（配列の中身を表示）
     managerArray.forEach(element => console.log("managerArray : " + element));
     memberArray.forEach(element => console.log("memberArray : " + element));

   //inputタグに配列を代入
   managerList.value = managerArray;
   memberList.value  = memberArray;
    //確認
    managerList.forEach( element =>console.log("managerList : " + element.value));
    memberList.forEach( element =>console.log("memberList : " + element.value));
    *////////////////////////
   console.log();
   setTimeout(function() {
     console.log("これは3秒後に表示されます");
   }, 10000);

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