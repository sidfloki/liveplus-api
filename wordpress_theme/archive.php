<?php get_header(); ?>

<main class="site-main">
    <div class="container" style="padding-top: 120px;">
        <header class="archive-header" style="margin-bottom: 40px; border-right: 5px solid var(--primary); padding-right: 20px;">
            <h1 style="font-size: 2.5rem;"><?php the_archive_title(); ?></h1>
            <p style="color: #777;"><?php the_archive_description(); ?></p>
        </header>

        <div class="movie-grid">
            <?php if (have_posts()) : while (have_posts()) : the_post(); ?>
                <article class="movie-card">
                    <a href="<?php the_permalink(); ?>" style="text-decoration: none; color: inherit;">
                        <?php if (has_post_thumbnail()) : ?>
                            <?php the_post_thumbnail('medium'); ?>
                        <?php else : ?>
                            <img src="https://via.placeholder.com/200x300/1a1a1a/ffffff?text=No+Poster" alt="No Thumbnail">
                        <?php endif; ?>
                        <div class="info">
                            <h3 style="font-size: 1rem; margin-top: 10px;"><?php the_title(); ?></h3>
                        </div>
                    </a>
                </article>
            <?php endwhile; else : ?>
                <p>لا توجد نتائج في هذا التصنيف.</p>
            <?php endif; ?>
        </div>
        
        <div class="pagination" style="margin-top: 50px; text-align: center;">
            <?php the_posts_pagination(); ?>
        </div>
    </div>
</main>

<?php get_footer(); ?>
