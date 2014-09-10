// Javascript for Results API page

function optimizelyConfigPage() {
  var $ = jQuery;

  /*

  AUTHENTICATION W/ OPTIMIZELY

  When the user presses the button, we call the GET projects/ endpoint to list out all the projects in their account. For each project, we show its name in the dropdown and store its ID in the value attribute for submission to a form.

  */

  $('button#connect_optimizely').click(function(event) {
    var optly = new OptimizelyAPI($('#token').val());
    $('#project_id').html('<option>Loading projects...</option>');

    optly.get('projects').then(function(response) {
      $('#project_id').empty();

      $.each(response, function(key, val) {
        $('#project_id').append('<option value="' + val.id + '">' + val.project_name + '</option>'); 
      });
    });
    
    $('button#generate_results').removeAttr('disabled');
  });

  /*

  GENERATE RESULTS

  When the user presses the button, we call the GET experiments/ endpoint to get all the experiments in the project. For each experiment we then find results and push them into a CSV file which is then exported. 

  */

  $('button#generate_results').click(function(event) {
    var projectId = $('#project_id').val(),
        projectName = $('#project_id :selected').text(),
        experimentListEndpoint = 'projects/' + projectId + '/experiments',
        optly = new OptimizelyAPI($('#token').val()),
        // Headings for CSV 
        csvContent = 'Experiment ID, Variation Name, Variation ID, Baseline ID, Goal Name, Goal ID, Visitors, Chance To Beat Baseline, Improvement, Is Revenue, Begin Time, End Time, Conversions, Conversion Rate, Status\n';
    
    optly.get(experimentListEndpoint).then(function(response) {
      var deferreds = [];
      var exp_count = 0;
      var exp_limit = 10;

      $('button#generate_results').text('Downloading...');
      $('button#generate_results').attr('disabled', 'disabled');
 
      $.each(response, function(index, exp) {
        if (exp_count == exp_limit) {
          return false;
        } 
        if (exp.status === 'Archived') {
          return;
        }

        var resultEndpoint = 'experiments/' + exp.id + '/results';

        // Collect the deferred object to keep track of each API call's result
        var deferred = optly.get(resultEndpoint).then(function(experimentResult) {
          $.each(experimentResult, function(index, result) {
            csvContent += exp.id + ', ';
            var results = [];
            var fields = ['variation_name', 'variation_id', 'baseline_id', 'goal_name', 'goal_id', 'visitors', 'confidence', 'improvement', 'is_revenue', 'begin_time', 'end_time', 'conversions', 'conversion_rate', 'status'];

            for (idx = 0; idx < fields.length; idx++) {
              results.push(result[fields[idx]]);
            }

            csvContent += results.join(', ');
            csvContent += '\n'
          });          
        });

        deferreds.push(deferred);
        exp_count++;
      });

      // Export all experiment results into a CSV file after receiving those from all Result API calls 
      $.when.apply($, deferreds).then(function() {
        // Add a link used later to trigger download of a file
        var downloadLink = document.createElement('a'),
            csv = 'data:application/csv;charset=utf-8,' + encodeURIComponent(csvContent),
            hidden = document.getElementById('hidden');

        // Configure the download link
        downloadLink.setAttribute('href', csv);
        downloadLink.setAttribute('download', projectName + '_results.csv');
        downloadLink.setAttribute('id', 'csv_download');
        hidden.appendChild(downloadLink)

        // Click on the link and then remove it
        downloadLink.click();
        hidden.removeChild(downloadLink);
        $('button#generate_results').text('Download Results');
        $('button#generate_results').removeAttr('disabled');
      });
    });
  });
}
