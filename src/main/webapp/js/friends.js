/**
 * 友だち一覧の読み込み
 */
$(function () {
  $.ajax({
    url: '/weChat/Friends',
    type: 'POST',
    data: {}
  })
    .done((data) => {
      $('#res').html(data);
      console.log(data);
    })
    .fail((data) => {
      $('#res').html("読み込みに失敗しました。しばらくしてから再度お試しください。");
      console.error(data);
    });
});
