<?php
/**
 * Plugin Name: LIVEPLUS Core
 * Description: Core functionality for the LIVEPLUS Movie/Series/Anime platform. Registers post types, categories, and server fields.
 * Version: 1.0
 * Author: Antigravity AI
 */

if (!defined('ABSPATH')) exit;

// 1. Register Custom Post Types
function liveplus_register_post_types() {
    $types = array(
        'movie'  => array('singular' => 'Movie', 'plural' => 'Movies', 'icon' => 'dashicons-video-alt3'),
        'series' => array('singular' => 'Series', 'plural' => 'TV Series', 'icon' => 'dashicons-welcome-view-site'),
        'anime'  => array('singular' => 'Anime', 'plural' => 'Anime', 'icon' => 'dashicons-art'),
    );

    foreach ($types as $slug => $data) {
        register_post_type($slug, array(
            'labels' => array(
                'name' => $data['plural'],
                'singular_name' => $data['singular'],
            ),
            'public' => true,
            'has_archive' => true,
            'supports' => array('title', 'editor', 'thumbnail', 'excerpt'),
            'menu_icon' => $data['icon'],
            'show_in_rest' => true, // Enable Gutenberg and API
        ));
    }
}
add_action('init', 'liveplus_register_post_types');

// 2. Register Genre Taxonomy (Action, Fantasy, Comedy, Horror)
function liveplus_register_taxonomies() {
    register_taxonomy('movie_genre', array('movie', 'series', 'anime'), array(
        'labels' => array('name' => 'Genres', 'singular_name' => 'Genre'),
        'hierarchical' => true,
        'show_admin_column' => true,
        'show_in_rest' => true,
    ));

    // Pre-insert requested categories if they don't exist
    $genres = array('Action', 'Fantasy', 'Comedy', 'Horror');
    foreach ($genres as $genre) {
        if (!term_exists($genre, 'movie_genre')) {
            wp_insert_term($genre, 'movie_genre');
        }
    }
}
add_action('init', 'liveplus_register_taxonomies');

// 3. Add Server Data Meta Boxes (بينات السيرفر)
function liveplus_add_server_meta_boxes() {
    add_meta_box('liveplus_server_info', 'Server Information (بينات السيرفر)', 'liveplus_server_callback', array('movie', 'series', 'anime'), 'normal', 'high');
}
add_action('add_meta_boxes', 'liveplus_add_server_meta_boxes');

function liveplus_server_callback($post) {
    $server_1 = get_post_meta($post->ID, '_liveplus_server_1', true);
    $server_2 = get_post_meta($post->ID, '_liveplus_server_2', true);
    ?>
    <p>
        <label>Server 1 (Primary Link):</label><br>
        <input type="text" name="liveplus_server_1" value="<?php echo esc_attr($server_1); ?>" style="width:100%">
    </p>
    <p>
        <label>Server 2 (Backup/Embed):</label><br>
        <input type="text" name="liveplus_server_2" value="<?php echo esc_attr($server_2); ?>" style="width:100%">
    </p>
    <?php
}

function liveplus_save_server_meta($post_id) {
    if (isset($_POST['liveplus_server_1'])) update_post_meta($post_id, '_liveplus_server_1', $_POST['liveplus_server_1']);
    if (isset($_POST['liveplus_server_2'])) update_post_meta($post_id, '_liveplus_server_2', $_POST['liveplus_server_2']);
}
add_action('save_post', 'liveplus_save_server_meta');
