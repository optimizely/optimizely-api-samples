/*
The OptimizelyAPI class provides a connection to the API via Javascript and lets you make authenticated calls without repeating yourself.

We store the API token in each instance of the object, and we can connect to multiple different accounts by creating new instances of the OptimizelyAPI class.
*/

var OptimizelyAPI = function(token) {
  this.token = token;
};

/*
To call the API, we use jQuery's `$.ajax` function, which sends an asynchronous request based on a set of `options`.

Our function takes two arguments:

* The `endpoint` to hit, like `projects/27`
* A `callback` function to run when the operation is done. The callback should take one argument, the `response`.

We construct the URL by appending the endpoint to the base API link, and we authenticate by adding the token in the headers section.

To send data, we set content type to JSON and encode the array as a JSON string to send over the wire.
*/

OptimizelyAPI.prototype.get = function(endpoint) {
  return jQuery.ajax({
    url: 'https://www.optimizelyapis.com/experiment/v1/' + endpoint,
    type: 'GET',
    headers: {'Token': this.token},
    error: function(jqXHR, textStatus, errorThrown) {
      alert("API call failed with status: " + jqXHR.status + ". Response is \"" + jqXHR.responseText + "\"");
    }
  });
};
