/*
 * Conditional Activation Examples
 * 
 * The 2 ways to use this code box are shown below as the 'Usage Options' 
 * There are several examples of these below
 */


/*
 * Usage Option #1 - Polling
 *
 */
[Code that evaluates to true/false, just like custom JS targeting]

/*
 * Usage Option #2 - Callback Function
 *
 * @param {Function} activate - Activate this experiment
 * @param {Object=} options {
 *                           isActive : Function indicating if this experiment is active
 *                           experimentId : This experiments Id
 *                          } 
 */
function(activate, options) { }



// EXAMPLES

/*
 * Condition: Activate when the green button DOM element appears
 * Type: Polling
 */
$('.greenButton').length


/*
 * Condition: Activate when the variable s.eVar33=product
 * Type: Polling
 */
window.s.eVar33.indexOf('product') != -1


/*
 * Condition: Activate based on specific eventID loaded after Optly
 * Type: Polling
 */
window.TM.Tracking.satellite.data.eventData.eid == '09004C81F112D8E8'


/*
 * Condition: Activate when a button is clicked to trigger #lightbox
 * Type: Callback function
 */
function(activate, options) {
  $('html').delegate('#btn', 'mousedown', function() {
      activate();
  });
}


/*
 * Condition: Activate when an Ajax load contains my element
 * Type: Callback function
 */
function(activate, options) {
  $( document ).ajaxComplete(function( event, xhr, settings ) {
    if ( xhr.responseText.indexOf('rightRailModule') != -1 ) {
      activate();
    }
  });
} 


/*
 * Condition: Activate when angular changes page to a product page (first time only)
 * Type: Callback function
 */
function(activate, options) {
  $scope.$on('$locationChangeSuccess', function(event, next, current) {
    if (next.indexOf('productPage') != -1) {
      if (!options.isActive) {
        activate(); 
      }
    }
  });
}
