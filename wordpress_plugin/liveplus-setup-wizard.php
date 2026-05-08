<?php
/**
 * Plugin Name: LIVEPLUS Setup Wizard
 * Description: One-click setup for your Movie/Anime platform. Configures categories, pages, and TMDB integration.
 * Version: 1.1
 * Author: Antigravity AI
 */

if (!defined('ABSPATH')) exit;

// 1. Run Setup on Activation
register_activation_hook(__FILE__, 'liveplus_run_setup');

function liveplus_run_setup() {
    // A. Create Categories
    $genres = array('Action', 'Fantasy', 'Comedy', 'Horror', 'Sci-Fi', 'Anime');
    foreach ($genres as $genre) {
        if (!term_exists($genre, 'movie_genre')) {
            wp_insert_term($genre, 'movie_genre');
        }
    }

    // B. Create Essential Pages
    $pages = array(
        'Home' => '[liveplus_home_grid]',
        'Movies' => '',
        'Series' => '',
        'Anime' => '',
    );

    foreach ($pages as $title => $content) {
        if (!get_page_by_title($title)) {
            wp_insert_post(array(
                'post_title'   => $title,
                'post_content' => $content,
                'post_status'  => 'publish',
                'post_type'    => 'page',
            ));
        }
    }

    // C. Set Permalinks to Post Name (Crucial for SEO)
    global $wp_rewrite;
    $wp_rewrite->set_permalink_structure('/%postname%/');
    flush_rewrite_rules();
}

// 2. TMDB Auto Importer Logic
add_action('wp_ajax_import_tmdb_movie', 'liveplus_ajax_import_tmdb');

function liveplus_ajax_import_tmdb() {
    $tmdb_id = $_POST['tmdb_id'];
    $api_key = 'YOUR_TMDB_API_KEY'; // User needs to input this
    
    $url = "https://api.themoviedb.org/3/movie/{$tmdb_id}?api_key={$api_key}&language=ar";
    $response = wp_remote_get($url);
    
    if (is_wp_error($response)) wp_send_json_error('Connection failed');
    
    $data = json_decode(wp_remote_retrieve_body($response), true);
    
    // Create the post
    $post_id = wp_insert_post(array(
        'post_title'   => $data['title'],
        'post_content' => $data['overview'],
        'post_status'  => 'publish',
        'post_type'    => 'movie',
    ));
    
    // Set Genre (Dummy logic for example)
    wp_set_object_terms($post_id, 'Action', 'movie_genre');
    
    wp_send_json_success(array('post_id' => $post_id));
}

// 3. Admin Menu for Easy Control
add_action('admin_menu', 'liveplus_wizard_menu');
function liveplus_wizard_menu() {
    add_menu_page('LIVEPLUS Wizard', 'LIVEPLUS Wizard', 'manage_options', 'liveplus-wizard', 'liveplus_wizard_page', 'dashicons-admin-tools');
}

function liveplus_wizard_page() {
    ?>
    <div class="wrap" style="background: #fff; padding: 30px; border-radius: 10px; margin-top: 20px;">
        <h1>🎥 LIVEPLUS Setup Wizard</h1>
        <p>مرحباً بك! هذا المعالج سيساعدك على بناء موقعك في ثوانٍ.</p>
        <hr>
        <h3>1. جلب بيانات فيلم تلقائياً</h3>
        <input type="text" id="tmdb_id" placeholder="أدخل رقم الفيلم من TMDB (مثلاً: 550)">
        <button class="button button-primary" onclick="importMovie()">ابدأ الجلب</button>
        
        <script>
        function importMovie() {
            let id = document.getElementById('tmdb_id').value;
            alert('بدأ جلب الفيلم رقم: ' + id + '... (هذه الميزة تتطلب API Key)');
        }
        </script>
    </div>
    <?php
}
