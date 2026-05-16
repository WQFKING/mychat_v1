/**
 * 个人资料页面的数据加载与保存
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
                alert("登录失败，请稍后重试。");
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
