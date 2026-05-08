<?php get_header(); ?>

<main class="site-main">
    <?php while (have_posts()) : the_post(); 
        $server1 = get_post_meta(get_the_ID(), '_liveplus_server_1', true);
        $server2 = get_post_meta(get_the_ID(), '_liveplus_server_2', true);
    ?>
    <div class="container" style="padding-top: 120px;">
        
        <!-- Player Section -->
        <div class="player-container" id="player-area">
            <?php if ($server1) : ?>
                <iframe src="<?php echo esc_url($server1); ?>" frameborder="0" allowfullscreen style="width: 100%; height: 100%;"></iframe>
            <?php else : ?>
                <div style="display: flex; align-items: center; justify-content: center; height: 100%; background: #222;">
                    <p>يرجى إضافة رابط السيرفر من لوحة التحكم.</p>
                </div>
            <?php endif; ?>
        </div>

        <!-- Server Switching -->
        <div class="server-tabs">
            <?php if ($server1) : ?>
                <button class="server-btn active" onclick="switchServer('<?php echo esc_url($server1); ?>', this)">سيرفر 1</button>
            <?php endif; ?>
            <?php if ($server2) : ?>
                <button class="server-btn" onclick="switchServer('<?php echo esc_url($server2); ?>', this)">سيرفر 2</button>
            <?php endif; ?>
        </div>

        <!-- Movie Details -->
        <div class="movie-content" style="margin-top: 40px; display: flex; gap: 30px;">
            <div class="poster" style="width: 250px; flex-shrink: 0;">
                <?php if (has_post_thumbnail()) : ?>
                    <?php the_post_thumbnail('large', array('style' => 'width:100%; border-radius:10px;')); ?>
                <?php endif; ?>
            </div>
            <div class="details">
                <h1 style="font-size: 2.5rem; margin-bottom: 20px;"><?php the_title(); ?></h1>
                <div class="meta" style="color: var(--accent); margin-bottom: 20px;">
                    <?php echo get_the_term_list(get_the_ID(), 'movie_genre', 'التصنيفات: ', ' ، '); ?>
                </div>
                <div class="description" style="line-height: 1.8; color: #ccc;">
                    <?php the_content(); ?>
                </div>
            </div>
        </div>

    </div>
    <?php endwhile; ?>
</main>

<script>
function switchServer(url, btn) {
    const iframe = document.querySelector('#player-area iframe');
    if (iframe) {
        iframe.src = url;
    }
    // Update active button
    document.querySelectorAll('.server-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
}
</script>

<?php get_footer(); ?>
