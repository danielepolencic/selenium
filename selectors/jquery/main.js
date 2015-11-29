$('button').on('click', function () {
  $.ajax('http://jsonplaceholder.typicode.com/posts').then(function (posts) {
    var html = posts.map(function (post) {
      return $('<li>').append($('<h1>').text(post.title), $('<p>').text(post.body));
    });
    debugger;
    $('body').append(html);
  });
});
