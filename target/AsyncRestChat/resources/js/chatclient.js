var path = "http://localhost:8080/AsyncRESTChat/chat";
var userName;
var nameSubDone = false;

function requestForMessages(uri) {
   // alert(uri);
    var chatElems;
    if (uri !== undefined) {
        var xhr = $.ajax({
            url: uri,
            type: 'GET',
            success: function (response, status, xhr) {
                if (xhr === null || response === null) {
                    return;
                } else {
                    chatElems = $("<tr><td>" + escapeHtml(response) + "</td></tr>").appendTo("tbody");
                    chatElems[0].scrollIntoView();
                    requestForMessages(xhr.getResponseHeader('Location'));
                }
            }
        });
    } else {
        xhr = $.ajax({
            url: path,
            type: 'GET',
            success: function (response, status, xhr) {
                if (xhr === null || response === null) {
                    return;
                } else {
                    chatElems = $("<tr><td>" + escapeHtml(response) + "</td></tr>").appendTo("tbody");
                    chatElems[0].scrollIntoView();
                    requestForMessages(xhr.getResponseHeader('Location'));
                }
            }
        });
    }
}


var userName;
$(function () {
    if ($('#userName').length > 0)
        $('#userName').trigger("focus");

    if ($('#usermsg').length > 0) {
        $('#usermsg').trigger("focus");
        $("#usermsg").val("");
        userName = window.location.search.substring(6);
        requestForMessages();
    }

    $("#submitName").on("click", function (event) {
        userName = escapeHtml($("#userName").val());
        window.location.href = 'chatclient.xhtml?user=' + userName;
    });

    $("#exit").click(function (event) {
        $('tbody').html("");
        $('.welcome').html("Disconnected");
        $("#usermsg").val("");
        xhr.abort();
    });


    $('#postBtn').click(function (e) {
        var textSent = escapeHtml($("#usermsg").val());
        $("#usermsg").val("");
        sendChatText(textSent);
    });
});

function sendChatText(textSent) {
    $.ajax({
        url: path,
        type: 'POST',
        contentType: 'text/plain',
        data: userName + ":" + textSent,
        success: postFn
    });
}

function postFn(response, status, xhr) {

}

function escapeHtml(unsafe) {
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;")
        .replace(/:/g, ":&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp");
}


