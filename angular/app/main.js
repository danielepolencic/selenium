angular
  .module('selenium', [])
  .controller('Controller', function ($http) {
    this.posts = [];
    this.start = function () {
      $http.get('http://jsonplaceholder.typicode.com/posts').then(function (response) {
        this.posts = response.data;
      }.bind(this));
    }
  });
