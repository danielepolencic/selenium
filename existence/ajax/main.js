document.querySelector('form').addEventListener('submit', function (event) {
  event.preventDefault();
  event.stopPropagation();

  var year = document.querySelector('input').value;

  var message = document.createElement('p');

  if (/[0-9]{4}/.test(year)) {
    message.className = "success";
    message.innerHTML = "The year is valid";
  } else {
    message.className = "failure"
    message.innerHTML = "The year is invalid";
  }

  document.querySelector('button').setAttribute('disabled', 'disabled');
  document.querySelector('.spinner').className = 'spinner';

  setTimeout(function () {
    document.querySelector('.spinner').className = 'spinner hide';
    document.querySelector('button').removeAttribute('disabled');
    document.querySelector('section').appendChild(message);
    setTimeout(function (message) {
      document.querySelector('section').removeChild(message);
    }.bind(null, message), 2000);
  }, 1000);

});
