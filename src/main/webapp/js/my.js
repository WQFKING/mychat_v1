/**
 * プロフィールの読み込み/保存
 */

$(function () {
    $.ajax({
        url: '/wechat/My',
        type: 'POST',
        dataType: 'json',
        data: {}
    })
        .done((res) => {
            if (res.status == "success") {
                const str = res.message.split("/");
                $("#email").val(str[0]);
                $("#password").val(str[1]);
            } else {
                alert("ログインに失敗しました。しばらくしてから再度お試しください。");
                setTimeout(function () {
                    window.location.href = "login.html";
                }, 5000);
            }
        });
});

function save() {
    $.ajax({
        url: '/wechat/UpdateMy',
        type: 'POST',
        data: {
            'new_email': $('#email').val(),
            'new_password': $('#password').val()
        }
    })
        .done((data) => {
            $('#res').html(data);
        });
}
