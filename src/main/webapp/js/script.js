function addEntry() {
    var request = {'url': $(".url-input").val()};
    $.post("/domain-ranker/api/add?url=" + $(".url-input").val(), request, function (data) {
        //console.log(data);
        if (data.state === "success") {
            $(".url-input").val("");
        }
    });
}

function getEntries() {
    var n = $(".count-input").val();
    $.get("/domain-ranker/api/get/" + n, function (data) {
        if (data.state === "success") {
            $(".count-input").val("");
            var result = data.result;
            //console.log(result);
            $("#main-table > tbody").html("");
            for (var i = 0; i < result.length; i++) {
                addNewRow(i + 1, result[i]);
            }
        }
    });
}

function addNewRow(id, data) {
    var newEntryHTML = "<tr id=\"entry-" + id + "\">";
    newEntryHTML += "<td>" + id + "</td><td>" +
        data.domain +
        "</td><td>" +
        data.count +
        "</td></tr>";
    $("#main-table > tbody").append(newEntryHTML);
}
