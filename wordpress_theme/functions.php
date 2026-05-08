<?php
/**
 * LIVEPLUS Theme Functions
 */

function liveplus_theme_setup() {
    // Add support for Featured Images
    add_theme_support('post-thumbnails');
    
    // Add support for Title Tag
    add_theme_support('title-tag');

    // Register Menus
    register_nav_menus(array(
        'primary' => __('Primary Menu', 'liveplus'),
    ));
}
add_action('after_setup_theme', 'liveplus_theme_setup');

function liveplus_enqueue_styles() {
    // Enqueue Google Font Cairo
    wp_enqueue_style('cairo-font', 'https://fonts.googleapis.com/css2?family=Cairo:wght@400;700;900&display=swap');
    
    // Main Style
    wp_enqueue_style('liveplus-main-style', get_stylesheet_uri());
}
add_action('wp_enqueue_scripts', 'liveplus_enqueue_styles');

// Custom Function to get TMDB Data (Placeholder)
// You can call this to automatically fill movie details
function fetch_movie_from_tmdb($tmdb_id) {
    $api_key = 'YOUR_API_KEY_HERE';
    $url = "https://api.themoviedb.org/3/movie/{$tmdb_id}?api_key={$api_key}&language=ar";
    
    $response = wp_remote_get($url);
    if (is_wp_error($response)) return false;
    
    return json_decode(wp_remote_retrieve_body($response), true);
}

// Redirect search results to specific template if needed
function liveplus_search_template($template) {
    if (is_search()) {
        $new_template = locate_template(array('archive.php'));
        if ('' != $new_template) return $new_template;
    }
    return $template;
}
add_filter('template_include', 'liveplus_search_template');
