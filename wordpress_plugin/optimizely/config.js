// Javascript for plugin settings page

function optimizelyConfigPage() {
  var $ = jQuery;

  /*

  AUTHENTICATION W/ OPTIMIZELY

  When the user presses the button, we call the GET projects/ endpoint to list out all the projects in their account. For each project, we show its name in the dropdown and store its ID in the value attribute for submission to a form.

  */

  $("button#connect_optimizely").click(function(event) {
    event.preventDefault();
    $("#project_id").html("<option>Loading projects...</option>");
    
    optly = new OptimizelyAPI($("#token").val());

    optly.get('projects', function(response) {
      $("#project_id").empty();

      $.each(response, function(key, val) {
        $("#project_id").append("<option value='" + val.id + "'>" + val.project_name + "</option>"); 
      });
      
      $("#project_id").change(); // update project code w/ the default value
    });

  /*

  CHOOSING A PROJECT

  When the user selects a project from the dropdown, we populate the project code box with the Optimizely snippet for that project ID.

  */
  $('#project_id').change(function() {
    var id = $('#project_id').val();
    var name = $('#project_id option:selected').text();
    var project_code = '<script src="//cdn.optimizely.com/js/' + id + '.js"></script>';
    $('#project_code').text(project_code);
    $('#project_name').val(name);
  });


  });
}