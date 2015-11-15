document.querySelector('form').addEventListener('submit', function (event) {
  event.preventDefault();
  event.stopPropagation();

  var year = document.querySelector('input').value;

  var message = document.createElement('p');

  message.className = "timeout";
  message.innerHTML = "Operation timed out";

  document.querySelector('button').setAttribute('disabled', 'disabled');
  document.querySelector('.spinner').className = 'spinner';

  setTimeout(function () {
    document.querySelector('.spinner').className = 'spinner hide';
    document.querySelector('button').removeAttribute('disabled');
    document.querySelector('section').appendChild(message);
    setTimeout(function (message) {
      document.querySelector('section').removeChild(message);
    }.bind(null, message), 2000);
  }, 5000);

});
