<?php

/*

HEADLINE INPUT BOX

When users go to write their posts, they'll see a new section for A/B testing headlines. This section will include inputs for users to write alternate headlines and a button to create the experiment.

We also use several hidden input fields to store data about the project and experiment. These are used in edit.js to send AJAX requests to the Optimizely API.

*/

// The number of variations to show in the box
define("NUM_VARIATIONS", 2);

function title_variations_render($post) {

	$titles = array();
	$contents = "";

	for ($i = 1; $i <= NUM_VARIATIONS; $i++) {

		$titles[$i] = get_post_meta( $post->ID, "post_title$i", true);
		$contents .= "<p>";
		$contents .= "<label for='$key'>Variation #$i</label>";
		$contents .= "<input type='text' name='post_title$i' id='post_title$i' class='optimizely_variation' placeholder='Title $i' value='$titles[$i]'>";
		$contents .= "</p>";
	}

	if ( can_create_experiments() ) {
		echo $contents;

		?>
		<div id="optimizely_not_created">
			<a id="optimizely_create" class="button-primary">Create Experiment</a>
		</div>
		<div id="optimizely_created">
			<a id="optimizely_toggle_running" class="button-primary">Start Experiment</a>	
			<p></p>
			<a id="optimizely_view" class="button" target="_blank">View on Optimizely</a>
			<p>Status: <b id="optimizely_experiment_status_text"><?= get_post_meta($post->ID, 'optimizely_experiment_status', true); ?></b>
			<br />
			Results: <a id="optimizely_results" target="_blank">View Results</a></p>
		</div>
		<input type="hidden" id="optimizely_token" value="<?= get_option('optimizely_token'); ?>" />
		<input type="hidden" id="optimizely_project_id" value="<?= get_option('optimizely_project_id'); ?>" />
		<input type="hidden" id="optimizely_experiment_id" name="optimizely_experiment_id" value="<?= get_post_meta($post->ID, 'optimizely_experiment_id', true); ?>" />
		<input type="hidden" id="optimizely_experiment_status" name="optimizely_experiment_status" value="<?= get_post_meta($post->ID, 'optimizely_experiment_status', true); ?>" />
		<textarea id="optimizely_variation_template" style="display: none"><?= get_option('optimizely_variation_template') ?></textarea>

		<script type="text/javascript">
		optimizelyEditPage();
		</script>
		<?php

	} else {
		?>
		<p>Please configure your API credentials in the <a href="<?php menu_page_url('optimizely-config'); ?>">Optimizely settings page</a>.</p>
		<?php
	}

}

add_action( 'add_meta_boxes', 'title_variations_add' );
function title_variations_add()
{
    add_meta_box('optimizely-headlines', 'A/B Test Headlines', title_variations_render, 'post', 'side', 'high');
}

add_action( 'save_post', 'title_variations_save' );
function title_variations_save($post_id)
{
	if( !current_user_can( 'edit_post' ) ) return;

	for ($i = 1; $i <= NUM_VARIATIONS; $i++) {

	    $key = "post_title$i";
	    if( isset( $_POST[$key] ) ) {
	        // Save titles
	        $new_title = esc_attr($_POST[$key]);
	        update_post_meta( $post_id, $key, $new_title);
	    }
	}

	if( isset( $_POST["optimizely_experiment_id"] ) ) {	
		update_post_meta( $post_id, "optimizely_experiment_id", $_POST["optimizely_experiment_id"]);
		update_post_meta( $post_id, "optimizely_experiment_status", $_POST["optimizely_experiment_status"]);
	}

}

add_action("wp_ajax_update_experiment_meta", "update_experiment_meta");
function update_experiment_meta()
{
	$post_id = $_REQUEST["post_id"];
	title_variations_save($post_id);
}

?>