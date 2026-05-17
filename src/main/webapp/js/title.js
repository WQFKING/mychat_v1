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
                <span class="welcome-text">ようこそ、<strong>${userEmail}</strong></span>
            </div>
            <div class="nav-actions">
                <a href="${base}my.html">プロフィール</a>
                <a href="${base}myFriendAdd.html">友だち追加</a>
                <a href="${base}myFriends.html">友だち一覧</a>
                <a href="${base}friendGroupMessageAdd.html">投稿する</a>
                <a href="${base}friendGroupMessage.html">投稿一覧</a>
                <a href="#" id="logout-btn" class="logout-link">ログアウト</a>
            </div>
        `);
    } else {
        $container.html(`
            <a href="${base}login.html">ログイン</a>
            <a href="${base}add.html" class="btn-register">新規登録</a>
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
        if (!confirm("ログアウトしますか？")) return;

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
