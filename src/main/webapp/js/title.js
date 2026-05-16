function getBasePath() {
    const normalized = (window.location.pathname || "").replace(/\\/g, "/");
    const parts = normalized.split("/").filter(Boolean);
    return parts.includes("teach") ? "../" : "";
}

function renderNav() {
    const userEmail = localStorage.getItem('email');
    const $container = $('#nav-links');
    const base = getBasePath();

    if (userEmail && userEmail !== 'undefined') {
        $container.html(`
            <div class="user-info">
                <span class="welcome-text">欢迎回来，<strong>${userEmail}</strong></span>
            </div>
            <div class="nav-actions">
                <a href="${base}my.html">个人资料</a>
                <a href="${base}myFriendAdd.html">添加好友</a>
                <a href="${base}myFriends.html">好友列表</a>
                <a href="${base}friendGroupMessageAdd.html">发布群消息</a>
                <a href="${base}friendGroupMessage.html">群消息列表</a>
                <a href="#" id="logout-btn" class="logout-link">退出登录</a>
            </div>
        `);
    } else {
        $container.html(`
            <a href="${base}login.html">登录</a>
            <a href="${base}add.html" class="btn-register">注册新账号</a>
        `);
    }
}

$(function () {
    const base = getBasePath();
    const headerHtml = `
        <nav class="main-nav" aria-label="main-navigation">
            <div class="nav-left">
                <a href="${base}myFriends.html" class="logo">MyChat</a>
            </div>
            <div class="nav-right" id="nav-links"></div>
        </nav>
    `;

    $('body').prepend(headerHtml);

    $(document).on('click', '#logout-btn', function (e) {
        e.preventDefault();
        if (!confirm("确定要退出登录吗？")) return;

        localStorage.removeItem('email');
        sessionStorage.removeItem('email');
        sessionStorage.removeItem('isLoggedIn');

        $.post(base + 'logout.php', function () {
            window.location.href = base + 'login.html';
        }).fail(function () {
            window.location.href = base + 'login.html';
        });
    });

    renderNav();
});
