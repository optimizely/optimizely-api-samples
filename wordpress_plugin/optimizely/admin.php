<?php

// PHP controller for plugin configuration page. The page itself is rendered in config.php

add_action( 'admin_menu', 'optimizely_admin_menu' );
  
optimizely_admin_warnings();

function optimizely_nonce_field($action = -1) { return wp_nonce_field($action); }
$optimizely_nonce = 'optimizely-update-code';

function optimizely_plugin_action_links( $links, $file ) {
  if ( $file == plugin_basename( dirname(__FILE__).'/optimizely.php' ) ) {
    $links[] = '<a href="admin.php?page=optimizely-config">'.__('Settings').'</a>';
  }

  return $links;
}

add_filter( 'plugin_action_links', 'optimizely_plugin_action_links', 10, 2 );

function optimizely_conf() {
  global $optimizely_nonce, $DEFAULT_VARIATION_TEMPLATE;


  if ( isset($_POST['submit']) ) {
    if ( function_exists('current_user_can') && !current_user_can('manage_options') )
      die(__('Cheatin&#8217; uh?'));

    check_admin_referer( $optimizely_nonce );

    $token = $_POST['token'];
    $project_id = $_POST['project_id'];
    $project_name = stripcslashes($_POST['project_name']);
    $project_code = stripcslashes($_POST['project_code']);
    $variation_template = stripcslashes($_POST['variation_template']);

    if ( empty($token) ) {
      delete_option('optimizely_token');
    } else {
      update_option('optimizely_token', $token);
    }

    if ( empty($project_id) ) {
      delete_option('optimizely_project_id');
    } else {
      update_option('optimizely_project_id', $project_id);
    }

    if ( empty($project_name) ) {
      delete_option('optimizely_project_name');
    } else {
      update_option('optimizely_project_name', $project_name);
    }

    if ( empty($project_code) ) {
      delete_option('optimizely_project_code');
    } else {
      update_option('optimizely_project_code', $project_code);
    }

    if ( empty($variation_template) ) {
      update_option('optimizely_variation_template', $DEFAULT_VARIATION_TEMPLATE);
    } else {
      update_option('optimizely_variation_template', $variation_template);
    }

    echo "<div id='message' class='updated fade'><p><strong>Settings saved.</strong></p></div>";

  }

  include(dirname( __FILE__ ) . '/config.php');

}

function optimizely_admin_warnings() {
  if ( !get_option('optimizely_project_code') && !isset($_POST['submit']) ) {
    function optimizely_warning() {
      echo "
      <div id='optimizely-warning' class='updated fade'><p><strong>".__('Optimizely is almost ready.')."</strong> ".sprintf(__('You must <a href="%1$s">authenticate and choose a project</a> to begin using Optimizely on your site.'), "admin.php?page=optimizely-config")."</p></div>";
    }
    add_action('admin_notices', 'optimizely_warning');
    return;
  } 
}

function optimizely_admin_menu() {
  add_submenu_page('plugins.php', __('Optimizely Configuration'), __('Optimizely Configuration'), 'manage_options', 'optimizely-config', 'optimizely_conf');
}
