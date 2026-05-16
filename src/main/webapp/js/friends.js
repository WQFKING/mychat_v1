/**
 * 好友列表页面的数据加载
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
      $('#res').html("加载失败，请稍后重试。");
      console.error(data);
    });
});
